package com.xju.config;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.service.UserServ;

@Component
public class LoginIntercepter implements HandlerInterceptor {
	
	@Autowired
	private UserServ userServ;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o)
			throws Exception {
		HttpSession session = request.getSession();
		if (request.getRequestURI().equals("/user/login")) {
			String name=request.getParameter("loginName");
			String pass=request.getParameter("loginPass");
			
			if (name!=null&&pass!=null&&checkUser(name,pass)) {
				session.setAttribute(session.getId(), true);
				return true;
			}
		}else {
			Object check=session.getAttribute(session.getId());
			if(check!=null) {
				if ((boolean) check==true) {
					return true;
				}
			}
			
		}
		return false;
	}
	
	private boolean checkUser(String loginName,String loginPass) {
		boolean flag=userServ.checkLogin(loginName, loginPass);
		if (flag) {
			System.out.println("用户名密码验证通过");
			return true;
		}
		return false;
	}
}
