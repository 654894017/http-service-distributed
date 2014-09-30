package com.zaq.ihttp.web.client;

import com.zaq.ihttp.web.IcallBack;
/**
 * 逗B类
 * @author zaqzaq
 *
 */
public class SimpleCallBack implements IcallBack{

	@Override
	public Object call() {
		
		System.out.println("我只是个例子,just so so");
		return null;
	}

}
