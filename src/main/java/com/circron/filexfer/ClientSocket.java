package com.circron.filexfer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public interface ClientSocket {
    Socket getClientSocket(String host, int port) throws IOException;
    ServerSocket getServerSocket(int port) throws Exception;
}
