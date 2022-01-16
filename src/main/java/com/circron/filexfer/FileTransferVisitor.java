package com.circron.filexfer;

import org.apache.commons.logging.Log;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileTransferVisitor implements FileVisitor<Path> {
    Log logger = Utils.getLogger(this.getClass());

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        logger.trace("Entering directory: " + dir.getFileName().toString() + " with perms: " + attrs.toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        logger.trace(file.getFileName().toString() + ": " + attrs.toString());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        logger.trace("error on: " + file.getFileName().toString() + " " + exc.getClass());
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }
}
