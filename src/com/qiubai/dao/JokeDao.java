package com.qiubai.dao;

import java.util.List;

import com.qiubai.entity.Joke;

public interface JokeDao {

	public List<Joke> getJokes(String[] selectionArgs);
	
	public void addJokes(List<Joke> list);
	
	public void emptyJokeTable();
}
