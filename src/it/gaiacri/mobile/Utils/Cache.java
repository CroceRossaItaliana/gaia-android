package it.gaiacri.mobile.Utils;

import java.io.File;

import com.jakewharton.disklrucache.DiskLruCache;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import uk.co.senab.bitmapcache.BitmapLruCache;

public class Cache {
	private BitmapLruCache mCache;
	
	public Cache(Activity context){
		File cacheLocation;
        // If we have external storage use it for the disk cache. Otherwise we use
        // the cache dir
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            cacheLocation = new File(
                    Environment.getExternalStorageDirectory() + "/GaiaCache");
        } else {
            cacheLocation = new File(context.getFilesDir() + "/GaiaCache");
        }
        cacheLocation.mkdirs();

        BitmapLruCache.Builder builder = new BitmapLruCache.Builder(context);
        builder.setMemoryCacheEnabled(true).setMemoryCacheMaxSizeUsingHeapSize();
        builder.setDiskCacheEnabled(true).setDiskCacheLocation(cacheLocation);
        
        mCache = builder.build();
	}
	
	public void put(String url,Bitmap bitmap){
		mCache.put(url, bitmap);
	}
	
	public boolean contains(String url){
		return mCache.contains(url);
	}
	
	public Bitmap get(String url){
		return mCache.get(url).getBitmap();
	}
}
