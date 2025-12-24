package com.alpha.MoveBuddy.entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Users {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userId;
    private long usermobileNo;
    private String userPassword;
    private String role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id")   //  explicit FK column
    private Driver driver;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id") // explicit FK column
    private Customer customer;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getUsermobileNo() {
		return usermobileNo;
	}

	public void setUsermobileNo(long usermobileNo) {
		this.usermobileNo = usermobileNo;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Users(long usermobileNo, String userPassword, String role, Driver driver, Customer customer) {
		super();
		this.usermobileNo = usermobileNo;
		this.userPassword = userPassword;
		this.role = role;
		this.driver = driver;
		this.customer = customer;
	}

	public Users() {
		super();
	}

	@Override
	public String toString() {
		return "Users [userId=" + userId + ", usermobileNo=" + usermobileNo + ", userPassword=" + userPassword
				+ ", role=" + role + ", driver=" + driver + ", customer=" + customer + "]";
	}

	
}
