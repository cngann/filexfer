package com.circron.filexfer.file;

import com.circron.filexfer.FileTransferConfig;
import com.circron.filexfer.Utils;

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

public class Decrypt {
    public static FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;

    public static File decryptFile(File encryptedFile) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if (!fileTransferConfig.isEncrypted()) return encryptedFile;
        String filename = Utils.getFilePath(fileTransferConfig.getDestinationPath(), encryptedFile.getName());
        String password = fileTransferConfig.getPassKey();
        FileInputStream fileInputStream = new FileInputStream(encryptedFile);
        File decryptedFile = new File(filename.replace(fileTransferConfig.getEncryptedFileExtension(), ""));
        FileOutputStream fileOutputStream = new FileOutputStream(decryptedFile);
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory sKeyFac = SecretKeyFactory.getInstance(fileTransferConfig.getEncryptionCipher());
        SecretKey sKey = sKeyFac.generateSecret(keySpec);
        byte[] salt = new byte[8];
        if (fileInputStream.read(salt) < 0) throw new IOException("Could not read input");
        int iterations = 100;
        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
        Cipher cipher = Cipher.getInstance(fileTransferConfig.getEncryptionCipher());
        cipher.init(Cipher.DECRYPT_MODE, sKey, parameterSpec);
        return Utils.getFile(decryptedFile, cipher, fileInputStream, fileOutputStream);
    }
}
