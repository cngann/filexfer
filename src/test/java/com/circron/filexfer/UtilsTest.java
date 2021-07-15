package com.circron.filexfer;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
    File setupFile(String name) {
        try {
            boolean file = new File(name).createNewFile();
            return new File(name);
        } catch (IOException e) {
            System.out.println("Cannot create file here");
        }
        return null;
    }

    boolean tearDownFile(String name) {
        return new File(name).delete();
    }

    @Test void getLogger() {
        assertNotNull(Logger.getLogger(getClass().getName()));
    }

    @Test void getFile() {
        assertEquals(setupFile("tmp").getName(), "tmp");
        assertNotNull(setupFile("tmp"));
        assertTrue(tearDownFile("tmp"));
    }

    @Test void getFilePath() {
        File file = setupFile("tmp");
        File file2 = setupFile("/tmp/tmp");
        assertFalse(file.getPath().contains("/"));
        assertTrue(file2.getPath().contains("/"));
        assertNotNull(new Random().toString());
        tearDownFile("tmp");
        tearDownFile("/tmp/tmp");
    }

}