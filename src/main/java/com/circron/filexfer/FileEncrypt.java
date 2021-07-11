package com.circron.filexfer;

import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class FileEncrypt {
    public static String tempFilename;
    public static File tempFile;

    public static File encryptFile(File file, FileServerConfig fileServerConfig) throws Exception {
        if (!fileServerConfig.isEncrypted()) return file;
        if (file.isDirectory()) {
            throw new Exception("Cannot encrypt a directory");
        }
        String filename = file.getPath();
        String password = fileServerConfig.getPassKey();
        tempFilename = filename + ".des";
        tempFile = new File(tempFilename);
        FileInputStream inFile = new FileInputStream(file);
        FileOutputStream outFile = new FileOutputStream(tempFile);
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory sKeyFac = SecretKeyFactory.getInstance(fileServerConfig.getEncryptionCipher());
        SecretKey sKey = sKeyFac.generateSecret(keySpec);
        byte[] salt = new byte[8];
        Random rnd = new Random();
        rnd.nextBytes(salt);
        int iterations = 100;
        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
        Cipher c = Cipher.getInstance(fileServerConfig.getEncryptionCipher());
        c.init(Cipher.ENCRYPT_MODE, sKey, parameterSpec);
        outFile.write(salt);
        return Utils.getFile(tempFile, c, inFile, outFile);
    }
}
