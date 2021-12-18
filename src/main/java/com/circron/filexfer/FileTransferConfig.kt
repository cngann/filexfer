package com.circron.filexfer

import org.apache.logging.log4j.Level

object FileTransferConfig {
    var port = 3318
    var streamBufferLength = 4092
    var isRecurseIntoDirectory = false
    var destinationFilePath = System.getProperty("java.io.tmpdir")
    var destinationAddress = "localhost"
    var logLevel: Level = LogLevel.setLogLevel("WARN")
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