package com.gbroche.archiver.utils;

import java.io.File;
import java.nio.file.Path;

public class FileUtils {

    static public String getFileExtension(File file) {
        String name = file.getName();
        int extensionIndex = name.lastIndexOf('.');
        return extensionIndex >= 0 ? name.substring(extensionIndex + 1) : "";
    }

    static public String getFileNameWithoutExtension(File file) {
        String name = file.getName();
        int extensionIndex = name.lastIndexOf('.');
        return extensionIndex >= 0 ? name.substring(0, extensionIndex) : name;
    }

    static public boolean isDirectoryDirectParentOfFile(File candidateParent, File candidateChild){
        Path childPath = candidateChild.toPath().toAbsolutePath().normalize();
        Path directParent = childPath.getParent();
        if (directParent == null) return false;
        Path parentPath = candidateParent.toPath().toAbsolutePath().normalize();
        return directParent.equals(parentPath);
    }


    static public boolean doesDirectoryEndWithSegment(File fullDirectory, String endSegment){
        return fullDirectory.toPath()
                .normalize()
                .getFileName()
                .toString()
                .equals(endSegment);
    }
}
