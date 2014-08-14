package com.qycolud.catkin;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class Test1 {

	public static void main(String[] args) throws Exception {
		/*File file = new File("D:/a.txt");
		file.delete();*/
		String encode =URLEncoder.encode("adgdg#", "UTF-8");
		String decode =URLDecoder.decode( encode, "UTF-8");
		System.out.println(encode);
		System.out.println(decode);
		String str = "aaaaaaaa#aaaaaaaaaa";
		
		
		System.out.println(str.substring(str.indexOf("#")));
	}
    

}
