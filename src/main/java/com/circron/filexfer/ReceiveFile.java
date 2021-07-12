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
    FileTransferConfig fileTransferConfig = FileTransferConfig.getInstance();
    Logger logger = Utils.getLogger(this.getClass());

    public ReceiveFile(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            int number = dataInputStream.readInt();
            logger.debug("Number of Files to be received: " + number);
            for (int i = 0; i < number; i++) {
                String filename = Utils.getFilePath(fileTransferConfig.getToPath(), dataInputStream.readUTF());
                boolean isDirectory = dataInputStream.readBoolean();
                boolean isEncrypted = dataInputStream.readBoolean();
                long fileSize = dataInputStream.readLong();
                logger.debug("Receiving " + (isDirectory ? "directory" : "file") + ": " + filename);
                logger.debug("Receiving Size: " + fileSize);
                logger.debug("Is directory: " + isDirectory);
                logger.debug("Is encrypted: " + isEncrypted);
                if (!isDirectory) {
                    writeFile(filename, fileSize, dataInputStream);
                    if (fileTransferConfig.isEncrypted()) {
                        handleEncryptedFile(filename);
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

    public void handleEncryptedFile(String filename) {
        try {
            FileDecrypt.decryptFile(new File(filename));
            boolean deleted = new File(filename).delete();
            if (deleted) {
                logger.debug("Original encrypted file (" + filename + ") has been deleted");
            }
        } catch (Exception e) {
            logger.error("Could not decrypt file\n" + e.getMessage());
        }
    }

    public void writeFile(String filename, long fileSize, DataInputStream dataInputStream) throws IOException {
        int length;
        byte[] buf = new byte[fileTransferConfig.getStreamBufferLength()];
        FileOutputStream fos = new FileOutputStream(filename);
        logger.error("File not found: " + filename);
        while (fileSize > 0 && (length = dataInputStream.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1) {
            fos.write(buf, 0, length);
            fileSize -= length;
        }
        fos.close();
    }
}
