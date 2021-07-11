package com.circron.filexfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class FileDecrypt {
    public static File decryptFile(File encryptedFile, FileServerConfig fileServerConfig) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if (!fileServerConfig.isEncrypted()) return encryptedFile;
        String filename = Utils.getFilePath(fileServerConfig.getToPath(), encryptedFile.getName());
        String password = fileServerConfig.getPassKey();
        FileInputStream inFile = new FileInputStream(encryptedFile);
        File decryptedFile = new File(filename);
        FileOutputStream outFile = new FileOutputStream(decryptedFile);
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory sKeyFac = SecretKeyFactory.getInstance(fileServerConfig.getEncryptionCipher());
        SecretKey sKey = sKeyFac.generateSecret(keySpec);
        byte[] salt = new byte[8];
        inFile.read(salt);
        int iterations = 100;
        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
        Cipher cipher = Cipher.getInstance(fileServerConfig.getEncryptionCipher());
        cipher.init(Cipher.DECRYPT_MODE, sKey, parameterSpec);
        return Utils.getFile(decryptedFile, cipher, inFile, outFile);
    }
}
