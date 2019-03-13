package com.service;

import java.util.List;

import com.pojo.User;

public interface UserServ {
	
	User getUserById(int id);
	
	List<User> getAllUsers();
	
	boolean addUser(User user);
	
	boolean addUser(String userName,String password,int type);
	
	boolean updateUser(User user);
	
	boolean deleteUser(int id);
	
	boolean patchDeleteUser(List<String> names);

	/**
	 * 
	 * @param userName
	 * @param passWord
	 * @return 0-管理员，1-普通，-1-错误
	 */
	int checkLogin(String userName,String passWord);
	
}
