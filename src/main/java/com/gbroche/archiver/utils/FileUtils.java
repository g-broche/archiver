package com.gbroche.archiver.utils;

import com.gbroche.archiver.enums.Extension;

import java.io.File;
import java.nio.file.Path;

public class FileUtils {

    public static String getFileExtension(File file) {
        if(file == null) return null;
        String name = file.getName();
        int extensionIndex = name.lastIndexOf('.');
        return extensionIndex >= 0 ? name.substring(extensionIndex + 1) : "";
    }

    public static String getFileNameWithoutExtension(File file) {
        if(file == null) return null;
        String name = file.getName();
        int extensionIndex = name.lastIndexOf('.');
        return extensionIndex >= 0 ? name.substring(0, extensionIndex) : name;
    }

    public static boolean isDirectoryDirectParentOfFile(File candidateParent, File candidateChild){
        Path childPath = candidateChild.toPath().toAbsolutePath().normalize();
        Path directParent = childPath.getParent();
        if (directParent == null) return false;
        Path parentPath = candidateParent.toPath().toAbsolutePath().normalize();
        return directParent.equals(parentPath);
    }


    public static boolean doesDirectoryEndWithSegment(File fullDirectory, String endSegment){
        return fullDirectory.toPath()
                .normalize()
                .getFileName()
                .toString()
                .equals(endSegment);
    }

    public static boolean deleteDirectory(File directory) {
        return directory.delete();
    }

    public static Extension getExtensionEnumFromFile(File file){
        if (file == null) return null;
        String rawExtension = FileUtils.getFileExtension(file);
        return switch (rawExtension) {
            case "7z" -> {
                yield Extension.SEVEN_ZIP;
            }
            case "zip" -> {
                yield Extension.ZIP;
            }
            case "rar" -> {
                yield Extension.RAR;
            }
            default -> null;
        };
    }
}
