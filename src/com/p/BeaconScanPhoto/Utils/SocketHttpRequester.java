package com.p.BeaconScanPhoto.Utils;

import android.util.Log;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

/**
 * 上传文件到服务器
 * 
 * @author lz
 * 
 */
public class SocketHttpRequester {

	static String resultMsgStr="";
	public static String getResultMsgStr() {
		return resultMsgStr;
	}
	/**
	 * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能: <FORM METHOD=POST
	 * ACTION="http://192.168.1.101:8083/upload/servlet/UploadServlet"
	 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
	 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
	 * type="file" name="zip"/> </FORM>
	 *
	 * @param path
	 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://
	 *            www.iteye.cn或http://192.168.1.101:8083这样的路径测试)
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static boolean post(String path, Map<String, String> params,
			FormFile[] files) throws Exception {
		final String BOUNDARY = "---------------------------7da2137580612"; // 数据分隔线
		final String endline = "--" + BOUNDARY + "--\r\n";// 数据结束标志

		int fileDataLength = 0;
		StringBuilder fileExplain = null;
		for (FormFile uploadFile : files) {// 得到文件类型数据的总长度
			fileExplain = new StringBuilder();
			fileExplain.append("--");
			fileExplain.append(BOUNDARY);
			fileExplain.append("\r\n");
			fileExplain.append("Content-Disposition: form-data;name=\""
					+ uploadFile.getParameterName() + "\";filename=\""
					+ uploadFile.getFilname() + "\"\r\n");
			fileExplain.append("Content-Type: " + uploadFile.getContentType()
					+ "\r\n\r\n");
			fileExplain.append("\r\n");
			fileDataLength += fileExplain.length();
			if (uploadFile.getInStream() != null) {
				fileDataLength += uploadFile.getFile().length();
			} else {
				fileDataLength += uploadFile.getData().length;
			}
		}
		Log.i("SocketHttpRequester", "文件头：" + fileExplain.toString());
		System.out.println("文件头：" + fileExplain.toString());
		Log.i("SocketHttpRequester", " 文件总长度：" + fileDataLength);
		System.out.println(" 文件总长度：" + fileDataLength);
		// 传输给服务器的实体数据总长度
		int dataLength = 0;
		StringBuilder textEntity = null;
		if (params != null) {
			textEntity = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {// 构造文本类型参数的实体数据
				textEntity.append("--");
				textEntity.append(BOUNDARY);
				textEntity.append("\r\n");
				textEntity.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"\r\n\r\n");
				textEntity.append(entry.getValue());
				textEntity.append("\r\n");
			}

			Log.i("SocketHttpRequester", "文件文本内容：" + textEntity.toString());
			System.out.println("文件文本内容：" + textEntity.toString());
		}
		dataLength += fileDataLength + endline.getBytes().length;

		if (textEntity != null) {
			dataLength += textEntity.toString().getBytes().length;
		}

		Log.i("SocketHttpRequester", "传输给服务器的实体数据总长度：" + dataLength);
		System.out.println("传输给服务器的实体数据总长度：" + dataLength);

		URL url = new URL(path);
		int port = url.getPort() == -1 ? 80 : url.getPort();
		Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
		OutputStream outStream = socket.getOutputStream();
		// 下面完成HTTP请求头的发送
		String requestmethod = "POST " + url.getPath() + " HTTP/1.1\r\n";
		outStream.write(requestmethod.getBytes());
		String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
		outStream.write(accept.getBytes());
		String language = "Accept-Language: zh-CN\r\n";
		outStream.write(language.getBytes());
		String contenttype = "Content-Type: multipart/form-data; boundary="
				+ BOUNDARY + "\r\n";
		outStream.write(contenttype.getBytes());
		String contentlength = "Content-Length: " + dataLength + "\r\n";
		outStream.write(contentlength.getBytes());
		String alive = "Connection: Keep-Alive\r\n";
		outStream.write(alive.getBytes());
		String host = "Host: " + url.getHost() + ":" + port + "\r\n";
		outStream.write(host.getBytes());
		// 写完HTTP请求头后根据HTTP协议再写一个回车换行
		outStream.write("\r\n".getBytes());
		// 把所有文本类型的实体数据发送出来
		if (textEntity != null)
			outStream.write(textEntity.toString().getBytes());
		// 把所有文件类型的实体数据发送出来
		for (FormFile uploadFile : files) {
			StringBuilder fileEntity = new StringBuilder();
			fileEntity.append("--");
			fileEntity.append(BOUNDARY);
			fileEntity.append("\r\n");
			fileEntity.append("Content-Disposition: form-data;name=\""
					+ uploadFile.getParameterName() + "\";filename=\""
					+ uploadFile.getFilname() + "\"\r\n");
			fileEntity.append("Content-Type: " + uploadFile.getContentType()
					+ "\r\n\r\n");
			outStream.write(fileEntity.toString().getBytes());
			if (uploadFile.getInStream() != null) {
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = uploadFile.getInStream().read(buffer, 0, 1024)) != -1) {
					outStream.write(buffer, 0, len);
				}
				uploadFile.getInStream().close();
			} else {
				outStream.write(uploadFile.getData(), 0,
						uploadFile.getData().length);
			}
			outStream.write("\r\n".getBytes());
		}
		// 下面发送数据结束标志，表示数据已经结束
		outStream.write(endline.getBytes());


		DataInputStream	in = new DataInputStream(socket.getInputStream());
		
		
		String messageString = null;
		byte[] reply = new byte[1024];
		int len = -1;
		try {
			len = in.read(reply);//读入的字节数
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		if (len <= 0)
		return false;
	try {
		messageString = new String(reply, "utf-8").trim();
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 int x=messageString.indexOf("{");

	String contentJsonstr=messageString.substring(x);
	Log.i("ddn", contentJsonstr);
	outStream.flush();
	outStream.close();
	in.close();
	socket.close();
   JSONObject jobj=new JSONObject(contentJsonstr);
   resultMsgStr=(String) jobj.get("message");
   boolean flag=jobj.getBoolean("success");
   return flag;
	
//	BufferedReader reader = new BufferedReader(new InputStreamReader(
//	socket.getInputStream()));
//	String resultStr= reader.readLine();
//		if (resultStr.indexOf("200") == -1) {// 读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
//			Log.i("SocketHttpRequester", "上传失败");
//			System.out.println("上传失败");
//			return false;
//		}

//		outStream.flush();
//		outStream.close();
//		reader.close();
//		socket.close();
//		Log.i("SocketHttpRequester", "上传成功");
//		System.out.println("上传成功");
//		return true;
	}

	/**
	 * 提交数据到服务器
	 * 
	 * @param path
	 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://
	 *            www.itcast.cn或http://192.168.1.10:8080这样的路径测试)
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static boolean post(String path, Map<String, String> params,
			FormFile file) throws Exception {
		return post(path, params, new FormFile[] { file });
	}
}