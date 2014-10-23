package com.example.dragshare;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.dragshare.networkmanager.FTPNetworkManager;
import com.utils.DragDropGridView;
import com.utils.DragDropGridView.OnDropListener;
import com.utils.ImageUtility;

public class MainActivity extends Activity {
	
	public class FTPTask extends AsyncTask<String, Long, Void> {
		FTPNetworkManager network;
		
		final String 	host = "165.132.107.90";
		final int		port = 21;
		final String 	id	 = "msl";
		final String	pw	 = "0";
		
		final String	targetPath = "/";
		
		long fileSize;
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			network = new FTPNetworkManager();
			
			// Progress Update
			//------------------------
//			network.setFTPTask(this);						// to process upload progress, transfer this instance to FTPNetworkManager 
		}

		@Override
		protected Void doInBackground(String... params) {
			
			network.initialize(host, port, id, pw);
			
			// 파일 크기를 계산
			fileSize = (new File(params[0])).length();
			
			if(!network.send(params[0], targetPath + getFileName(params[0]))){
				Log.e("NetworkManager", "Sending Failed");
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// Notify done
			Toast.makeText(getApplicationContext(), "FTP Upload Done", Toast.LENGTH_SHORT).show();
			
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			// progress bar 등을 설정하시면 됩니다.
//			long transferredBytes = values[0].longValue();
			
//			((TextView)findViewById(R.id.textView1)).setText("Progress: "+ String.format("%.2f", 100.0 * transferredBytes / fileSize) + "%" );
			super.onProgressUpdate(values);
		}
		
		public void callPublishProgress(long value) {
			publishProgress(Long.valueOf(value));
		}
	}
	
	
	
	
	GalleryAdapter adapter = null;
	Context		mycontext;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mycontext = this;
		adapter = new GalleryAdapter(this);
     	loadImageFromGallery();
     	DragDropGridView gridview = (DragDropGridView)findViewById(R.id.gridview);
		gridview.setAdapter((ListAdapter) adapter);
		gridview.setOnDropListener(onDropListener);
	}
	
	private OnDropListener onDropListener = new OnDropListener()
	{
		
		@Override
		public void drop(int from, int to, int x, int y)
		{
//			Toast.makeText(getBaseContext(), adapter.getItem(from).path + "["+x+ "," + y+"]", Toast.LENGTH_SHORT).show();

        	// Initialize FTP Task instance
        	FTPTask ftp = new FTPTask();
        	
        	// Get the file name of last picture 
        	final String selectedPicture = adapter.getItem(from).path;

        	// Notify
        	Toast.makeText(getApplicationContext(), selectedPicture, Toast.LENGTH_SHORT).show();
        	Log.d("FTP", "filename = " + selectedPicture + ", x = "+ x + ", y = "+y);
        	
        	// Go and upload
        	ftp.execute(selectedPicture);
		}
	};
	
	public void loadImageFromGallery()
	{
		File rootsd = Environment.getExternalStorageDirectory();
		File dcim = new File(rootsd.getAbsolutePath() + "/DCIM/Camera");
		
		File[] files = dcim.listFiles();
		for(int i=0;i<files.length;i++)
		{
			File file = files[i];
			if(file.isFile())
			{	
				String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")+1);
				if(extension.compareToIgnoreCase("jpg")==0
					|| extension.compareToIgnoreCase("png")==0
				)
				{
					
					Item item = new Item();
					item.path = file.getAbsolutePath(); 
					adapter.addItem(item);
				}
			}
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<adapter.getCount();i++)
				{
					adapter.getItem(i).bitmap = ImageUtility.SafeDecodeBitmapFile(adapter.getItem(i).path,mycontext);
					runOnUiThread(new Runnable(){
			             @Override
			             public void run() {
			            	 adapter.notifyDataSetChanged();
			             }
			        });
				}
			}
		}).start();

	}
	
	private String getFileName(String fullPath) {
		int S = fullPath.lastIndexOf("/");
		int M = fullPath.lastIndexOf(".");
		int E = fullPath.length();
		
		String filename = fullPath.substring(S+1, M);
		String extname = fullPath.substring(M+1, E);
		
		String extractFileName = filename + "." + extname;
		return extractFileName;
	}
}
