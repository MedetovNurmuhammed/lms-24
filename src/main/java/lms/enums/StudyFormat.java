package lms.enums;

public enum StudyFormat {
    OFFLINE,
    ONLINE;
    public static StudyFormat fromString(String format) {
        if (format != null) {
            for (StudyFormat sf : StudyFormat.values()) {
                if (format.equalsIgnoreCase(sf.name())) {
                    return sf;
                }
            }
        }
        return null;
    }
}
