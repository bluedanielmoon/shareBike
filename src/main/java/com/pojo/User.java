package com.pojo;

import java.util.Date;

import org.springframework.stereotype.Repository;

@Repository
public class User {
	private Integer id;
	private String userName;
	private String password;
	private int type;
	private Date createTime;
	private Date updateTime;

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String userName, String password, int type) {
		super();
		this.userName = userName;
		this.password = password;
		this.type = type;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", password=" + password + ", type=" + type
				+ ", createTime=" + createTime + ", updateTime=" + updateTime + "]";
	}

}
