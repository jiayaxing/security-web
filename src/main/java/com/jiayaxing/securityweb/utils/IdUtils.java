package com.jiayaxing.securityweb.utils;


import java.util.UUID;

/**
 * ID生成器工具类
 * 
 * @author ruoyi
 */
public class IdUtils
{

    public static void main(String[] args) {
        String s = UUID.randomUUID().toString().replaceAll("-","");
        System.out.println(s);
    }
}
