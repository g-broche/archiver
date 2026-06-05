package com.gbroche.archiver.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    @Test
    void getFileExtension_shouldReturnExtensionIfPresent() {
        File testFile = Path.of("dir", "test.txt").toFile();
        String extension = FileUtils.getFileExtension(testFile);
        assertEquals("txt", extension);
    }


    @Test
    void getFileExtension_shouldReturnEmptyStringIfAbsent() {
        File testFile = Path.of("dir", "dockerfile").toFile();
        String extension = FileUtils.getFileExtension(testFile);
        assertEquals("", extension);
    }

    @Test
    void getFileNameWithoutExtension_shouldReturnNameForFileWithExtension() {
        File testFile = Path.of("dir", "test.txt").toFile();
        String extension = FileUtils.getFileNameWithoutExtension(testFile);
        assertEquals("test", extension);
    }
    @Test
    void getFileNameWithoutExtension_shouldReturnNameForFileWithoutExtension() {
        File testFile = Path.of("dir", "dockerfile").toFile();
        String extension = FileUtils.getFileNameWithoutExtension(testFile);
        assertEquals("dockerfile", extension);
    }

    @Test
    void isDirectoryDirectParentOfFile_shouldReturnTrueIfDirectParent() {
        File parentFile = Path.of("dir", "parent").toFile();
        File file = Path.of("dir", "parent", "test.txt").toFile();
        boolean result = FileUtils.isDirectoryDirectParentOfFile(parentFile, file);
        assertTrue(result);
    }

    @Test
    void isDirectoryDirectParentOfFile_shouldReturnFalseIfNotDirectParent() {
        File parentFile = Path.of("dir", "parent").toFile();
        File file = Path.of("dir", "parent", "child", "test.text").toFile();
        boolean result = FileUtils.isDirectoryDirectParentOfFile(parentFile, file);
        assertFalse(result);
    }

    @Test
    void doesDirectoryEndWithSegment_shouldReturnTrueWhenCorrect() {
    File directory = Path.of("root", "subdirectory1", "subdirectory2", "test").toFile();
    String testValue = "test";
    boolean result = FileUtils.doesDirectoryEndWithSegment(directory, testValue);
    assertTrue(result);
    }


    @Test
    void doesDirectoryEndWithSegment_shouldReturnFalseWhenIncorrect() {
        File directory = Path.of("root", "subdirectory1", "subdirectory2", "derp").toFile();
        String testValue = "test";
        boolean result = FileUtils.doesDirectoryEndWithSegment(directory, testValue);
        assertFalse(result);
    }

    @Test
    void doesDirectoryEndWithSegment_shouldReturnWhenIncludedButNotFinalSegment() {
    File directory = Path.of("root", "subdirectory1", "test", "subdirectory2").toFile();
    String testValue = "test";
    boolean result = FileUtils.doesDirectoryEndWithSegment(directory, testValue);
    assertFalse(result);
    }
}