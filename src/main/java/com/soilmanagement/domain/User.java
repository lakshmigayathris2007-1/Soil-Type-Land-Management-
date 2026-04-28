package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @Column
    private LocalDateTime lastLoginAt;

    @Column(nullable = false)
    private boolean isActive;

    @Column(length = 50)
    private String preferredLanguage;

    @Column(length = 20)
    private String aadharNumber;

    @Column(length = 30)
    private String userRole;

    public User() {
        this.registeredAt = LocalDateTime.now();
        this.isActive = true;
        this.preferredLanguage = "English";
    }

    public User(String username, String passwordHash, String email, String phoneNumber,
                String fullName, String address, String aadharNumber, String userRole) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.address = address;
        this.aadharNumber = aadharNumber;
        this.userRole = userRole;
    }

    public abstract boolean authenticate(String rawPassword);
    public abstract String getDashboardSummary();

    public boolean isAccountStale(int maxInactiveDays) {
        if (lastLoginAt == null) return true;
        return lastLoginAt.plusDays(maxInactiveDays).isBefore(LocalDateTime.now());
    }

    public void updateContact(String newPhone) { this.phoneNumber = newPhone; }
    public void updateContact(String newPhone, String newEmail) {
        this.phoneNumber = newPhone;
        this.email = newEmail;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    public String getAadharNumber() { return aadharNumber; }
    public void setAadharNumber(String aadharNumber) { this.aadharNumber = aadharNumber; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + userRole + "'}";
    }
}
