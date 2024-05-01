package it.project.bean;

public class User {
	private String username;
	private boolean isAdmin;

	public User() {
		this.username = null;
		this.isAdmin = false;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}

	public boolean isAdmin() {
		return this.isAdmin;
	}

	public void setRole(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
}
