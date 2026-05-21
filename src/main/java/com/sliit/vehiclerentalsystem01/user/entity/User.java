package com.sliit.vehiclerentalsystem01.user.entity;

/**
 * DOMAIN MODEL — abstract parent for every account in the system.
 * <p>
 * WHY abstract?
 * - Customer and Staff share id/name/email but differ in login fields and CSV layout.
 * - Code can use {@code User} references (lists, save method) while runtime objects are subclasses.
 * <p>
 * ENCAPSULATION: fields are private; callers use getters/setters. {@link #normalize(String)} trims input
 * so users.txt does not get accidental spaces.
 * <p>
 * ABSTRACTION: {@link #getUserRole()} and {@link #toCSV()} force each subtype to define how it is stored.
 */
public abstract class User {

    // Shared identity fields stored in users.txt for every user type
    private String id;
    private String name;
    private String email;
    /** Avatar filename under data/avatars/, or "N/A" when none uploaded */
    private String profilePicture = "N/A";

    protected static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    protected User() {
        this("", "", "");
    }

    public User(String id, String name, String email) {
        this.id = normalize(id);
        this.name = normalize(name);
        this.email = normalize(email);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = normalize(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = normalize(name);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = normalize(email);
    }

    /** Role label for polymorphism (e.g. CUSTOMER, STAFF). */
    public abstract String getUserRole();

    /** Serialized form for flat-file storage (CSV). */
    public abstract String toCSV();

    /** Login id; overridden by {@link Customer} and {@link Staff}. */
    public String getUsername() {
        return "";
    }

    public String getPassword() {
        return "";
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    /** Normalizes empty values to "N/A" so the CSV column is always present */
    public void setProfilePicture(String profilePicture) {
        if (profilePicture == null || profilePicture.isBlank()) {
            this.profilePicture = "N/A";
        } else {
            this.profilePicture = profilePicture.trim();
        }
    }

    /*
     * AVATAR helpers — HOW upload works end-to-end:
     * 1. HTML form uses enctype=multipart/form-data and input name avatarImage.
     * 2. ImageStorageService saves bytes to data/avatars/ and calls setProfilePicture(filename).
     * 3. users.txt stores only the filename; browser loads /avatars/filename via WebMvcConfig.
     */
    /** True when a real image file was saved (not N/A) */
    public boolean hasAvatar() {
        return profilePicture != null
                && !profilePicture.isBlank()
                && !"N/A".equalsIgnoreCase(profilePicture);
    }

    /** Web path served by WebMvcConfig from data/avatars/ */
    public String getAvatarUrl() {
        return hasAvatar() ? "/avatars/" + profilePicture : null;
    }
}
