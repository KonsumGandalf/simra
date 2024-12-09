package com.simra.konsumgandalf.common.utils.services;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class FileReaderServiceTest {

    @InjectMocks
    private FileReaderService fileReaderService;

    @TempDir
    Path tempDir;

    @Nested
    class testReadFileFromPath {
        @Test
        public void testReadFileFromPath_ValidFile() throws Exception {
            Path tempFile = tempDir.resolve("testfile.txt");
            String content = "Hello, World!";
            Files.write(tempFile, content.getBytes());

            String result = fileReaderService.readFileFromPath(tempFile.toString());

            assertEquals(content, result);
        }

        @Test
        public void testReadFileFromPath_FileNotFound() {
            assertThrows(RuntimeException.class, () -> {
                fileReaderService.readFileFromPath("nonexistentfile.txt");
            });
        }
    }
}