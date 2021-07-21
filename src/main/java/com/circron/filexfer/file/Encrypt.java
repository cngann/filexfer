package com.circron.filexfer.file;

import com.circron.filexfer.FileTransferConfig;
import com.circron.filexfer.Utils;

import org.apache.logging.log4j.Logger;

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

public class Encrypt {
    public static String tempFilename;
    public static File tempFile;
    public static FileTransferConfig fileTransferConfig = FileTransferConfig.INSTANCE;
    public final static Logger logger = Utils.getLogger(Encrypt.class);

    public static File encryptFile(File file) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (!fileTransferConfig.isEncrypted() || fileTransferConfig.getUseSsl()) {
            logger.warn("Encryption not enabled");
            return file;
        }
        if (file.isDirectory()) {
            String message = "Cannot encrypt a directory";
            logger.info(message);
            throw new IOException(message);
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
