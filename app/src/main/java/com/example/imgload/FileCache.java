package com.example.imgload;

import java.io.File;
import android.content.Context;

public class FileCache {

	private static File cacheDir;

	public FileCache(Context context) {
		/**
		 * 如果有SD卡则在SD卡中建一个LazyList的目录存放缓存的图片 没有SD卡就放在系统的缓存目录中
		 */
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"fragcached");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url) {
		// 将url的hashCode作为缓存的文件名
		String filename = String.valueOf(url.hashCode() + ".png");
		File f = new File(cacheDir, filename);
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
	public static long getFolderSize() throws Exception {
		long size = 0;
		try {
			File[] fileList = cacheDir.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// 如果下面还有文件
				if (fileList[i].isDirectory()) {
					size = size + getFolderSize();
				} else {
					size = size + fileList[i].length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}


}