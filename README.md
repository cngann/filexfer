# Embedded File Transfer Library
## Private file transfer client and server in Java
### Purpose: To enable a cross-platform embedded application to privately transfer files between clients and servers in a secure and efficient manner with minimal overhead and configuration, no third-party libraries, and no direct interaction with the underlying operating system without using SSL-based sockets due to export restrictions.
### Usage

#### Server
```java
// basic default configuration
class Example {
    public static void main(String[] args) {
        FileServer fileServer = new FileServer();
        new Thread(fileServer).start();
    }
}

// basic default configuration, with new port
class Example {
    public static void main(String[] args) {
        FileTransferConfig fileTransferConfig = FileTransferConfig.getInstance();
        fileTransferConfig.setPort(12345);
        FileServer fileServer = new FileServer();
        new Thread(fileServer).start();
    }
}

```

#### Client
```java
// basic default configuration
class Example {
    public static void main(String[] args) {
        try {
            new SendFile.send(Arrays.asList(new File("file1"), new File("file2")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}

// basic default configuration with new port
class Example {
    public static void main(String[] args) {
        FileTransferConfig fileTransferConfig = FileTransferConfig.getInstance();
        fileTransferConfig.setPort(12345);
        try {
            new SendFile.send(Arrays.asList(new File("file1"), new File("file2")));
        } catch (Exception e) {
            // Bare minimum! Please catch the real exceptions!
            e.printStackTrace();
        }

    }
}
```

### Default Config Options
```java
class FileTransferConfig {
    int port=3318;
    int streamBufferLength=4092;
    boolean encrypted=false;
    boolean recurseIntoDirectory=false;
    String sourcePath="/tmp";
    String destinationPath="/tmp";
    String destinationAddress="localhost";
    String passKey="someSortOfPasskey";
    String encryptionCipher="PBEWithMD5AndDES";
    String encryptedFileExtension=".des";
    Level logLevel=Level.ALL;
}
```