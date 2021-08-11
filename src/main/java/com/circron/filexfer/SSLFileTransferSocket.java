package com.circron.filexfer;

import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private final Logger logger = Utils.getLogger(this.getClass());
    public static String ClientCipherRegex = ".*"; // default match everything
    static String VxCipher = "none";
    static String VxProtocol = "none";

    FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;

    @Override public Socket getClientSocket(String host, int port) throws IOException {
        SSLSocketFactory sslsocketfactory = null;
        try {
            SSLContext sslContext;
            TrustManagerFactory trustManagerFactory;
            KeyStore trustStore;

            trustStore = KeyStore.getInstance(fileTransferConfig.getKeystoreInstanceType());
            trustStore.load(new FileInputStream(fileTransferConfig.getTrustStoreFile()), fileTransferConfig.getTrustStorePassphrase().toCharArray());
            trustManagerFactory = TrustManagerFactory.getInstance(fileTransferConfig.getKeyManagerInstanceType());
            trustManagerFactory.init(trustStore);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            sslsocketfactory = sslContext.getSocketFactory();
        } catch (KeyManagementException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }  catch (CertificateException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (KeyStoreException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        String[] SupportedCiphers = sslsocketfactory.getSupportedCipherSuites();
        int ClientCipherCount = 0;
        for (int i=0; i<SupportedCiphers.length; i++) {
            if (SupportedCiphers[i].matches(ClientCipherRegex)) {
                ClientCipherCount++;
            }
        }
        if (ClientCipherCount == 0) {
            logger.error("\n\nNo " + ClientCipherRegex + " matching Cipher Suites - exiting\n");
            return null;
        }
        String[] ClientCiphers = new String[ClientCipherCount];
        for (int i=0, j=0; i<SupportedCiphers.length; i++) {
            if (SupportedCiphers[i].matches(ClientCipherRegex)) {
                ClientCiphers[j++] = SupportedCiphers[i];
            }
        }
        SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port);
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
        return sslServerSocketFactory.createServerSocket(port);
    }

}
