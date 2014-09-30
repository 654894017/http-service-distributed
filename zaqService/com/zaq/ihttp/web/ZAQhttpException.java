package com.zaq.ihttp.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

/**
 * httpservice异常类
 * @author zaqzaq
 *
 */
public class ZAQhttpException extends RuntimeException{

	public ZAQhttpException(Logger logger,String s, Throwable throwable) {
		super(s+throwable.getMessage(), throwable);
		
		logger.error(s, throwable);
	}

	public ZAQhttpException(Logger logger,String s) {
		super(s);
		logger.error(s);
	}
	public ZAQhttpException(String s) {
		super(s);
	}
	public ZAQhttpException(Logger logger,Throwable throwable) {
		super(throwable);
		logger.error(throwable.getMessage());
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Gson gson=new Gson();
		Object ss=new RetObj<Integer>("123123");
		System.out.println(ss.getClass());
		System.out.println(ZAQhttpException.class.getMethod("query", HttpServletRequest.class).getGenericReturnType());
		System.out.println(gson.toJson(ss,ZAQhttpException.class.getMethod("query", HttpServletRequest.class).getGenericReturnType()));;
	}
	
	public RetObj<Integer> query(HttpServletRequest request){
		return new RetObj<Integer>(123);
		
	}
}
