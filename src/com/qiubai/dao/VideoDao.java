package com.qiubai.dao;

import java.util.List;

import com.qiubai.entity.Video;

public interface VideoDao {

	public List<Video> getVideos(String[] selectionArgs);

	public void addVideos(List<Video> list);

	public void emptyVideoTable();

}
