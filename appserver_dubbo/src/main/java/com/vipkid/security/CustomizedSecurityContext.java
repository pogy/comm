/*
package com.vipkid.security;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import com.vipkid.model.Role;

public class CustomizedSecurityContext implements SecurityContext {
	private final CustomizedPrincipal principal;

	public CustomizedSecurityContext(CustomizedPrincipal principal) {
		this.principal = principal;
	}

	@Override
	public Principal getUserPrincipal() {
		return principal;
	}

	@Override
	public boolean isUserInRole(String role) {
		if (principal == null) {
			return false;
		} else {
			if(principal.getUser().getRoleSet().contains(Role.valueOf(role))) {
				return true;
			}else {
				return false;
			}
		}
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public String getAuthenticationScheme() {
		return null;
	}
}
*/
