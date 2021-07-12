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
        FileServerConfig fileServerConfig = new FileServerConfig();
        fileServerConfig.setFromPath("/tmp");
        fileServerConfig.setToPath("/tmp");
        fileServerConfig.setPort(3318);
        fileServerConfig.setEncrypted(false);
        fileServerConfig.setRecurseIntoDirectory(false);
        FileServer fileServer = new FileServer(fileServerConfig);
        new Thread(fileServer).start();
        // File sender
        try {
            SendFile sendFile = new SendFile(fileServerConfig);
            List<File> files = Arrays.asList(new File("zzz/example4"), new File("example"), new File("example1"), new File("example2"), new File("example3"));
            sendFile.send(files);
        } catch (IOException e) {
            logger.error("Could not open socket");
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Something else went horribly wrong");
            e.printStackTrace();
        }
    }
}