package com.circron.filexfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class FileEncrypt {
    public static String tempFilename;
    public static File tempFile;
    public static FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;

    public static File encryptFile(File file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (!fileTransferConfig.isEncrypted()) return file;
        if (file.isDirectory()) {
            throw new IOException("Cannot encrypt a directory");
        }
        String filename = file.getPath();
        String password = fileTransferConfig.getPassKey();
        tempFilename = filename + fileTransferConfig.getEncryptedFileExtension();
        tempFile = new File(tempFilename);
        FileInputStream inFile = new FileInputStream(file);
        FileOutputStream outFile = new FileOutputStream(tempFile);
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory sKeyFac = SecretKeyFactory.getInstance(fileTransferConfig.getEncryptionCipher());
        SecretKey sKey = sKeyFac.generateSecret(keySpec);
        byte[] salt = new byte[8];
        Random random = new Random();
        random.nextBytes(salt);
        int iterations = 100;
        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
        Cipher c = Cipher.getInstance(fileTransferConfig.getEncryptionCipher());
        c.init(Cipher.ENCRYPT_MODE, sKey, parameterSpec);
        outFile.write(salt);
        return Utils.getFile(tempFile, c, inFile, outFile);
    }
}
