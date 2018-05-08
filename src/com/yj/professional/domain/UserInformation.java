package com.yj.professional.domain;

/**
 * @author liaoyao
 * 用户信息  表字段信息
 */
public class UserInformation {
	private Integer userId;
	private String userName;
	private String password;
	private String hospitalName;
	@Override
	public String toString() {
		return "UserInformation [userId=" + userId + ", userName=" + userName + ", password=" + password
				+ ", hospitalName=" + hospitalName + "]";
	}
	
	public UserInformation() {
		super();
	}
	
	public UserInformation(Integer userId, String userName, String password) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.password = password;
	}

	public UserInformation(Integer userId, String userName, String password, String hospitalName) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.password = password;
		this.hospitalName = hospitalName;
	}

	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	
}
