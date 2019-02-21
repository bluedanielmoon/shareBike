package com.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.pojo.ForbidArea;
import com.service.ForbidAreaServ;

@RestController
@RequestMapping(value = "/forbid")
public class ForbidController {

	@Autowired
	private ForbidAreaServ forbidServ;
	
	@GetMapping(value = "/all")
	@ResponseBody
	public List<ForbidArea> getAll() {
		return forbidServ.getAllAreas();
	}
	
	@GetMapping(value = "/update")
	@ResponseBody
	public boolean updateForbid(@RequestParam int id,@RequestParam String name) {
		return forbidServ.updateArea(id,name);
	}
	
	@PostMapping(value = "/add")
	@ResponseBody
	public boolean getSite(@RequestParam String name,@RequestParam List<String> paths) {
		return forbidServ.addArea(name, paths);
	}

	@PostMapping(value = "/delete")
	@ResponseBody
	public boolean postSite(@RequestParam List<Integer> ids) {
		return forbidServ.patchDeleteAreas(ids);
	}
	
}
