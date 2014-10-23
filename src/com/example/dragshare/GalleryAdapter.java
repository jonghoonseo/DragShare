package com.example.dragshare;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter {
	
	private Context mContext;
    ArrayList<Item> itemlist;	    
    Bitmap temp_img;
    
    public GalleryAdapter(Context c) {
        mContext = c;
        itemlist = new ArrayList<Item>();
        temp_img = BitmapFactory.decodeResource(mContext.getResources(),
	            R.drawable.ic_launcher);
    }

    public int getCount() {
        return itemlist.size();
    }
    

    public Item getItem(int position) {
        return itemlist.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }
    
    public void addItem(Item item)
    {
    	itemlist.add(item);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
        	LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.gridview_picture_item, null);
        } else {
            v = convertView;
        }
        
        ImageView iv = (ImageView) v.findViewById(R.id.iv_picture);
        
        if(itemlist.get(position).bitmap!=null)
        	iv.setImageBitmap(itemlist.get(position).bitmap);
        else
        {
        	iv.setImageBitmap(temp_img);
        }
        return v;
    }

}
