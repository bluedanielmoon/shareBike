package com.dao;

import com.pojo.User;

public interface UserDao{
	
	
	User getUser(int id);
	
	int addUser(User user);
	
	//boolean addList(List<Integer> list);
	
	int updateUser(User user);
	
	int deleteUser(int id);

}
