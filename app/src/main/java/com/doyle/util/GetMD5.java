package com.doyle.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 30579 on 2018/1/29.
 */

public class GetMD5 {
    public static String GetMD5(String str){
        try {
            MessageDigest mdi = MessageDigest.getInstance("MD5");
            byte[] input = str.getBytes("UTF-8");
            byte[] hash = mdi.digest(input);
            String d = "";
            for (int i=0;i<hash.length;i++){
                int v = hash[i] & 0xFF;//作用应该是将字符串前面清零 因为转换为int 时 高位会补位 负数就会产生不一样的效果.
                if (v<16)  d+="0";
                System.out.println(" v :"+v);
                d+=Integer.toString(v,16).toLowerCase();
            }
            System.out.println(d);
            return d;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       return null;
    }
}
