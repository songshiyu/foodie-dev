package com.lxk.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author songshiyu
 * @date 2020/6/17 22:47
 **/
public class MD5Utils {

    /**
     * @des 对字符串进行md5加密
     * @param strValue
     * @return
     * */
    public static String getMD5Str(String strValue) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String md5Str = Base64.encodeBase64String(md5.digest(strValue.getBytes()));
        return md5Str;
    }

    public static void main(String[] args) {
        String str = "lxk";
        try {
            String md5Str = getMD5Str(str);
            System.out.println(md5Str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
