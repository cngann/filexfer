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
    public static FileTransferConfig fileTransferConfig = FileTransferConfig.getInstance();

    public static File decryptFile(File encryptedFile) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if (!fileTransferConfig.isEncrypted()) return encryptedFile;
        String filename = Utils.getFilePath(fileTransferConfig.getToPath(), encryptedFile.getName());
        String password = fileTransferConfig.getPassKey();
        FileInputStream inFile = new FileInputStream(encryptedFile);
        File decryptedFile = new File(filename.replace(fileTransferConfig.getEncryptedFileExtension(), ""));
        FileOutputStream outFile = new FileOutputStream(decryptedFile);
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory sKeyFac = SecretKeyFactory.getInstance(fileTransferConfig.getEncryptionCipher());
        SecretKey sKey = sKeyFac.generateSecret(keySpec);
        byte[] salt = new byte[8];
        inFile.read(salt);
        int iterations = 100;
        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
        Cipher cipher = Cipher.getInstance(fileTransferConfig.getEncryptionCipher());
        cipher.init(Cipher.DECRYPT_MODE, sKey, parameterSpec);
        return Utils.getFile(decryptedFile, cipher, inFile, outFile);
    }
}
