package com.circron.filexfer;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLClientSocket implements ClientSocket {
    FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;

    @Override public Socket getClientSocket(String host, int port) throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket)sslSocketFactory.createSocket(fileTransferConfig.getDestinationAddress(), fileTransferConfig.getPort());
        socket.startHandshake();
        return socket;
    }

    @Override public ServerSocket getServerSocket(int port) throws Exception {
        SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
        sslServerSocketFactory.createServerSocket(port);
        SSLContext sslContext;
        KeyManagerFactory keyManagerFactoryf;
        KeyStore keyStore;
        char[] passphrase = fileTransferConfig.getPassKey().toCharArray();
        sslContext = SSLContext.getInstance("TLS");
        keyManagerFactoryf = KeyManagerFactory.getInstance("SunX509");
        keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("testkeys"), passphrase);
        keyManagerFactoryf.init(keyStore, passphrase);
        sslContext.init(keyManagerFactoryf.getKeyManagers(), null, null);
        sslServerSocketFactory = sslContext.getServerSocketFactory();
        return sslServerSocketFactory.createServerSocket();
    }
}
