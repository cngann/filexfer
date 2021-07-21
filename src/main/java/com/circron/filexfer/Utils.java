package com.circron.filexfer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static final FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;

    public static Socket getClientSocket(String host, int port) throws IOException {
        return getSocket().getClientSocket(host, port);
    }

    public static ServerSocket getServerSocket(int port) throws Exception {
        return getSocket().getServerSocket(port);
    }

    private static FileTransferSocket getSocket() {
        FileTransferSocket socket;
        boolean keystoreExists = new File(fileTransferConfig.getKeystoreFile()).exists();
        if (!keystoreExists && fileTransferConfig.getPlainFallback()) {
            logger.warn("Keystore does not exist! Falling back to plain socket");
            fileTransferConfig.setEncrypted(false);
        }
        if (fileTransferConfig.isEncrypted() && fileTransferConfig.getUseSsl()) {
            logger.info("Opening SSL connection");
            socket = new SSLFileTransferSocket();
        } else {
            logger.info("Opening plain connection");
            socket = new PlainFileTransferSocket();
        }
        return socket;
    }

    public static Logger getLogger(Class<?> clazz) {
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), FileTransferConfig.INSTANCE.getLogLevel());
        return LogManager.getLogger(clazz);
    }

    public static File getFile(File file, Cipher cipher, FileInputStream fileInputStream, FileOutputStream fileOutputStream) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] input = new byte[64];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(input)) != -1) {
            byte[] output = cipher.update(input, 0, bytesRead);
            if (output != null) {
                fileOutputStream.write(output);
            }
        }
        byte[] output = cipher.doFinal();
        if (output != null) {
            fileOutputStream.write(output);
        }
        fileInputStream.close();
        fileOutputStream.flush();
        fileOutputStream.close();
        return file;
    }

    public static String getFilePath(String path, String filename) {
        logger.debug("Calling getFilePath with path " + path + " and filename " + filename);
        if (filename == null) {
            filename = new Random().toString();
        }
        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }
        if (path == null) {
            path = "/";
        }
        if (path.endsWith("/")) {
            return path + filename;
        } else {
            return path + "/" + filename;
        }
    }

    static List<FileTransferFile> getFilesWithDirs(List<FileTransferFile> files) {
        boolean recurseIntoDirs = fileTransferConfig.isRecurseIntoDirectory();
        List<FileTransferFile> filesWithDirs = new ArrayList<>(files);
        for (FileTransferFile fileTransferFile : filesWithDirs) {
            if (fileTransferFile.getFile().getParentFile() != null) {
                if (recurseIntoDirs) {
                    files.add(0, fileTransferFile);
                } else {
                    logger.info("Skipping subdirectories and their files, recursion is not enabled");
                    files.remove(fileTransferFile);
                }
            }
        }
        return files;
    }
}
