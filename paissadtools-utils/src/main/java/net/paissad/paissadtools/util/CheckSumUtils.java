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
 */
public final class CheckSumUtils {

    private static final int    BUFFER = 8192;
    private static final String MD5    = "MD5";
    private static final String SHA1   = "SHA";
    private static final String SHA256 = "SHA-256";
    private static final String SHA512 = "SHA-512";

    private CheckSumUtils() {
    }

    /**
     * Retrieves the md5 sum of a file.
     * 
     * @param file - The file to check.
     * @return The md5 sum.
     * @throws IllegalArgumentException If the file is <code>null</code> or is
     *             not a file.
     * @throws IOException If an error occurs while reading the file.
     * @throws NoSuchAlgorithmException If it is impossible to produce a
     *             checksum by using the specified method/algorithm.
     */
    public static String md5sum(final File file) throws IllegalArgumentException, IOException, NoSuchAlgorithmException {
        return computeCheckSum(file, MD5);
    }

    /**
     * Retrieves the SHA-1 sum of a file.
     * 
     * @param file - The file to check.
     * @return The SHA1 sum.
     * @throws IllegalArgumentException If the file is <code>null</code> or is
     *             not a file.
     * @throws IOException If an error occurs while reading the file.
     * @throws NoSuchAlgorithmException If it is impossible to produce a
     *             checksum by using the specified method/algorithm.
     */
    public static String sha1sum(final File file) throws IllegalArgumentException, IOException,
            NoSuchAlgorithmException {
        return computeCheckSum(file, SHA1);
    }

    /**
     * @param file - The file to check.
     * @return The SHA-256 sum of the specified file.
     * @throws IllegalArgumentException If the file is <code>null</code> or is
     *             not a file.
     * @throws IOException If an error occurs while reading the file.
     * @throws NoSuchAlgorithmException If it is impossible to produce a
     *             checksum by using the specified method/algorithm.
     */
    public static String sha256sum(final File file) throws IllegalArgumentException, NoSuchAlgorithmException,
            IOException {
        return computeCheckSum(file, SHA256);
    }

    /**
     * @param file - The file to check.
     * @return The SHA-512 sum of the specified file.
     * @throws IllegalArgumentException If the file is <code>null</code> or is
     *             not a file.
     * @throws IOException If an error occurs while reading the file.
     * @throws NoSuchAlgorithmException If it is impossible to produce a
     *             checksum by using the specified method/algorithm.
     */
    public static String sha512sum(final File file) throws IllegalArgumentException, NoSuchAlgorithmException,
            IOException {
        return computeCheckSum(file, SHA512);
    }

    /**
     * @param file
     * @param algorithm
     * @return
     * @throws IllegalArgumentException If the file is <code>null</code> or is
     *             not a file.
     * @throws IOException If an error occurs while reading the file.
     * @throws NoSuchAlgorithmException If it is impossible to produce a
     *             checksum by using the specified method/algorithm.
     */
    private static String computeCheckSum(final File file, final String algorithm) throws IllegalArgumentException,
            IOException, NoSuchAlgorithmException {

        if (file == null) throw new IllegalArgumentException("The file for which to check the sum cannot be null.");
        if (!file.isFile()) throw new IllegalArgumentException("Must be a file.");

        BufferedInputStream bis = null;
        try {
            final MessageDigest checksum = MessageDigest.getInstance(algorithm);
            bis = new BufferedInputStream(new FileInputStream(file));
            int length;
            final byte[] data = new byte[BUFFER];
            while ((length = bis.read(data, 0, BUFFER)) != -1) {
                checksum.update(data, 0, length);
            }
            bis.close();
            final byte[] bytes = checksum.digest();
            return toHex(bytes).toUpperCase(Locale.ENGLISH);

        } finally {
            CommonUtils.closeStreamQuietly(bis);
        }
    }

    /**
     * Converts an array of byte into an hexadecimal string format.
     * 
     * @param bytes - The byte array to convert
     * @return The string in hexadecimal format
     */
    private static String toHex(final byte[] bytes) {
        final BigInteger bigInt = new BigInteger(1, bytes);
        return bigInt.toString(16);
    }
}
