package com.sliit.vehiclerentalsystem01.user.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Base class for Customer and Staff
public abstract class User {

    private String id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    private String profilePicture = "N/A"; // image file name or N/A

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

    public abstract String getUserRole();

    public abstract String toCSV();

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

    // Profile picture file name for the web page
    public boolean hasAvatar() {
        return profilePicture != null
                && !profilePicture.isBlank()
                && !"N/A".equalsIgnoreCase(profilePicture);
    }

    public String getAvatarUrl() {
        return hasAvatar() ? "/avatars/" + profilePicture : null;
    }
}
