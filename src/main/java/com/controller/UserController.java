package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pojo.User;
import com.service.UserServ;

import net.bytebuddy.asm.Advice.Return;

/**
 * 列举所有的controller的标准形式
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {
	
	@Autowired
	private UserServ userServ;
	
	@GetMapping(value = "/all")
	@ResponseBody
	public List<User> getAll() {
		return userServ.getAllUsers();
	}

	@GetMapping(value = "/add")
	@ResponseBody
	public boolean getUser(@RequestParam String userName,@RequestParam String password) {
		return userServ.addUser(userName, password);
	}

	@PostMapping(value = "/delete")
	@ResponseBody
	public boolean postUser(@RequestParam List<String> names) {
		System.out.println(names);
		return userServ.patchDeleteUser(names);
	}

	@PutMapping(value = "/{user}")
	/**
	 * 修改
	 * 
	 * @param user
	 * @return
	 */
	public String updateUser(@PathVariable Long user) {
		return "update" + user;
	}

	@DeleteMapping(value = "/{user}")
	public String deleteUser(@PathVariable Long user) {
		return "delete" + user;
	}

	@PostMapping("/userParam")
	// default=如果传来的参数不正确，就让userName称为defaultValue
	// http://localhost:8080/boot/web/userPram?uname=dhh
	// 可以接受路径上的，也可以是body里面的
	public String getAllUserParam(@RequestParam(defaultValue = "danny", name = "uname") String userName) {
		return "test " + userName + " is here";
	}

	@PostMapping(path = "/po", consumes = "application/json")
	public String postUser(@RequestBody User user) {
		System.out.println(user);
		return user + " json class is posted";
	}

	// 默认去往text/plain,即时是json格式字符串，如果不指明，也用text处理
	@PostMapping(path = "/po", consumes = "text/plain")
	public String postUser(@RequestBody String user) {
		System.out.println(user);
		return user + " text string is posted";
	}

}