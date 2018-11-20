package com.service;

import com.pojo.User;

public interface UserServ {
	
	User getUserById(int id);
	
	boolean addUser(User user);
	
}
