# Embedded File Transfer Library
## Private file transfer client and server in Java
### Purpose: To be a cross-platform embedded library which transfers files between client and server applications in a secure and efficient manner.
Minimal overhead and configuration; can be used with or without using SSL sockets.
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
    var isRecurseIntoDirectory = false
    var destinationFilePath = System.getProperty("java.io.tmpdir") ?: "."
    var destinationAddress = "localhost"
    var logging = true
    // The following has been deprecated and only left to support older code
    // Deprecation message will appear in most editors
    var logLevel = "deprecated"
    var useSsl = false
    var plainTextFallback = false
    var keystorePassphrase = "password"
    var keystoreFile = "default"
    var keystoreInstanceType = "JKS"
    var keyManagerInstanceType = "SunX509"
    var sslContext = "TLS"
    var trustStorePassphrase = "password"
    var trustStoreFile = "default"
}
```

## Notes
* If you want to use a self-signed certificate for SSL-based sockets, you will need to specify the following VM arguments:
`-Djavax.net.ssl.trustStore=[truststore] -Djavax.net.ssl.trustStorePassword=[password]`
  
* Plaintext (if SSL fails) fallback is available if desired, but definitely not recommended!

* Code is compatible with Java 8 and above