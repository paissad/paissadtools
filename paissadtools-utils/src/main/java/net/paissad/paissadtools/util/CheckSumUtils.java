package net.paissad.paissadtools.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * This class contains some utilities that help to retrieve the checksum value
 * of files.
 * 
 * @author paissad
 * @since 0.1
 */
public class CheckSumUtils {

    private static final int    BUFFER = 8192;
    private static final String MD5    = "MD5";
    private static final String SHA1   = "SHA";
    private static final String SHA256 = "SHA-256";
    private static final String SHA512 = "SHA-512";

    /**
     * Retrieves the md5 sum of a file.
     * 
     * @param file - The file to check.
     * @return The md5 sum.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String md5sum(final File file) throws IOException, NoSuchAlgorithmException {
        return computeCheckSum(file, MD5);
    }

    /**
     * Retrieves the SHA-1 sum of a file.
     * 
     * @param file
     * @return The SHA1 sum.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String sha1sum(final File file) throws IOException, NoSuchAlgorithmException {
        return computeCheckSum(file, SHA1);
    }

    /**
     * @param file
     * @return The SHA-256 sum of the specified file.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String sha256sum(final File file) throws NoSuchAlgorithmException, IOException {
        return computeCheckSum(file, SHA256);
    }

    /**
     * @param file
     * @return The SHA-512 sum of the specified file.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String sha512sum(final File file) throws NoSuchAlgorithmException, IOException {
        return computeCheckSum(file, SHA512);
    }

    private static String computeCheckSum(final File file, final String algorithm)
            throws IOException, NoSuchAlgorithmException {

        if (file == null) throw new IllegalArgumentException("The file for which to check the sum cannot be null.");
        if (!file.isFile()) {
            throw new IOException("Must be a file.");
        }

        BufferedInputStream bis = null;
        MessageDigest checksum = MessageDigest.getInstance(algorithm);
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            int length;
            byte[] data = new byte[BUFFER];
            while ((length = bis.read(data, 0, BUFFER)) != -1) {
                checksum.update(data, 0, length);
            }
            bis.close();
            byte[] bytes = checksum.digest();
            // On retourne le tableau de bytes en un String format hexad�cimal
            return toHex(bytes).toUpperCase(Locale.US);
        } finally {
            if (bis != null) {
                bis.close();
                bis = null;
            }
        }
    }

    /**
     * Convertit un tableau de byte en une cha�ne de caract�res au format
     * hexad�cimal.
     * 
     * @param bytes
     * @return La cha�ne au format hexad�cimal
     */
    private static String toHex(byte[] bytes) {
        BigInteger bigInt = new BigInteger(1, bytes);
        return bigInt.toString(16);
    }
}
