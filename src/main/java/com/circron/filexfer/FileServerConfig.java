package com.circron.filexfer;

import java.util.Objects;

public class FileServerConfig {
    int port = 3318;
    int streamBufferLength = 4092;
    boolean encrypted = false;
    String fromPath = "/tmp";
    String toPath = "/tmp";
    String passKey = "someSortOfPasskey";
    String encryptionCipher = "PBEWithMD5AndDES";
    String encryptedFileExtension = ".des";

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileServerConfig that = (FileServerConfig)o;
        return port == that.port && streamBufferLength == that.streamBufferLength && encrypted == that.encrypted && Objects.equals(fromPath, that.fromPath) && Objects.equals(toPath, that.toPath) && Objects.equals(passKey, that.passKey) && Objects.equals(encryptionCipher, that.encryptionCipher) && Objects.equals(encryptedFileExtension, that.encryptedFileExtension);
    }

    @Override public int hashCode() {
        return Objects.hash(port, streamBufferLength, encrypted, fromPath, toPath, passKey, encryptionCipher, encryptedFileExtension);
    }

    public String getEncryptedFileExtension() {
        return encryptedFileExtension;
    }

    public void setEncryptedFileExtension(String encryptedFileExtension) {
        this.encryptedFileExtension = encryptedFileExtension;
    }

    public String getPassKey() {
        return passKey;
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFromPath() {
        return fromPath;
    }

    public void setFromPath(String fromPath) {
        this.fromPath = fromPath;
    }

    public String getToPath() {
        return toPath;
    }

    public void setToPath(String toPath) {
        this.toPath = toPath;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getEncryptionCipher() {
        return encryptionCipher;
    }

    public void setEncryptionCipher(String encryptionCipher) {
        this.encryptionCipher = encryptionCipher;
    }

    public int getStreamBufferLength() {
        return streamBufferLength;
    }

    public void setStreamBufferLength(int streamBufferLength) {
        this.streamBufferLength = streamBufferLength;
    }
}
