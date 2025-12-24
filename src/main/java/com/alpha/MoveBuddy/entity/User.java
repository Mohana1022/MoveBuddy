package com.alpha.MoveBuddy.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // ✅ renamed from "user"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private long usermobileNo;
    private String userPassword;
    private String role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id")   // ✅ explicit FK column
    private Driver driver;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id") // ✅ explicit FK column
    private Customer customer;

    public User() {}

    public User(int userId, long usermobileNo, String userPassword, String role,
                Driver driver, Customer customer) {
        this.userId = userId;
        this.usermobileNo = usermobileNo;
        this.userPassword = userPassword;
        this.role = role;
        this.driver = driver;
        this.customer = customer;
    }

	@Override
	public String toString() {
		return "User [userId=" + userId + ", usermobileNo=" + usermobileNo + ", userPassword=" + userPassword
				+ ", role=" + role + ", driver=" + driver + ", customer=" + customer + "]";
	}

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

    
    
}
