package com.ezhuang.model;

import java.io.Serializable;

public class Role implements Serializable {
	private String roleId;
	private String roleName;// ` varchar(20) DEFAULT NULL,

	public Role() {
		super();
	}

	public Role(String roleId, String roleName) {
		super();
		this.roleId = roleId;
		this.roleName = roleName;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
