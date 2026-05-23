package com.sliit.vehiclerentalsystem01.core.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileDbHelperTest {

    @Test
    void testWriteAtomic(@TempDir Path tempDir) throws IOException {
        Path target = tempDir.resolve("db.txt");
        List<String> lines = List.of("Line 1", "Line 2", "Line 3");

        FileDbHelper.writeAtomic(target, lines);

        assertTrue(Files.exists(target));
        List<String> readLines = Files.readAllLines(target, StandardCharsets.UTF_8);
        assertEquals(3, readLines.size());
        assertEquals("Line 1", readLines.get(0));
        assertEquals("Line 2", readLines.get(1));
        assertEquals("Line 3", readLines.get(2));
    }
}
