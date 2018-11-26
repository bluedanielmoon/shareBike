package com.serviceImpl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.pojo.Dispatcher;
import com.service.DispatcherServ;
import com.xju.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class DispatcherServImplTest {
	@Autowired
	private DispatcherServ dispatcher;

	@Test
	public void testGetDispatcherById() {

		System.out.println(dispatcher.getDispatcherById(1));
	}

	@Test
	public void testGetAllDispatchers() {
		List<Dispatcher> ls = dispatcher.getAllDispatchers();
		System.out.println(ls);
	}

	@Test
	public void testAddDispatcher() {
		Dispatcher disp = new Dispatcher("kache1", 1);

		dispatcher.addDispatcher(disp);
	}

	@Test
	public void testUpdateDispatcher() {
		Dispatcher disp = dispatcher.getDispatcherById(1);
		disp.setName("三轮车");
		dispatcher.updateDispatcher(disp);
	}

	@Test
	public void testDeleteDispatcher() {
		dispatcher.deleteDispatcher(1);
	}

	@Test
	public void testPatchaddDispatchers() {
		fail("Not yet implemented");
	}

	@Test
	public void testPatchDeleteDispatchers() {
		List<Dispatcher> dispatchers=dispatcher.getAllDispatchers();
		List<String> names=new ArrayList<>();
		for(Dispatcher d:dispatchers) {
			names.add(d.getName());
		}
		dispatcher.patchDeleteDispatchers(names);
	}

}
