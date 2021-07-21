package com.circron.filexfer

import org.apache.logging.log4j.Level

object FileTransferConfig {
    var port = 3318
    var streamBufferLength = 4092
    var isEncrypted = false
    var useSsl = false
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