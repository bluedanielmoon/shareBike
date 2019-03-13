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
			
			if (name!=null&&pass!=null) {
				int result=checkUser(name,pass);
				if(result>=0) {
					session.setAttribute(session.getId(), true);
					if(result==0) {
						session.setAttribute("addUser", true);
					}else {
						session.setAttribute("addUser", false);
					}
					return true;
				}
				return false;
			}
		}else if(request.getRequestURI().equals("/user/add")||request.getRequestURI().equals("/user/delete")) {
			Object check=session.getAttribute(session.getId());
			Object canAdd=session.getAttribute("addUser");
			if(check!=null&&canAdd!=null) {
				if ((boolean) check==true&&(boolean)canAdd==true) {
					return true;
				}
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
	
	private int checkUser(String loginName,String loginPass) {

		return userServ.checkLogin(loginName, loginPass);
	}

}
