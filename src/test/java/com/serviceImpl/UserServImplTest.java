package com.serviceImpl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pojo.User;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class UserServImplTest {
	
	@Autowired
	private UserServImpl userServ;

	@Test
	public void testGetUserById() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddUser() {
		
		User user=new User("hul","123");
		userServ.addUser(user);
	}

}
