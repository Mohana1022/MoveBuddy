package com.alpha.MoveBuddy.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;

@Entity
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	private int age;
	private String gender;
	private long mobileNo;
	private String emailId;
	private String currentLoc;
	
	@Column(nullable = false)
	private int penality = 0;
	
	@Column(name="bookingflag")
	private boolean bookingflag = false;
	
	@OneToMany(mappedBy = "customer")
	@JsonIgnore
	private List<Booking> bookinglist = new ArrayList<>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getCurrentLoc() {
		return currentLoc;
	}

	public void setCurrentLoc(String currentLoc) {
		this.currentLoc = currentLoc;
	}

	public int getPenality() {
		return penality;
	}

	public void setPenality(int penality) {
		this.penality = penality;
	}

	public boolean isBookingflag() {
		return bookingflag;
	}

	public void setBookingflag(boolean bookingflag) {
		this.bookingflag = bookingflag;
	}

	public List<Booking> getBookinglist() {
		return bookinglist;
	}

	public void setBookinglist(List<Booking> bookinglist) {
		this.bookinglist = bookinglist;
	}

	public Customer(String name, int age, String gender, long mobileNo, String emailId, String currentLoc, int penality,
			boolean bookingflag, List<Booking> bookinglist) {
		super();
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.mobileNo = mobileNo;
		this.emailId = emailId;
		this.currentLoc = currentLoc;
		this.penality = penality;
		this.bookingflag = bookingflag;
		this.bookinglist = bookinglist;
	}

	public Customer() {
		super();
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + ", age=" + age + ", gender=" + gender + ", mobileNo="
				+ mobileNo + ", emailId=" + emailId + ", currentLoc=" + currentLoc + ", penality=" + penality
				+ ", bookingflag=" + bookingflag + ", bookinglist=" + bookinglist + "]";
	}

	
	
}
