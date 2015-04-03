package com.ezhuang.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "current_user")
public class CurrentUser extends Model{

	private String global_key; // 用户id
	private String phone;
	private String password;
	private String avatar;// 头像
	private String name;
	private String email;
	private String wxId;
	private String createTime;
	private String companyId;// 公司ID
	private Integer companyType;// 公司类型（0 装修公司 1材料商）
    private String companyName;
	private String status;//
	private Integer isActive;// (0未激活 1激活)

	private List<Role> role;//

	public CurrentUser(String global_key, String phone, String password,
                       String avatar, String name, String email, String wxId,
                       String createTime, String companyId, Integer companyType,
                       String status, Integer isActive, List<Role> role) {
		super();
		this.global_key = global_key;
		this.phone = phone;
		this.password = password;
		this.avatar = avatar;
		this.name = name;
		this.email = email;
		this.wxId = wxId;
		this.createTime = createTime;
		this.companyId = companyId;
		this.companyType = companyType;
		this.status = status;
		this.isActive = isActive;
		this.role = role;
	}

	public CurrentUser() {
		super();
	}

	public String getGlobal_key() {
		return global_key;
	}

	public void setGlobal_key(String global_key) {
		this.global_key = global_key;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWxId() {
		return wxId;
	}

	public void setWxId(String wxId) {
		this.wxId = wxId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public Integer getCompanyType() {
		return companyType;
	}

	public void setCompanyType(Integer companyType) {
		this.companyType = companyType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

    public String getCompanyName() { return companyName; }

    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
