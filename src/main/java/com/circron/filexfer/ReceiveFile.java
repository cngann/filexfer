package com.circron.filexfer;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

@SuppressWarnings("unused") public class ReceiveFile implements Runnable {
    private final Socket socket;
    private final Logger logger = Utils.getLogger(this.getClass());
    FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;

    public ReceiveFile(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            int number = objectInputStream.readInt();
            logger.debug("Number of Files to be received: " + number);
            for (int i = 0; i < number; i++) {
                FileTransferFile file = (FileTransferFile)objectInputStream.readObject();
                String filename = Utils.getFilePath(fileTransferConfig.getDestinationPath(), file.getNormalizedFilename());
                boolean isDirectory = file.isDirectory();
                long fileSize = file.getSize();
                logger.debug("Receiving " + (isDirectory ? "directory" : "file") + ": " + filename + " " + fileSize + " bytes");
                if (!isDirectory) {
                    writeFile(filename, fileSize, objectInputStream);
                } else {
                    boolean dirExists = new File(filename).mkdirs();
                    if (dirExists) {
                        logger.info("Directory already exists");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.error("IOException");
        }
    }

    public void writeFile(String filename, long fileSize, ObjectInputStream objectInputStream) throws IOException {
        int length;
        byte[] buf = new byte[fileTransferConfig.getStreamBufferLength()];
        FileOutputStream fos = new FileOutputStream(FilenameUtils.normalize(filename));
        while (fileSize > 0 && (length = objectInputStream.read(buf, 0, (int)Math.min(buf.length, fileSize))) != -1) {
            fos.write(buf, 0, length);
            fileSize -= length;
        }
        fos.close();
    }
}
