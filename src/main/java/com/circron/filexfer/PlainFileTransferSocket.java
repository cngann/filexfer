package com.circron.filexfer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.SocketFactory;

public class PlainFileTransferSocket implements FileTransferSocket {
    @Override public Socket getClientSocket(String host, int port) throws IOException {
        SocketFactory socketFactory = SocketFactory.getDefault();
        return socketFactory.createSocket(host, port);
    }

    @Override public ServerSocket getServerSocket(int port) throws Exception {
        return new ServerSocket(port);
    }
}
