package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pojo.Dispatcher;
import com.service.DispatcherServ;

/**
 * 列举所有的controller的标准形式
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/disp")
public class DispatcherController {
	
	@Autowired
	private DispatcherServ dispServ;
	
	@GetMapping(value = "/all")
	@ResponseBody
	public List<Dispatcher> getAll() {
		return dispServ.getAllDispatchers();
	}

	@GetMapping(value = "/add")
	@ResponseBody
	public boolean getDispatcher(@RequestParam String dispName,@RequestParam int dispType) {
		return dispServ.addDispatcher(dispName,dispType);
	}

	@PostMapping(value = "/delete")
	@ResponseBody
	public boolean postDispatcher(@RequestParam int id) {
		return dispServ.deleteDispatcher(id);
	}

	@PostMapping(value = "/deletePatch")
	@ResponseBody
	public boolean postUser(@RequestParam List<String> names) {
		return dispServ.patchDeleteDispatchers(names);
	}

}