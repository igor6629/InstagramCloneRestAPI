package com.example.instagramclone.api.models;

import jakarta.validation.constraints.*;

public class RegistrationBody {
    @Size(min = 3, max = 20, message = "Username should be between 3 and 20 characters")
    @NotBlank(message = "username should not be null")
    @NotNull(message = "username should not be null")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "email should not be null")
    @NotNull(message = "email should not be null")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "Password should contain minimum eight characters, at least one uppercase letter, one lowercase letter and one number")
    @NotBlank(message = "password should not be null")
    @NotNull(message = "password should not be null")
    private String password;

    private String bio;

    @Size(min = 5, message = "Profile picture URL should be more than 5 characters")
    @NotBlank(message = "profilePicture should not be null")
    @NotNull(message = "profilePicture should not be null")
    private String profilePicture;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
