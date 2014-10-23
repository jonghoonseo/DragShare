package com.utils;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;

public class ImageUtility {
	  
	  public synchronized static int GetExifOrientation(String filepath) 
		{
		    int degree = 0;
		    ExifInterface exif = null;
		    
		    try 
		    {
		        exif = new ExifInterface(filepath);
		    } 
		    catch (IOException e) 
		    {
		        e.printStackTrace();
		    }
		    
		    if (exif != null) 
		    {
		        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
		        
		        if (orientation != -1) 
		        {
		            // We only recognize a subset of orientation tag values.
		            switch(orientation) 
		            {
		                case ExifInterface.ORIENTATION_ROTATE_90:
		                    degree = 90;
		                    break;
		                    
		                case ExifInterface.ORIENTATION_ROTATE_180:
		                    degree = 180;
		                    break;
		                    
		                case ExifInterface.ORIENTATION_ROTATE_270:
		                    degree = 270;
		                    break;
		            }
		        }
		    }
		    
		    return degree;
		}
		
		
		public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) 
		{
		    if ( degrees != 0 && bitmap != null ) 
		    {
		        Matrix m = new Matrix();
		        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
		        try 
		        {
		            Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
		            if (bitmap != b2) 
		            {
		            	bitmap.recycle();
		            	bitmap = b2;
		            }
		        } 
		        catch (OutOfMemoryError ex) 
		        {
		            // We have no memory to rotate. Return the original bitmap.
		        }
		    }
		    
		    return bitmap;
		}
		
		
		public synchronized static Bitmap SafeDecodeBitmapFile(String strFilePath, Context context)
		{	
			try
			{	
				Bitmap bitmap = getThumbnail(context, strFilePath);		    	
		        int degree = GetExifOrientation(strFilePath);		        
		    	return GetRotatedBitmap(bitmap, degree);
			}
			catch(OutOfMemoryError ex)
			{
				ex.printStackTrace();
				
				return null;
			}
		}
		

		public static Bitmap getThumbnail(Context context, String path)
		{
			Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?",
					new String[] { path }, null);
			if (cursor != null && cursor.moveToFirst())
			{
				int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
				cursor.close();
				return MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), id, MediaStore.Images.Thumbnails.MINI_KIND, null);
			}		  
			cursor.close();
			return null;
		}		
}
