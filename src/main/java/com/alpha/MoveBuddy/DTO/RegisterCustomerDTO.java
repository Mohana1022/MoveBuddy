package com.alpha.MoveBuddy.DTO;

import jakarta.validation.constraints.*;

public class RegisterCustomerDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 60, message = "Name must be between 2 and 60 characters")
    private String name;

    @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 100, message = "Age must be at most 100")
    private int age;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Min(value = 1000000000L, message = "Mobile number must be 10 digits")
    @Max(value = 9999999999L, message = "Mobile number must be 10 digits")
    private long mobileNo;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String emailId;

    @NotBlank(message = "Latitude is required")
    private String latitude;

    @NotBlank(message = "Longitude is required")
    private String longitude;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public RegisterCustomerDTO() {}

    public RegisterCustomerDTO(String name, int age, String gender, long mobileNo, String emailId,
                               String latitude, String longitude, String password) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.mobileNo = mobileNo;
        this.emailId = emailId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.password = password;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public long getMobileNo() { return mobileNo; }
    public void setMobileNo(long mobileNo) { this.mobileNo = mobileNo; }
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "RegisterCustomerDTO{name='" + name + "', mobileNo=" + mobileNo + "}";
    }
}
