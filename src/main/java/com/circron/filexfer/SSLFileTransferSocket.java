package com.circron.filexfer;

import org.apache.commons.logging.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SSLFileTransferSocket implements FileTransferSocket {
    private final Log logger = Utils.getLogger(this.getClass());
    public static String ClientCipherRegex = ".*"; // default match everything
    FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;

    @Override public Socket getClientSocket(String host, int port) throws IOException {
        SSLSocketFactory sslsocketfactory;
        try {
            SSLContext sslContext;
            TrustManagerFactory trustManagerFactory;
            KeyStore trustStore;
            trustStore = KeyStore.getInstance(fileTransferConfig.getKeystoreInstanceType());
            trustStore.load(new FileInputStream(fileTransferConfig.getTrustStoreFile()), fileTransferConfig.getTrustStorePassphrase().toCharArray());
            trustManagerFactory = TrustManagerFactory.getInstance(fileTransferConfig.getKeyManagerInstanceType());
            trustManagerFactory.init(trustStore);
            sslContext = SSLContext.getInstance(fileTransferConfig.getSslContext());
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            sslsocketfactory = sslContext.getSocketFactory();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | KeyManagementException | IOException e) {
            logger.error(e.getMessage());
            return null;
        }
        String[] SupportedCiphers = sslsocketfactory.getSupportedCipherSuites();
        int ClientCipherCount = 0;
        for (String supportedCipher : SupportedCiphers) {
            if (supportedCipher.matches(ClientCipherRegex)) {
                ClientCipherCount++;
            }
        }
        if (ClientCipherCount == 0) {
            logger.error("No " + ClientCipherRegex + " matching Cipher Suites - exiting");
            return null;
        }
        String[] ClientCiphers = new String[ClientCipherCount];
        for (int i = 0, j = 0; i < SupportedCiphers.length; i++) {
            if (SupportedCiphers[i].matches(ClientCipherRegex)) {
                ClientCiphers[j++] = SupportedCiphers[i];
            }
        }
        SSLSocket sslsocket = (SSLSocket)sslsocketfactory.createSocket(host, port);
        sslsocket.setEnabledCipherSuites(ClientCiphers);
        sslsocket.startHandshake();
        return sslsocket;
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
        return sslServerSocketFactory.createServerSocket(port, 0, InetAddress.getByAddress(new byte[] {0x00,0x00,0x00,0x00}));
    }
}
