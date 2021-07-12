package com.circron.filexfer;

import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SendFile {
    protected FileServerConfig fileServerConfig;
    protected Socket socket;
    Logger logger = Utils.getLogger(this.getClass());

    public SendFile(FileServerConfig fileServerConfig) throws IOException {
        this.fileServerConfig = fileServerConfig;
        this.socket = new Socket(InetAddress.getLoopbackAddress(), fileServerConfig.getPort());
    }

    public void send(String file) {
        send(new File(file));
    }

    public void send(File file) {
        List<File> files = new ArrayList<>();
        if (file.isDirectory()) {
            try {
                files.addAll(Utils.getFilesFromDir(file));
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage());
            }
        } else {
            files.add(file);
        }
        send(files);
    }

    public void send(List<File> files) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            files = Utils.getFilesWithDirs(files, fileServerConfig.isRecurseIntoDirectory());
            logger.debug("Number of files to transfer: " + files.size());
            dataOutputStream.writeInt(files.size());
            dataOutputStream.flush();
            int length;
            byte[] bytes = new byte[fileServerConfig.getStreamBufferLength()];
            for (File file : files) {
                boolean isDirectory = file.isDirectory();
                if (fileServerConfig.isEncrypted()) {
                    try {
                        file = FileEncrypt.encryptFile(file, fileServerConfig);
                    } catch (Exception e) {
                        logger.error("Could not encrypt file " + file.getName());
                    }
                }
                logger.debug("Sending " + (isDirectory ? "directory" : "file") + ": " + file.getPath());
                dataOutputStream.writeUTF(file.getPath());
                dataOutputStream.writeBoolean(isDirectory);
                dataOutputStream.writeLong(file.length());
                dataOutputStream.flush();
                if (!file.isDirectory()) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    while ((length = fileInputStream.read(bytes)) != -1) {
                        dataOutputStream.write(bytes, 0, length);
                        dataOutputStream.flush();
                    }
                }
            }
            dataOutputStream.close();
        } catch (IOException e) {
            logger.error("IOException");
            e.printStackTrace();
        }
    }
}
