# Embedded File Transfer Library
## Private file transfer client and server in Java
### Purpose: To enable a cross-platform embedded application to privately transfer files between clients and servers in a secure and efficient manner with minimal overhead and configuration, no third-party libraries, and no direct interaction with the underlying operating system.

###Usage
####Server
```java
class Example {
    public static void main(String[] args) {
        // basic default configuration
        FileTransferConfig fileTransferConfig = new FileTransferConfig();
        FileServer fileServer = new FileServer(fileTransferConfig);
        new Thread(fileServer).start();
    }
}
```
####Client
```java
class Example {
    public static void main(String[] args) {
        // basic default configuration
        FileTransferConfig fileTransferConfig = new FileTransferConfig();
        try {
            SendFile sendFile = new SendFile(fileTransferConfig);
            List<File> files = Arrays.asList(new File("file1"), new File("file2"), new File("file3"));
            sendFile.send(files);
        } catch (Exception e) {
            // Bare minimum! Please catch the real exceptions!
            e.printStackTrace();
        }
        
    }
}
```

**TODO**
* Remove traversal paths (eg. `..`, `.`) from both incoming and outgoing files
* Wire in the encryption modules
* Remove `example.java`
