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
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLFileTransferSocket implements FileTransferSocket {
    private final Log logger = Utils.getLogger(this.getClass());
    public static String ClientCipherRegex = ".*"; // default match everything
    FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;

    @Override public Socket getClientSocket(String host, int port) throws IOException {
        SSLSocketFactory sslsocketfactory;
        LocalX509TrustManager[] x509TrustManager;
        try {
            SSLContext sslContext;
            KeyStore ksTrust = KeyStore.getInstance("JKS");
            ksTrust.load(new FileInputStream(fileTransferConfig.getTrustStoreFile()), fileTransferConfig.getTrustStorePassphrase().toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(ksTrust);
            x509TrustManager = getTrustManagers(trustManagerFactory);
            sslContext = SSLContext.getInstance(fileTransferConfig.getSslContext());
            sslContext.init(null, x509TrustManager,null);
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
        KeyStore keyStore;
        LocalX509TrustManager[] x509TrustManager;
        char[] passphrase = fileTransferConfig.getTrustStorePassphrase().toCharArray();
        sslContext = SSLContext.getInstance(fileTransferConfig.getSslContext());
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(fileTransferConfig.getKeyManagerInstanceType());
        keyStore = KeyStore.getInstance(fileTransferConfig.getTrustManagerInstanceType());
        keyStore.load(new FileInputStream(fileTransferConfig.getKeystoreFile()), passphrase);
        keyManagerFactory.init(keyStore, passphrase);
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        sslServerSocketFactory = sslContext.getServerSocketFactory();
        return sslServerSocketFactory.createServerSocket(port, 0, InetAddress.getByAddress(new byte[] {0x00,0x00,0x00,0x00}));
    }

    private static LocalX509TrustManager[] getTrustManagers(TrustManagerFactory tmf) {
        LocalX509TrustManager[] localX509TrustManagers;
        TrustManager[] trustManagers = tmf.getTrustManagers();
        localX509TrustManagers = new LocalX509TrustManager[trustManagers.length];
        for (int i = 0; i < localX509TrustManagers.length; i++) {
            if (trustManagers[i] instanceof X509TrustManager)
                localX509TrustManagers[i] = (LocalX509TrustManager)trustManagers[i];
        }
        return localX509TrustManagers;
    }

}
