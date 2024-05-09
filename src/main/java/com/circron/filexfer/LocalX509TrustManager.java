package com.circron.filexfer;

import org.apache.commons.logging.Log;

import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class LocalX509TrustManager implements X509TrustManager {
    private final X509TrustManager impl;
    private final Log logger = Utils.getLogger(this.getClass());

    public LocalX509TrustManager(X509TrustManager impl) {
        this.impl = impl;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        impl.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        impl.checkServerTrusted(chain, authType);
        for (X509Certificate x509Certificate : chain) {
            try {
                x509Certificate.checkValidity();
            } catch (CertificateExpiredException e) {
                logger.error("Certificate is expired");
            }
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return impl.getAcceptedIssuers();
    }
}