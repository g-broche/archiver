package com.gbroche.archiver.enums;

import java.util.List;

import java.util.List;

public enum Extension {
    SEVEN_ZIP("7z", List.of(
            ConflictStrategy.SKIP,
            ConflictStrategy.OVERWRITE,
            ConflictStrategy.RENAME_EXTRACTED,
            ConflictStrategy.RENAME_EXISTING
    )),
    ZIP("zip", List.of(
            ConflictStrategy.SKIP,
            ConflictStrategy.OVERWRITE,
            ConflictStrategy.RENAME_EXTRACTED,
            ConflictStrategy.RENAME_EXISTING
    )),
    RAR("rar", List.of(
            ConflictStrategy.SKIP,
            ConflictStrategy.OVERWRITE
    ));

    public final String label;
    public final List<ConflictStrategy> availableConflictStrategies;

    Extension(String label, List<ConflictStrategy> availableConflictStrategies) {
        this.label = label;
        this.availableConflictStrategies = availableConflictStrategies;
    }

    @Override
    public String toString() { return label; }
}