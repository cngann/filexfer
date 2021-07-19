package com.circron.filexfer

import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.Serializable

class FileTransferFile(var file: File): Serializable {
    var size: Long = file.length()
    var isDirectory: Boolean = file.isDirectory
    var isEncrypted = false
    var path: String = file.path
    var filename: String = file.name
    var normalizedFilename: String = FilenameUtils.normalize(file.path)
}