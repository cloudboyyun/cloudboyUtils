package com.cloudboy.audiobookPackUp.service;

import java.io.File;
import java.io.FilenameFilter;

public class Mp3Filter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if(name.endsWith(".mp3")) {
			return true;
		}
		return false;
	}
}
