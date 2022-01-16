package com.circron.filexfer

import org.jetbrains.annotations.NotNull

object FileTransferConfig {
    var port = 3318
    var streamBufferLength = 4092
    var isRecurseIntoDirectory = false
    var destinationFilePath = System.getProperty("java.io.tmpdir") ?: "."
    var destinationAddress = "localhost"
    var logging = true
    @Suppress("unused")
    @Deprecated("left for legacy compatibility to not break code")
    @NotNull
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