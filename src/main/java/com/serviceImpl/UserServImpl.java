package com.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.UserDao;
import com.pojo.User;
import com.service.UserServ;


@Service
public class UserServImpl implements UserServ{
	
	@Autowired
	private UserDao userDao;

	@Override
	public User getUserById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addUser(User user) {
		
		userDao.addUser(user);
		return false;
	}

}
