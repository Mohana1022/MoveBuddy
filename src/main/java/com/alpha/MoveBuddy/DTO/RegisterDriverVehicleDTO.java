package com.alpha.MoveBuddy.DTO;

import jakarta.validation.constraints.*;

public class RegisterDriverVehicleDTO {

    @NotNull(message = "License number is required")
    private Long licenseNo;

    @NotBlank(message = "UPI ID is required")
    private String upiID;

    @NotBlank(message = "Driver name is required")
    @Size(min = 2, max = 60, message = "Name must be between 2 and 60 characters")
    private String driverName;

    @Min(value = 18, message = "Driver must be at least 18 years old")
    @Max(value = 65, message = "Driver age must not exceed 65")
    private Integer age;

    @Min(value = 1000000000L, message = "Mobile number must be 10 digits")
    @Max(value = 9999999999L, message = "Mobile number must be 10 digits")
    private long mobileNo;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String mailId;

    @NotBlank(message = "Vehicle name is required")
    private String vehicleName;

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNo;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    @NotBlank(message = "Vehicle model is required")
    private String model;

    @Min(value = 1, message = "Vehicle capacity must be at least 1")
    private Integer vehicleCapacity;

    @NotBlank(message = "Latitude is required")
    private String latitude;

    @NotBlank(message = "Longitude is required")
    private String longitude;

    @Min(value = 5, message = "Price per KM must be at least 5")
    private Integer pricePerKM;

    @Min(value = 10, message = "Average speed must be at least 10 km/h")
    private int averageSpeed;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public RegisterDriverVehicleDTO() {}

    public Long getLicenseNo() { return licenseNo; }
    public void setLicenseNo(Long licenseNo) { this.licenseNo = licenseNo; }
    public String getUpiID() { return upiID; }
    public void setUpiID(String upiID) { this.upiID = upiID; }
    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public long getMobileNo() { return mobileNo; }
    public void setMobileNo(long mobileNo) { this.mobileNo = mobileNo; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getMailId() { return mailId; }
    public void setMailId(String mailId) { this.mailId = mailId; }
    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getVehicleCapacity() { return vehicleCapacity; }
    public void setVehicleCapacity(Integer vehicleCapacity) { this.vehicleCapacity = vehicleCapacity; }
    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    public Integer getPricePerKM() { return pricePerKM; }
    public void setPricePerKM(Integer pricePerKM) { this.pricePerKM = pricePerKM; }
    public int getAverageSpeed() { return averageSpeed; }
    public void setAverageSpeed(int averageSpeed) { this.averageSpeed = averageSpeed; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
