package com.circron.filexfer;

import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
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
        send(Collections.singletonList(file));
    }

    public void send(List<File> files) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            logger.debug("Number of files to transfer: " + files.size());
            dataOutputStream.writeInt(files.size());
            dataOutputStream.flush();
            int length;
            byte[] bytes = new byte[fileServerConfig.getStreamBufferLength()];
            for (File file : files) {
                if (fileServerConfig.isEncrypted()) {
                    try {
                        file = FileEncrypt.encryptFile(file, fileServerConfig);
                    } catch (Exception e) {
                        logger.error("Could not encrypt file " + file.getName());
                    }
                }
                logger.debug("Sending file: " + file.getName());
                dataOutputStream.writeUTF(file.getName());
                dataOutputStream.writeLong(file.length());
                dataOutputStream.flush();
                FileInputStream fileInputStream = new FileInputStream(file);
                while ((length = fileInputStream.read(bytes)) != -1) {
                    dataOutputStream.write(bytes, 0, length);
                    dataOutputStream.flush();
                }
            }
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
