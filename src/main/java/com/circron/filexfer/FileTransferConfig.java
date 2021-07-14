package com.circron.filexfer;

import org.apache.logging.log4j.Level;

import java.util.Objects;

@SuppressWarnings("unused") public class FileTransferConfig {
    int port = 3318;
    int streamBufferLength = 4092;
    boolean encrypted = false;
    boolean recurseIntoDirectory = false;
    String sourcePath = "/tmp";
    String destinationPath = "/tmp";
    String destinationAddress = "localhost";
    String passKey = "someSortOfPasskey";
    String encryptionCipher = "PBEWithMD5AndDES";
    String encryptedFileExtension = ".des";
    Level logLevel = Level.ALL;
    public static FileTransferConfig instance = null;

    public static FileTransferConfig getInstance() {
        if (FileTransferConfig.instance == null) {
            FileTransferConfig.instance = new FileTransferConfig();
        }
        return FileTransferConfig.instance;
    }

    private FileTransferConfig() {}

    public Level getLogLevel() {
        return logLevel;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isRecurseIntoDirectory() {
        return recurseIntoDirectory;
    }

    public void setRecurseIntoDirectory(boolean recurseIntoDirectory) {
        this.recurseIntoDirectory = recurseIntoDirectory;
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

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String fromPath) {
        this.sourcePath = fromPath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String toPath) {
        this.destinationPath = toPath;
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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileTransferConfig that = (FileTransferConfig)o;
        return port == that.port && streamBufferLength == that.streamBufferLength && encrypted == that.encrypted && recurseIntoDirectory == that.recurseIntoDirectory && Objects.equals(sourcePath, that.sourcePath) && Objects.equals(destinationPath, that.destinationPath) && Objects.equals(passKey, that.passKey) && Objects.equals(encryptionCipher, that.encryptionCipher) && Objects.equals(encryptedFileExtension, that.encryptedFileExtension);
    }

    @Override public int hashCode() {
        return Objects.hash(port, streamBufferLength, encrypted, recurseIntoDirectory, sourcePath, destinationPath, passKey, encryptionCipher, encryptedFileExtension);
    }
}
