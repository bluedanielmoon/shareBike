package com.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.User;
import com.service.UserServ;
import com.xju.App;

import junit.framework.TestSuite;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class UserServImplTest extends TestSuite{

	@Autowired
	private UserServ userServ;

	@Test
	public void testGetUserById() {
		User user = userServ.getUserById(2);
		System.out.println(user);
	}

	@Test
	public void testAddUser() {
		User user = new User("admin", "123");
		userServ.addUser(user);
	}

	@Test
	public void testGetAllUsers() {
		List<User> ls = userServ.getAllUsers();

		for (User u : ls) {
			System.out.println(u);
		}
	}

	@Test
	public void testUpdateUser() {
		User user = userServ.getUserById(2);
		String name=user.getUserName();
		user.setUserName(name+"_001");
		userServ.updateUser(user);
		System.out.println(userServ.getUserById(2));
	}

	@Test
	public void testDeleteUser() {
		User user = userServ.getUserById(1);
		userServ.deleteUser(user.getId());
	}
	
	@Test
	public void testPatchDeleteUser() {
		
		
		List<User> ls=userServ.getAllUsers();
		List<String> names=new ArrayList<>();
		for(User u:ls) {
			names.add(u.getUserName());
		}
		userServ.patchDeleteUser(names);
	}
	
	

}
