package com.circron.filexfer;

import org.apache.commons.logging.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused") public class SendFile {
    private final FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;
    private final Socket socket;
    private final Log logger = Utils.getLogger(this.getClass());

    public SendFile() throws IOException {
        this.socket = Utils.getClientSocket(fileTransferConfig.getDestinationAddress(), fileTransferConfig.getPort());
    }

    public void send(String file) throws IOException {
        send(new File(file));
    }

    public void send(File file) throws IOException {
        send(new FileTransferFile(file));
    }

    public void send(FileTransferFile file) throws IOException {
        send(new ArrayList<>(Collections.singletonList(file)));
    }

    public void send(List<FileTransferFile> sendFiles) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        List<FileTransferFile> files = Utils.getFilesWithDirs(sendFiles);
        logger.debug("Number of files to transfer: " + files.size());
        objectOutputStream.writeInt(files.size());
        objectOutputStream.flush();
        int length;
        byte[] bytes = new byte[fileTransferConfig.getStreamBufferLength()];
        for (FileTransferFile fileTransferFile : files) {
            boolean isDirectory = fileTransferFile.isDirectory();
            logger.debug("Sending " + (isDirectory ? "directory" : "file") + ": " + fileTransferFile.getNormalizedFilename());
            objectOutputStream.writeObject(fileTransferFile);
            objectOutputStream.flush();
            if (fileTransferFile.isDirectory()) continue;
            FileInputStream fileInputStream = new FileInputStream(fileTransferFile.getFile());
            while ((length = fileInputStream.read(bytes)) != -1) {
                objectOutputStream.write(bytes, 0, length);
                objectOutputStream.flush();
            }
        }
        objectOutputStream.close();
    }

    public void cleanUpTempFile(File file) {
        boolean deleted = file.delete();
        if (deleted) {
            logger.debug("Cleaning up encrypted temporary file");
        } else {
            logger.warn("Could not clean up encrypted temporary file");
        }
    }
}
