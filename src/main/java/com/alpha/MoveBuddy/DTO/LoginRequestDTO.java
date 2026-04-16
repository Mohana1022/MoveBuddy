package com.alpha.MoveBuddy.DTO;

import jakarta.validation.constraints.*;

public class LoginRequestDTO {

    @Min(value = 1000000000L, message = "Mobile number must be 10 digits")
    @Max(value = 9999999999L, message = "Mobile number must be 10 digits")
    private long mobileNo;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequestDTO() {}

    public long getMobileNo() { return mobileNo; }
    public void setMobileNo(long mobileNo) { this.mobileNo = mobileNo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
