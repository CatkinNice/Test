package com.qycolud.catkin;

import java.io.File;

import com.csvreader.CsvWriter;

public class Test2 {

	public static void main(String[] args) throws Exception {
		/*CsvWriter cw = new CsvWriter("");
		cw.toString();*/
		File file = new File("D:/indexDoc");
//		File[] files = ;
		for (File f : file.listFiles()) {
			System.out.print(f.getName());
		}
		Thread.sleep(10000);
		System.out.println();
		for (File f : file.listFiles()) {
			System.out.print(f.getName());
		}
	}

}
