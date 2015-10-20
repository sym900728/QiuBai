package com.qiubai.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

public class BitmapUtil {

	/**
	 * 调整图片的大小 (先将图片缩小或者放大，然后对图片进行裁剪，让不同的图片都能占满不同的手机尺寸的屏幕)
	 * @param boxWidth
	 * @param boxHeight
	 * @param bitmap 原图  
	 * @return
	 */
	public static Bitmap resizeBitmap(int boxWidth, int boxHeight, Bitmap bitmap) {

		float scaleX = ((float) boxWidth) / ((float) bitmap.getWidth());
		float scaleY = ((float) boxHeight) / ((float) bitmap.getHeight());
		float scale = 1.0f;

		if ((scaleX >= scaleY && scaleY >= 1.0f) || (scaleX > scaleY && scaleX < 1.0f) || (scaleX >= 1.0f && scaleY < 1.0f)) {
			scale = scaleX;
		}
		if ((scaleY > scaleX && scaleX >= 1.0f) || (scaleY > scaleX && scaleY < 1.0f) || (scaleX < 1.0f && scaleY >= 1.0f)) {
			scale = scaleY;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		Bitmap alterBitmap = Bitmap.createBitmap(newBitmap, 0, 0, boxWidth, boxHeight);
		newBitmap = null;
		return alterBitmap;
	}

	/**
	 * 调整图片的大小适合box
	 * @param boxWidth
	 * @param boxHeight
	 * @param bitmap 原图
	 * @return
	 */
	public static Bitmap resizeBitmapMatchBox(int boxWidth, int boxHeight, Bitmap bitmap) {
		float scaleX = ((float) boxWidth) / ((float) bitmap.getWidth());
		float scaleY = ((float) boxHeight) / ((float) bitmap.getHeight());
		float scale = 1.0f;

		if ((scaleX >= scaleY && scaleY >= 1.0f) || (scaleX > scaleY && scaleX < 1.0f) || (scaleX >= 1.0f && scaleY < 1.0f)) {
			scale = scaleY;
		}
		if ((scaleY > scaleX && scaleX >= 1.0f) || (scaleY > scaleX && scaleY < 1.0f) || (scaleX < 1.0f && scaleY >= 1.0f)) {
			scale = scaleX;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}

	/**
	 * 调整正方形图片适应正方形盒子大小(图片为正方形图片)
	 * @param width
	 * @param bitmap
	 * @return
	 */
	public static Bitmap resizeSquareBitmap(int width, Bitmap bitmap) {
		float scale = ((float) width) / ((float) bitmap.getWidth());
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}

	/**
	 * 调整图片使图片充满整个盒子
	 * @param width
	 * @param height
	 * @param bitmap
	 * @return
	 */
	public static Bitmap resizeBitmapFillBox(int width, int height, Bitmap bitmap){
		float scaleX = ((float) width) / ((float) bitmap.getWidth());
		float scaleY = ((float) height) / ((float) bitmap.getHeight());
		Matrix matrix = new Matrix();
		matrix.postScale(scaleX, scaleY);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}
	
	/**
	 * 移动图片
	 * @param distanceX
	 * @param distanceY
	 * @param bitmap
	 * @return
	 */
	public static Bitmap translateBitmap(float distanceX, float distanceY,Bitmap bitmap){
		Matrix matrix = new Matrix();
		matrix.postTranslate(distanceX, distanceY);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}
	
	/**
	 * 旋转图片(旋转中心为图片的正中心)
	 * @param degree
	 * @param bitmap
	 * @return
	 */
	public static Bitmap rotateBitmap(float degree, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}
	
	/**
	 * 放大或者缩小图片
	 * @param scale
	 * @param bitmap
	 * @return
	 */
	public static Bitmap zoomBitmap(float scale, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}
	
	
	/**
	 * 将正方形图片变成圆形图片
	 * @param bitmap
	 * @return
	 */
	public static Bitmap circleBitmap(Bitmap bitmap){
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
        Paint paint = new Paint();  
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        
        paint.setAntiAlias(true);  
        paint.setFilterBitmap(true);  
        paint.setDither(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        canvas.drawCircle(bitmap.getWidth() / 2,  bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output; 
	}
}
