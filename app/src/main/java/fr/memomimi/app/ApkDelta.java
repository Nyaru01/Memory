package fr.memomimi.app;

import java.io.*;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

/** MMD1: gzip stream of bounded COPY and DATA commands. Never executes downloaded code. */
final class ApkDelta {
    static final long MAX_APK = 256L * 1024 * 1024;
    static String sha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[65536];
        try (InputStream input = new FileInputStream(file)) {
            int n;
            while ((n = input.read(buffer)) != -1) {
                interrupted(); digest.update(buffer, 0, n);
            }
        }
        StringBuilder result = new StringBuilder(64);
        for (byte b : digest.digest()) result.append(String.format(Locale.ROOT, "%02x", b & 255));
        return result.toString();
    }
    static void interrupted() throws InterruptedIOException {
        if (Thread.currentThread().isInterrupted()) throw new InterruptedIOException("Cancelled");
    }
    static void apply(File base, File patch, File output, long expectedSize, String expectedHash) throws Exception {
        if (expectedSize <= 0 || expectedSize > MAX_APK) throw new IOException("APK size");
        boolean success = false;
        try (RandomAccessFile old = new RandomAccessFile(base, "r");
             DataInputStream input = new DataInputStream(new GZIPInputStream(new FileInputStream(patch)));
             OutputStream target = new BufferedOutputStream(new FileOutputStream(output))) {
            if (input.readInt() != 0x4d4d4431 || input.readLong() != expectedSize) throw new IOException("Patch header");
            byte[] buffer = new byte[65536];
            long written = 0;
            int commands = 0;
            while (true) {
                interrupted();
                if (++commands > 1000000) throw new IOException("Too many commands");
                int command = input.readUnsignedByte();
                if (command == 2) {
                    if (written != expectedSize || input.read() != -1) throw new IOException("Patch length");
                    break;
                }
                if (command != 0 && command != 1) throw new IOException("Patch command");
                long offset = command == 0 ? input.readLong() : 0;
                int length = input.readInt();
                if (length <= 0 || length > expectedSize - written) throw new IOException("Command length");
                if (command == 0 && (offset < 0 || offset > old.length() - length)) throw new IOException("Copy range");
                if (command == 0) old.seek(offset);
                int left = length;
                while (left > 0) {
                    interrupted();
                    int n = Math.min(left, buffer.length);
                    if (command == 0) old.readFully(buffer, 0, n); else input.readFully(buffer, 0, n);
                    target.write(buffer, 0, n); left -= n;
                }
                written += length;
            }
            target.flush();
            if (!sha256(output).equals(expectedHash)) throw new IOException("APK hash mismatch");
            success = true;
        } finally { if (!success) output.delete(); }
    }
}
