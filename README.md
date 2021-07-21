# Embedded File Transfer Library
## Private file transfer client and server in Java
### Purpose: To enable a cross-platform embedded application to privately transfer files between clients and servers in a secure and efficient manner with minimal overhead and configuration, no third-party libraries, and no direct interaction with the underlying operating system with or without using SSL-based sockets.
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
```kotlin
object FileTransferConfig {
    var port = 3318
    var streamBufferLength = 4092
    var isEncrypted = false
    var isRecurseIntoDirectory = false
    var sourcePath = "/tmp"
    var destinationPath = "/tmp"
    var destinationAddress = "localhost"
    var passKey = "someSortOfPasskey"
    var encryptionCipher = "PBEWithMD5AndDES"
    var encryptedFileExtension = ".des"
    var logLevel: Level = Level.WARN
    var plainFallback = false
    var keystorePassphrase = "password"
    var keystoreFile = "default"
    var keystoreInstanceType = "JKS"
    var keyManagerInstanceType = "SunX509"
    var sslContext = "TLS"
}
```

## Notes
* If you want to use a self-signed certificate for SSL-based sockets, you will need to specify the following VM arguments:
`-Djavax.net.ssl.trustStore=[truststore] -Djavax.net.ssl.trustStorePassword=[password]`
  
* File-based encryption is an option if SSL is not available. Files will be encrypted, transmitted, then decrypted upon receipt at the destination.

* Plaintext (if SSL fails) fallback is available if desired, but definitely not recommended!

* Code is compatible with Java 8 and above