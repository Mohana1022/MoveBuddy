package com.alpha.MoveBuddy.DTO;

public class RegisterDriverVehicleDTO {

	private long licenseNo;
	private String upiID;
	private String driverName;
	private int age;
	private long MobileNo;
	private String gender;
	private String mailId;
	private String VehicleName;
	private String VehicleNo;
	private String VehicleType;
	private String model;
	private int VehicleCapacity;
	private double latitude;
	private int longitude;
	private int pricePerKM;
	public long getLicenseNo() {
		return licenseNo;
	}
	public void setLicenseNo(long licenseNo) {
		this.licenseNo = licenseNo;
	}
	public String getUpiID() {
		return upiID;
	}
	public void setUpiID(String upiID) {
		this.upiID = upiID;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public long getMobileNo() {
		return MobileNo;
	}
	public void setMobileNo(long mobileNo) {
		MobileNo = mobileNo;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMailId() {
		return mailId;
	}
	public void setMailId(String mailId) {
		this.mailId = mailId;
	}
	public String getVehicleName() {
		return VehicleName;
	}
	public void setVehicleName(String vehicleName) {
		VehicleName = vehicleName;
	}
	public String getVehicleNo() {
		return VehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		VehicleNo = vehicleNo;
	}
	public String getVehicleType() {
		return VehicleType;
	}
	public void setVehicleType(String vehicleType) {
		VehicleType = vehicleType;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public int getVehicleCapacity() {
		return VehicleCapacity;
	}
	public void setVehicleCapacity(int vehicleCapacity) {
		VehicleCapacity = vehicleCapacity;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public int getLongitude() {
		return longitude;
	}
	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}
	public int getPricePerKM() {
		return pricePerKM;
	}
	public void setPricePerKM(int pricePerKM) {
		this.pricePerKM = pricePerKM;
	}
	public RegisterDriverVehicleDTO(long licenseNo, String upiID, String driverName, int age, long mobileNo,
			String gender, String mailId, String vehicleName, String vehicleNo, String vehicleType, String model,
			int vehicleCapacity, double latitude, int longitude, int pricePerKM) {
		super();
		this.licenseNo = licenseNo;
		this.upiID = upiID;
		this.driverName = driverName;
		this.age = age;
		MobileNo = mobileNo;
		this.gender = gender;
		this.mailId = mailId;
		VehicleName = vehicleName;
		VehicleNo = vehicleNo;
		VehicleType = vehicleType;
		this.model = model;
		VehicleCapacity = vehicleCapacity;
		this.latitude = latitude;
		this.longitude = longitude;
		this.pricePerKM = pricePerKM;
	}
	public RegisterDriverVehicleDTO() {
		super();
	}
	@Override
	public String toString() {
		return "RegisterDriverVehicleDTO [licenseNo=" + licenseNo + ", upiID=" + upiID + ", driverName=" + driverName
				+ ", age=" + age + ", MobileNo=" + MobileNo + ", gender=" + gender + ", mailId=" + mailId
				+ ", VehicleName=" + VehicleName + ", VehicleNo=" + VehicleNo + ", VehicleType=" + VehicleType
				+ ", model=" + model + ", VehicleCapacity=" + VehicleCapacity + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", pricePerKM=" + pricePerKM + "]";
	}
	
	
		
	
}
