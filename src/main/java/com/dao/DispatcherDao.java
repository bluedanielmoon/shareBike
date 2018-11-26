package com.dao;

import java.util.List;

import com.pojo.Dispatcher;

public interface DispatcherDao{
	
	
	Dispatcher getDispatcher(int id);
	
	int addDispatcher(Dispatcher disp);
	
	List<Dispatcher> getAll();
	
	int updateDispatcher(Dispatcher disp);
	
	int deleteDispatcher(int id);
	
	int deleteDispatchers(List<String> names);

}