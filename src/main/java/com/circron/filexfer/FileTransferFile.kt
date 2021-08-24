package com.circron.filexfer

import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.Serializable

data class FileTransferFile(var file: File): Serializable {
    var size: Long = file.length()
    var isDirectory: Boolean = file.isDirectory
    var path: String = FilenameUtils.normalize(file.path)
    var filename: String = file.name
    var normalizedFilename: String = ""
}