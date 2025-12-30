package com.alpha.MoveBuddy.DTO;

public class LoginRequestDTO {

	private long mobileNo;
	private String password;
	
	public long getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public LoginRequestDTO(long mobileNo, String password) {
		super();
		this.mobileNo = mobileNo;
		this.password = password;
	}
	public LoginRequestDTO() {
		super();
	}
	@Override
	public String toString() {
		return "LoginRequestDTO [mobileNo=" + mobileNo + ", password=" + password + "]";
	}
	
	
}
