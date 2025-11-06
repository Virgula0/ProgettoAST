package com.rosa.angelo.progetto.ast.model;

import java.util.Objects;

public class User {
	private int id;
	private String username;
	private String password;
	
	public User(String username, String password, int id) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, getUsername());
	}

	// 2 users are equal if the id and username are equal
	// we don't care about the password
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		return id == other.id && Objects.equals(getUsername(), other.getUsername());
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + getUsername() + "]";
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public int getId() {
		return id;
	}
}
