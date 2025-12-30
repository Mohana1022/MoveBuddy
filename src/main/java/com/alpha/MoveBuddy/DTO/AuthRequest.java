package com.alpha.MoveBuddy.DTO;

public class AuthRequest {

    private String username;
    private String password;
    private String role;

    
    // No-arg constructor (IMPORTANT)
    public AuthRequest() {
    }

    // Getters & Setters
    public String getUsername() {
        return username;
    }

    public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	@Override
	public String toString() {
		return "AuthRequest [username=" + username + ", password=" + password + ", role=" + role + "]";
	}
    
    
}

