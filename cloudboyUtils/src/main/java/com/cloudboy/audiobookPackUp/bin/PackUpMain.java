package com.cloudboy.audiobookPackUp.bin;

import java.io.File;

import com.cloudboy.audiobookPackUp.service.PackUpService;
import com.cloudboy.audiobookPackUp.service.PackUpServiceMp3Impl;

public class PackUpMain {

	public static void main(String[] args) {
		PackUpService service = new PackUpServiceMp3Impl();
		File folder = new File("D:\\TDDownload\\myNovel");
		String[] removeStrings = {"\\{有声听书吧 www.ysts8.com\\}"};
		service.packUp(folder, removeStrings);
	}
}
