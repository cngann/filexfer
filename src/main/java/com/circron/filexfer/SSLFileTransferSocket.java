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

public class SSLFileTransferSocket implements FileTransferSocket {
    private final FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;

    @Override public Socket getClientSocket(String host, int port) throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket)sslSocketFactory.createSocket(host, port);
        socket.startHandshake();
        return socket;
    }

    @Override public ServerSocket getServerSocket(int port) throws Exception {
        SSLServerSocketFactory sslServerSocketFactory;
        SSLContext sslContext;
        KeyManagerFactory keyManagerFactory;
        KeyStore keyStore;
        char[] passphrase = fileTransferConfig.getKeystorePassphrase().toCharArray();
        sslContext = SSLContext.getInstance(fileTransferConfig.getSslContext());
        keyManagerFactory = KeyManagerFactory.getInstance(fileTransferConfig.getKeyManagerInstanceType());
        keyStore = KeyStore.getInstance(fileTransferConfig.getKeystoreInstanceType());
        keyStore.load(new FileInputStream(fileTransferConfig.getKeystoreFile()), passphrase);
        keyManagerFactory.init(keyStore, passphrase);
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        sslServerSocketFactory = sslContext.getServerSocketFactory();
        return sslServerSocketFactory.createServerSocket(port);
    }
}
