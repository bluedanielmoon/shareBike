package com.service;

import java.util.List;

import com.pojo.User;

public interface UserServ {
	
	User getUserById(int id);
	
	List<User> getAllUsers();
	
	boolean addUser(User user);
	
	boolean addUser(String userName,String password);
	
	boolean updateUser(User user);
	
	boolean deleteUser(int id);
	
	boolean patchDeleteUser(List<String> names);

	boolean checkLogin(String userName,String passWord);
	
}
