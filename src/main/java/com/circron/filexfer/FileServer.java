package com.circron.filexfer;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("unused") public class FileServer implements Runnable {
    protected int serverPort;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    protected FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;
    private final Logger logger = Utils.getLogger(this.getClass());

    public FileServer() {
        this.serverPort = fileTransferConfig.getPort();
    }

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    logger.debug("Server Stopped.");
                    return;
                }
                String message = "Error accepting client connection";
                logger.error(message);
                throw new RuntimeException(message, e);
            }
            new Thread(new ReceiveFile(clientSocket)).start();
        }
        logger.info("Server Stopped.");
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
            logger.info("Stopping the server");
        } catch (IOException e) {
            String message = "Error closing server";
            logger.error(message);
            throw new RuntimeException(message, e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = Utils.getServerSocket(this.serverPort);
            logger.info("Server started on port " + this.serverPort);
        } catch (IOException e) {
            String message = "Cannot open port " + this.serverPort;
            logger.error(message);
            throw new RuntimeException(message, e);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
