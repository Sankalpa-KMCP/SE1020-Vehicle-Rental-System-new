package com.sliit.vehiclerentalsystem01.user.entity;

/**
 * Abstract base for all system users. Demonstrates abstraction and encapsulation.
 */
public abstract class User {

    private String id;
    private String name;
    private String email;
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

    public void setProfilePicture(String profilePicture) {
        if (profilePicture == null || profilePicture.isBlank()) {
            this.profilePicture = "N/A";
        } else {
            this.profilePicture = profilePicture.trim();
        }
    }

    public boolean hasAvatar() {
        return profilePicture != null
                && !profilePicture.isBlank()
                && !"N/A".equalsIgnoreCase(profilePicture);
    }

    public String getAvatarUrl() {
        return hasAvatar() ? "/avatars/" + profilePicture : null;
    }
}
