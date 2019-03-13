package com.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.UserDao;
import com.pojo.User;
import com.service.UserServ;

@Service
public class UserServImpl implements UserServ {

	@Autowired
	private UserDao userDao;

	@Override
	public User getUserById(int id) {
		return userDao.getUser(id);
	}

	@Override
	public boolean addUser(User user) {

		int change = userDao.addUser(user);
		if (change == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addUser(String userName, String password,int type) {
		User user = new User(userName, password,type);
		int change = userDao.addUser(user);
		if (change == 1) {
			return true;
		}
		return false;
	}

	@Override
	public List<User> getAllUsers() {
		return userDao.getAll();
	}

	@Override
	public boolean updateUser(User user) {
		int change = userDao.updateUser(user);
		if (change == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteUser(int id) {
		int change = userDao.deleteUser(id);
		if (change == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean patchDeleteUser(List<String> names) {
		if (names.size() > 0) {
			int change = userDao.deleteUsers(names);
			if (change > 0) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int checkLogin(String userName,String passWord) {
		User user=userDao.getUserByName(userName);
		String rightPass=user.getPassword();
		if (rightPass.equals(passWord)) {
			if(user.getType()==0) {
				return 0;
			}
			return 1;
		}
		
		return -1;
	}

}
