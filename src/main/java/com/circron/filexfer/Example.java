package com.circron.filexfer;

import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        Logger logger = Utils.getLogger(Example.class);
        // Configure this
        FileTransferConfig fileTransferConfig = FileTransferConfig.getInstance();
        fileTransferConfig.setFromPath("/tmp");
        fileTransferConfig.setToPath("/tmp");
        fileTransferConfig.setPort(3318);
        fileTransferConfig.setEncrypted(true);
        fileTransferConfig.setRecurseIntoDirectory(false);
        FileServer fileServer = new FileServer();
        new Thread(fileServer).start();
        // File sender
        try {
            SendFile sendFile = new SendFile();
//            List<File> files = Arrays.asList(new File("zzz/example4"), new File("build.gradle"), new File("example3"));
//            sendFile.send(files);
            sendFile.send(new File("build.gradle"));
        } catch (IOException e) {
            logger.error("Could not open socket");
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Something else went horribly wrong");
            e.printStackTrace();
        }
    }
}