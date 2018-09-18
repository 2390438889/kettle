package com.kellte.demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    public static Properties properties;
    public static String filePath="";
    public static Properties getProperties(String filePath){
        if(properties==null){
            properties=new Properties();
        }
        try {
            properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(filePath));
            PropertiesUtil.filePath=filePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }



    public static String getValue(String key,String filePath){
        if(PropertiesUtil.filePath.equals(filePath)){
            return properties.getProperty(key);
        }
        return getProperties(filePath).getProperty(key);
    }




}
