package com.circron.filexfer;

import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ReceiveFile implements Runnable {
    Socket socket;
    FileServerConfig fileServerConfig;
    Logger logger = Utils.getLogger(this.getClass());

    public ReceiveFile(Socket socket, FileServerConfig fileServerConfig) {
        this.socket = socket;
        this.fileServerConfig = fileServerConfig;
    }

    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            int number = dataInputStream.readInt();
            int length;
            logger.debug("Number of Files to be received: " + number);
            for (int i = 0; i < number; i++) {
                byte[] buf = new byte[fileServerConfig.getStreamBufferLength()];
                String filename = Utils.getFilePath(fileServerConfig.getToPath(), dataInputStream.readUTF());
                boolean isDirectory = dataInputStream.readBoolean();
                long fileSize = dataInputStream.readLong();
                logger.debug("Receiving " + (isDirectory ? "directory" : "file") + ": " + filename);
                logger.debug("Receiving Size: " + fileSize);
                logger.debug("Is directory: " + isDirectory);
                if (!isDirectory) {
                    FileOutputStream fos = new FileOutputStream(filename);
                    while (fileSize > 0 && (length = dataInputStream.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1) {
                        fos.write(buf, 0, length);
                        fileSize -= length;
                    }
                    fos.close();
                    if (fileServerConfig.isEncrypted()) {
                        try {
                            FileDecrypt.decryptFile(new File(filename), fileServerConfig);
                        } catch (Exception e) {
                            logger.error("Could not decrypt file\n" + e.getMessage());
                        }
                    }
                } else {
                    boolean dirExists = new File(filename).mkdirs();
                    if (dirExists) {
                        logger.info("Directory already exists");
                    }
                }
            }
        } catch (IOException e) {
            logger.error("IOException");
            e.printStackTrace();
        }
    }
}
