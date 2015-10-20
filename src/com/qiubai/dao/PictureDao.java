package com.qiubai.dao;

import java.util.List;

import com.qiubai.entity.Picture;

public interface PictureDao {
	
	public List<Picture> getPictures(String[] selectionArgs);
	
	public void addPictures(List<Picture> list);
	
	public void emptyPictureTable();
}
