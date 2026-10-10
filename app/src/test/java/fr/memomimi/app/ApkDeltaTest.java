package fr.memomimi.app;

import org.junit.Test;
import java.io.*;
import java.nio.file.Files;
import java.util.zip.GZIPOutputStream;
import static org.junit.Assert.*;

public class ApkDeltaTest {
    interface Commands { void write(DataOutputStream output) throws Exception; }
    private void exercise(byte[] expected, Commands commands, boolean accepted) throws Exception {
        File directory = Files.createTempDirectory("memomondes-delta").toFile();
        File base = new File(directory, "base"), patch = new File(directory, "patch"), output = new File(directory, "output");
        File correct = new File(directory, "correct");
        try {
            Files.write(base.toPath(), new byte[]{10, 20, 30, 40}); Files.write(correct.toPath(), expected);
            try (DataOutputStream data = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(patch)))) {
                data.writeInt(0x4d4d4431); data.writeLong(expected.length); commands.write(data);
            }
            try {
                ApkDelta.apply(base, patch, output, expected.length, ApkDelta.sha256(correct));
                if (!accepted) fail("Malformed patch accepted");
                assertArrayEquals(expected, Files.readAllBytes(output.toPath()));
            } catch (IOException rejected) { if (accepted) throw rejected; assertFalse(output.exists()); }
        } finally { for (File file : directory.listFiles()) file.delete(); directory.delete(); }
    }
    @Test public void copyAndLiteralReconstructExactBytes() throws Exception {
        exercise(new byte[]{20, 30, 99}, data -> {
            data.writeByte(0); data.writeLong(1); data.writeInt(2);
            data.writeByte(1); data.writeInt(1); data.writeByte(99); data.writeByte(2);
        }, true);
    }
    @Test public void refusesOutOfBoundsCopy() throws Exception {
        exercise(new byte[]{1}, data -> { data.writeByte(0); data.writeLong(4); data.writeInt(1); data.writeByte(2); }, false);
    }
    @Test public void refusesNegativeLength() throws Exception {
        exercise(new byte[]{1}, data -> { data.writeByte(1); data.writeInt(-1); }, false);
    }
    @Test public void refusesExpansionBeyondTarget() throws Exception {
        exercise(new byte[]{1}, data -> { data.writeByte(1); data.writeInt(2); data.write(new byte[]{1, 2}); data.writeByte(2); }, false);
    }
    @Test public void refusesTruncatedLiteral() throws Exception {
        exercise(new byte[]{1, 2}, data -> { data.writeByte(1); data.writeInt(2); data.writeByte(1); }, false);
    }
    @Test public void refusesHashMismatch() throws Exception {
        exercise(new byte[]{1}, data -> { data.writeByte(1); data.writeInt(1); data.writeByte(2); data.writeByte(2); }, false);
    }
    @Test public void refusesTrailingCommands() throws Exception {
        exercise(new byte[]{1}, data -> { data.writeByte(1); data.writeInt(1); data.writeByte(1); data.writeByte(2); data.writeByte(1); }, false);
    }
    @Test public void refusesUnknownCommands() throws Exception {
        exercise(new byte[]{1}, data -> data.writeByte(3), false);
    }
}
