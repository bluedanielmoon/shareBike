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

import com.pojo.Poi;
import com.pojo.User;
import com.service.PoiServ;
import com.service.UserServ;

import net.bytebuddy.asm.Advice.Return;

/**
 * 列举所有的controller的标准形式
 * 
 * @author Administrator
 *
 */
@RestController
@RequestMapping(value = "/poi")
public class PoiController {
	
	@Autowired
	private PoiServ poiServ;
	
	@GetMapping(value = "/all")
	@ResponseBody
	public List<Poi> getAll() {
		return poiServ.getAllPois();
	}

	@GetMapping(value = "/add")
	@ResponseBody
	public boolean getUser(@RequestParam String name,@RequestParam int type,@RequestParam double lng,@RequestParam  double lat) {
		return poiServ.addPoi(name, type, lng, lat);
	}

	@PostMapping(value = "/delete")
	@ResponseBody
	public boolean postUser(@RequestParam List<String> names) {
		System.out.println(names);
		return false;
	}

	@PutMapping(value = "/{user}")
	public String updateUser(@PathVariable Long user) {
		return "update" + user;
	}


}