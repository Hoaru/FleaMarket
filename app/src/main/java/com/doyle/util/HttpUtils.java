package com.doyle.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by 30579 on 2018/1/27.
 */

public class HttpUtils {
    /*
 * Function  :   发送Post请求到服务器
 * Param     :   params请求体内容，encode编码格式
 */
    //方法类
    public  String AsubmitPostData(String strUrlPath, Map<String, String> params, String encode) {

        byte[] data = getRequestData(params, encode).toString().getBytes();//获得请求体
        try {
            //String urlPath = "http://192.168.1.9:80/JJKSms/RecSms.php";
            URL url = new URL(strUrlPath);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            System.out.println("检查点1："+response);
            if(response == HttpURLConnection.HTTP_OK) {
                System.out.println("检查点2"+response);
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     //处理服务器的响应结果
            }
        } catch (IOException e) {
            System.out.println("错误："+e.getMessage().toString());
            return "-1";
        }
        return "-1";
    }

    //静态类
    public static String submitPostData(String strUrlPath, Map<String, String> params, String encode) {

        byte[] data = getRequestData(params, encode).toString().getBytes();//获得请求体
        try {

            URL url = new URL(strUrlPath);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(10000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);
            outputStream.close();

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            System.out.println("检查点1："+response);
            if(response == HttpURLConnection.HTTP_OK) {
                System.out.println("检查点2"+response);
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     //处理服务器的响应结果
            }
        } catch (IOException e) {
            System.out.println("错误："+e.getMessage().toString());
            return "-1";
        }
        return "-1";
    }
    /**
     * 下载图片
     */

    public static String getInputStream(String path)
    {
        System.out.println(path);
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try
        {
            URL url = new URL(path);
            if (null != url)
            {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                // 设置连接网络的超时时间
                httpURLConnection.setConnectTimeout(5000);
                // 打开输入流
                httpURLConnection.setDoInput(true);
                // 设置本次Http请求使用的方法
                httpURLConnection.setRequestMethod("GET");
                int requestCode = httpURLConnection.getResponseCode();
                System.out.println("这里返回值"+requestCode);
                if (requestCode==200)
                {
                    // 从服务器获得一个输入流
                    inputStream = httpURLConnection.getInputStream();

                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    return ImageDeal.Bitmap2String(bitmap);
                }

            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return "";
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }
        return "";
    }


    //list 同步下载图片
    public static synchronized Bitmap getBitmap(String path)
    {
        System.out.println(path);
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try
        {
            URL url = new URL(path);
            if (null != url)
            {
                httpURLConnection = (HttpURLConnection) url.openConnection();

                // 设置连接网络的超时时间
                httpURLConnection.setConnectTimeout(5000);

                // 打开输入流
                httpURLConnection.setDoInput(true);

                // 设置本次Http请求使用的方法
                httpURLConnection.setRequestMethod("GET");
                int requestCode = httpURLConnection.getResponseCode();
                System.out.println("这里返回值"+requestCode);
                if (requestCode==200)
                {
                    // 从服务器获得一个输入流
                    inputStream = httpURLConnection.getInputStream();
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }

            }
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

}
