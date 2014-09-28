package com.cloudboy.audiobookPackUp.service;

import java.io.File;

public interface PackUpService {
	/**
	 * The audio files are unziped and stored in a folder, to rename these files with their titles 
	 * so that the audio device can loop them in a correct order.
	 * @param folder the folder contains the audio files.
	 * @param removeStrings the words may exists in the file names or titles to be removed.
	 */
	public void packUp(File folder, String[] removeStrings);
}
