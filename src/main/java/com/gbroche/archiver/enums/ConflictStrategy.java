package com.gbroche.archiver.enums;

public enum ConflictStrategy {
    OVERWRITE("Overwrite all", "-aoa"),
    SKIP("Skip existing", "-aos"),
    RENAME_EXTRACTED("Auto rename extracted", "-aou");

    public final String label;
    public final String flag;

    ConflictStrategy(String label, String flag) {
        this.label = label;
        this.flag = flag;
    }

    @Override
    public String toString() { return label; }
}