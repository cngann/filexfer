package com.circron.filexfer;

import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") public class SendFile {
    protected FileTransferConfig fileTransferConfig = FileTransferConfig.getInstance();
    protected Socket socket;
    Logger logger = Utils.getLogger(this.getClass());

    public SendFile() throws IOException {
        this.socket = new Socket(fileTransferConfig.getDestinationAddress(), fileTransferConfig.getPort());
    }

    public void send(String file) throws IOException {
        send(new File(file));
    }

    public void send(File file) throws IOException {
        send(new ArrayList<>(Collections.singletonList(file)));
    }

    public void send(List<File> sendFiles) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        List<File> files = Utils.getFilesWithDirs(sendFiles);
        logger.debug("Number of files to transfer: " + files.size());
        dataOutputStream.writeInt(files.size());
        dataOutputStream.flush();
        int length;
        byte[] bytes = new byte[fileTransferConfig.getStreamBufferLength()];
        for (File file : files) {
            boolean isDirectory = file.isDirectory();
            boolean isEncrypted = fileTransferConfig.isEncrypted();
            if (isEncrypted) {
                file = handleEncryption(file);
            }
            logger.debug("Sending " + (isDirectory ? "directory" : "file") + ": " + file.getPath());
            dataOutputStream.writeUTF(file.getPath());
            dataOutputStream.writeBoolean(isDirectory);
            dataOutputStream.writeBoolean(isEncrypted);
            dataOutputStream.writeLong(file.length());
            dataOutputStream.flush();
            if (file.isDirectory()) continue;
            FileInputStream fileInputStream = new FileInputStream(file);
            while ((length = fileInputStream.read(bytes)) != -1) {
                dataOutputStream.write(bytes, 0, length);
                dataOutputStream.flush();
            }
            if (isEncrypted) {
                cleanUpTempFile(file);
            }
        }
        dataOutputStream.close();
    }

    public void cleanUpTempFile(File file) {
        boolean deleted = file.delete();
        if (deleted) {
            logger.debug("Cleaning up encrypted temporary file");
        } else {
            logger.warn("Could not clean up encrypted temporary file");
        }
    }

    private File handleEncryption(File file) {
        try {
            file = FileEncrypt.encryptFile(file);
        } catch (Exception e) {
            logger.error("Could not encrypt file " + file.getName() + ": " + e.getMessage());
        }
        return file;
    }
}
