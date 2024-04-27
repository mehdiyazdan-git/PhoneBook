package com.pishgaman.phonebook.enums;

public enum BackupLocation {
    WINDOWS("C:\\Backups\\MyApp"),
    LINUX("/var/backups/MyApp"),
    MAC("/Library/Application Support/MyApp/Backups");

    private final String path;

    BackupLocation(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    /**
     * Gets the default backup location based on the current operating system.
     *
     * @return The default backup location as a String.
     */
    public static String getDefaultLocation() {
        String osName = System.getProperty("os.name").toLowerCase();
        for (BackupLocation location : BackupLocation.values()) {
            if (osName.startsWith(location.name().toLowerCase())) {
                return location.getPath();
            }
        }
        // Default to user home directory if OS not matched
        return System.getProperty("user.home") + "//backups//MyApp";
    }
}

