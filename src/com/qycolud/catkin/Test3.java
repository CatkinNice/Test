package com.qycolud.catkin;

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * 监控本地文件变化
 * @author Catkin_nice
 *
 */
public class Test3 {

	public static void main(String[] args)throws Exception {
		WatchService watchService = FileSystems.getDefault().newWatchService();
		Paths.get("D:/tmp").register(watchService, StandardWatchEventKinds.ENTRY_CREATE, 
				StandardWatchEventKinds.ENTRY_DELETE, 
				StandardWatchEventKinds.ENTRY_MODIFY);
		while (true) {
			WatchKey watchKey = watchService.take();
			for (WatchEvent<?> event : watchKey.pollEvents()) {
				System.out.println(event.context()+"文件发生了"+event.kind()+"事件"+"此事件发生的次数: "+event.count()); 
			}
			boolean valid = watchKey.reset();
			if(!valid)break;
		}
		
	}

}
