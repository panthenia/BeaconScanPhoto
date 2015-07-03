package com.p.BeaconScanPhoto.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;
import com.p.BeaconScanPhoto.DataType.PublicData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FilePicker {

    int resultTag = 100;

    public int getResultTag() {
        return resultTag;
    }

    public void setResultTag(int resultTag) {
        this.resultTag = resultTag;
    }

    String filePath = null;
    IUploadListener listener;

    public interface IUploadListener {
        void onSuccess(String msg);

        void onFailed(String msg);
    }

    public FilePicker() {

    }

    public void setIUploadListener(IUploadListener listener) {
        this.listener = listener;
    }

    public void uploadFileToStrusServer(File uploadFile) {
        try {
			String requestUrl = "http://" + PublicData.getInstance().getIp() + ":" + PublicData.getInstance().getPort() + "/beacon/android!pic_upload";
            Map<String, String> params = null;
            params = new HashMap<String, String>();
//			params.put("username", "张三");
//			params.put("pwd", "zhangsan");
            params.put("fileName", uploadFile.getName()); //通过此方式传文件名
            // 上传文件
            FormFile formfile = new FormFile(uploadFile.getName(), uploadFile,
                    "image", "application/octet-stream");//image不能改，服务器端有依赖
            boolean flag = SocketHttpRequester.post(requestUrl, params, formfile);
            String msg = SocketHttpRequester.getResultMsgStr();
            if (flag) {

                if (listener != null)
                    listener.onSuccess(msg);
            } else {
                if (listener != null)
                    listener.onFailed(msg);
            }

        } catch (Exception e) {
            if (listener != null)
                listener.onFailed("网络请求失败");
            e.printStackTrace();
        }
    }


}
