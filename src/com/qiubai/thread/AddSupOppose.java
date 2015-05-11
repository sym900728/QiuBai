package com.qiubai.thread;

import java.util.Map;

import com.qiubai.service.CharacterService;

public class AddSupOppose implements Runnable {

	private Map<String, String> addMap;

	private CharacterService characterService;

	public AddSupOppose(Map<String, String> addMap) {
		this.addMap = addMap;
		this.characterService = new CharacterService();
	}

	@Override
	public synchronized void run() {
		characterService.getaddSupportTread(addMap);
	}
}
