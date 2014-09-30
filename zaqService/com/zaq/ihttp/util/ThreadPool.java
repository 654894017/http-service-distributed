package com.zaq.ihttp.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 控制线程量
 * @author zaqzaq
 */
public class ThreadPool {

	private static Executor executor;//消息解析线程池
	
	static {
		executor = Executors.newFixedThreadPool(Integer.valueOf(HttpServiceConf.getPro("THREADPOOL_SIZE","50")));
	}

	public static void execute(Runnable run) {
		executor.execute(run);
	}
}
