package com.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.DispatcherDao;
import com.pojo.Dispatcher;
import com.service.DispatcherServ;

@Service
public class DispatcherServImpl implements DispatcherServ {
	
	@Autowired
	private DispatcherDao dispatcherDao;

	@Override
	public Dispatcher getDispatcherById(int id) {
		return dispatcherDao.getDispatcher(id);
	}

	@Override
	public List<Dispatcher> getAllDispatchers() {
		return dispatcherDao.getAll();
	}

	@Override
	public boolean addDispatcher(Dispatcher disp) {
		int change=dispatcherDao.addDispatcher(disp);
		if(change==1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateDispatcher(Dispatcher disp) {
		int change=dispatcherDao.updateDispatcher(disp);
		if(change==1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteDispatcher(int id) {
		int change=dispatcherDao.deleteDispatcher(id);
		if(change==1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean patchaddDispatchers(List<String> names) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean patchDeleteDispatchers(List<String> names) {
		if(names.size()>0) {
			int change=dispatcherDao.deleteDispatchers(names);
			if(change>0) {
				return true;
			}
		}	
		return false;
	}

	@Override
	public boolean addDispatcher(String name, int type) {
		Dispatcher dispatcher=new Dispatcher(name, type);
		
		return addDispatcher(dispatcher);
	}

}
