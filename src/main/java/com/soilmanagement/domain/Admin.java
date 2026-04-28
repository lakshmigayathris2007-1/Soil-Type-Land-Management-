package com.soilmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    @Column(nullable = false, length = 50)
    private String department;
    @Column(nullable = false, length = 30)
    private String employeeCode;
    @Column(length = 50)
    private String designation;
    @Column(nullable = false)
    private Integer accessLevel;
    @Column(length = 100)
    private String officeLocation;
    @Column
    private LocalDateTime lastAuditTime;
    @Column(length = 200)
    private String authorizedModules;
    @Column
    private Boolean canApproveSchemes;
    @Column
    private Boolean canModifySoilStandards;
    @Column
    private Integer maxFarmersManaged;
    @Column(length = 20)
    private String supervisorCode;

    public Admin() {
        super();
        this.accessLevel = 1;
        this.canApproveSchemes = false;
        this.canModifySoilStandards = false;
    }

    public Admin(String username, String passwordHash, String email, String phoneNumber,
                 String fullName, String address, String aadharNumber,
                 String department, String employeeCode, String designation, Integer accessLevel) {
        super(username, passwordHash, email, phoneNumber, fullName, address, aadharNumber, "ADMIN");
        this.department = department;
        this.employeeCode = employeeCode;
        this.designation = designation;
        this.accessLevel = accessLevel;
    }

    @Override
    public boolean authenticate(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < 8) return false;
        return this.getPasswordHash().equals(String.valueOf(rawPassword.hashCode())) && this.accessLevel > 0;
    }

    @Override
    public String getDashboardSummary() {
        return "Admin: " + getFullName() + " | Dept: " + department + " | Level: " + accessLevel;
    }

    public Set<String> getAuthorizedModuleSet() {
        Set<String> modules = new HashSet<>();
        if (authorizedModules != null && !authorizedModules.isEmpty()) {
            for (String mod : authorizedModules.split(",")) {
                modules.add(mod.trim());
            }
        }
        return modules;
    }

    public boolean hasPermission(String moduleName) {
        return getAuthorizedModuleSet().contains(moduleName);
    }

    public String generateAuditEntry(String action, String targetEntity) {
        this.lastAuditTime = LocalDateTime.now();
        return "[AUDIT] " + employeeCode + " | " + action + " on " + targetEntity + " at " + lastAuditTime;
    }

    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public Integer getAccessLevel() { return accessLevel; }
    public void setAccessLevel(Integer accessLevel) { this.accessLevel = accessLevel; }
    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }
    public LocalDateTime getLastAuditTime() { return lastAuditTime; }
    public void setLastAuditTime(LocalDateTime lastAuditTime) { this.lastAuditTime = lastAuditTime; }
    public String getAuthorizedModules() { return authorizedModules; }
    public void setAuthorizedModules(String authorizedModules) { this.authorizedModules = authorizedModules; }
    public Boolean getCanApproveSchemes() { return canApproveSchemes; }
    public void setCanApproveSchemes(Boolean canApproveSchemes) { this.canApproveSchemes = canApproveSchemes; }
    public Boolean getCanModifySoilStandards() { return canModifySoilStandards; }
    public void setCanModifySoilStandards(Boolean v) { this.canModifySoilStandards = v; }
    public Integer getMaxFarmersManaged() { return maxFarmersManaged; }
    public void setMaxFarmersManaged(Integer maxFarmersManaged) { this.maxFarmersManaged = maxFarmersManaged; }
    public String getSupervisorCode() { return supervisorCode; }
    public void setSupervisorCode(String supervisorCode) { this.supervisorCode = supervisorCode; }
}
