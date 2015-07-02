package com.p.BeaconScanPhoto.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FilePicker {
	Activity activity;
	int resultTag=100;
	public int getResultTag() {
		return resultTag;
	}

	public void setResultTag(int resultTag) {
		this.resultTag = resultTag;
	}

	String filePath=null;
	IUploadListener listener;
	public interface IUploadListener{
		void onSuccess(String msg);
		void onFailed(String msg);
	}
    public FilePicker(Activity act)
    {
    	activity=act;
    }
    
    public void setIUploadListener(IUploadListener listener)
    {
    	this.listener=listener;
    }
    /**
	 * 上传图片到服务器
	 * 
	 *
	 *            包含路径
	 */
	private void uploadFileToStrusServer(File uploadFile,String id,String tempId) {
		try {
//			String requestUrl = "http://10.103.241.247:8888/vmap/android!execute";
			SharedPreferences sp = activity.getSharedPreferences("pwd", activity.MODE_PRIVATE);
			
			String requestUrl = sp.getString("http", "http://123.57.46.160:8080/beacon/android!uploadtest");
			// 文本信息
			Map<String, String> params=null;
		    params = new HashMap<String, String>();
//			params.put("username", "张三");
//			params.put("pwd", "zhangsan");
			params.put("fileName", uploadFile.getName()); //通过此方式传文件名
			params.put("id", id); //通过此方式传文件名
			params.put("tempid",tempId);
			// 上传文件
			FormFile formfile = new FormFile(uploadFile.getName(), uploadFile,
					"image", "application/octet-stream");//image不能改，服务器端有依赖
			boolean flag=SocketHttpRequester.post(requestUrl, params, formfile);
			String msg=SocketHttpRequester.getResultMsgStr();
			if(flag)
			{
				
				if(listener!=null)
					listener.onSuccess(msg);
			}
			else
			{
				if(listener!=null)
					listener.onFailed(msg);
			}
			       
		} catch (Exception e) {
			if(listener!=null)
				listener.onFailed("网络请求失败");
			e.printStackTrace();
		}
	}
	
	
	
    public void openFileChooser() {
		/***
		 * 这个是调用android内置的intent，来过滤文件 ，同时也可以过滤其他的
		 */
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		// intent.setType("image/*");//过滤图片文件
		//intent.setType("video/*;image/*");//同时选择视频和图片
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			activity.startActivityForResult(
					Intent.createChooser(intent, "请选择要上传的文件"),resultTag);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(activity, "请安装文件管理器",
					Toast.LENGTH_SHORT).show();
		}
	}

    public String getLoadFilePath(Intent data)
    {
    	Uri uri = data.getData();
    	filePath = getPath(activity, uri);
    	return filePath;
    }
    
    public boolean uploadFileToStrusServer(final String id,final String tempId)
    {
    	if(filePath==null)
			return false;
		final File file = new File(filePath);
		if (file != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					uploadFileToStrusServer(file,id,tempId);
				}
			}).start();
		}
    	return true;
    }
    
   
    
	private  String getPath(Context context, Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor;
			try {
				cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		}
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}

	public void setUploadFilePath(String path) {
		// TODO Auto-generated method stub
		filePath = path;
	}
}
