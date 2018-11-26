package com.service;

import java.util.List;

import com.pojo.Dispatcher;

public interface DispatcherServ{
	
	Dispatcher getDispatcherById(int id);
	
	List<Dispatcher> getAllDispatchers();
	
	boolean addDispatcher(Dispatcher disp);
	
	boolean addDispatcher(String name, int type);
	
	boolean updateDispatcher(Dispatcher disp);
	
	boolean deleteDispatcher(int id);
	
	boolean patchaddDispatchers(List<String> names);
	
	boolean patchDeleteDispatchers(List<String> names);

}
