package com.gbroche.archiver.enums;

public enum ConflictStrategy {
    SKIP("Skip existing", "-aos", "-o-"),
    OVERWRITE("Overwrite all", "-aoa", "-o+"),
    RENAME_EXTRACTED("Auto rename extracted", "-aou", null), // fallback to skip for rar
    RENAME_EXISTING("Auto rename existing", "-aot", null);  // fallback to skip for rar

    public final String label;
    public final String flagSevenZip;
    public final String flagUnrar;

    ConflictStrategy(String label, String flagSevenZip, String flagUnrar) {
        this.label = label;
        this.flagSevenZip = flagSevenZip;
        this.flagUnrar = flagUnrar;
    }

    @Override
    public String toString() { return label; }
}