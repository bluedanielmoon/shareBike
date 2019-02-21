package com.dao;

import java.util.List;

import com.pojo.User;

public interface UserDao {

	User getUser(int id);

	User getUserByName(String userName);

	int addUser(User user);

	List<User> getAll();

	int updateUser(User user);

	int deleteUser(int id);

	int deleteUsers(List<String> names);

}
