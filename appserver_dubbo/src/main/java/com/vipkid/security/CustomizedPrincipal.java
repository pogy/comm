package com.vipkid.security;

import java.security.Principal;

import com.vipkid.model.User;

public class CustomizedPrincipal implements Principal {
	private User user;

	public CustomizedPrincipal(User user) {
		super();
		this.user = user;
	}

	@Override
	public String getName() {
		return user.getRoles().toString();
	}

	public User getUser() {
		return user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (!(obj instanceof CustomizedPrincipal)) {
			return false;
		}

		CustomizedPrincipal other = (CustomizedPrincipal) obj;
		if (getName().equals(other.getName())) {
			return true;
		} else {
			return false;
		}
	}
}
