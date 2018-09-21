package com.kellte.demo.util;

import org.junit.Test;

import java.io.File;
import java.util.regex.Pattern;

public class FtpUtilTest {
    private String ftpHost=PropertiesUtil.getValue("ftp.host","config.properties");
    private Integer ftpPort=Integer.parseInt(PropertiesUtil.getValue("ftp.port","config.properties"));
    private String ftpUser=PropertiesUtil.getValue("ftp.user","config.properties");
    private String ftpPwd=PropertiesUtil.getValue("ftp.pwd","config.properties");
    private FtpUtil ftpUtil=new FtpUtil(ftpHost,ftpPort,ftpUser,ftpPwd);
    @Test
    public void testLogin(){

        ftpUtil.loginout();
    }
    @Test
    public void regexTest(){

       System.out.println(Pattern.matches("^(([0-8]\\d(.\\d{1,6})?)||90)$","80.01"));
       // System.out.println(Pattern.matches("^([^.]+.[^.]+)+","/1a/201-6-5我/cc"));
    }
    @Test
    public void createDir(){
        ftpUtil.createDirectorys("/aa/bb");
    }
    @Test
    public void uploadTest(){
        File file=new File("C:\\Users\\Administrator\\Desktop\\File");
        System.out.println(ftpUtil.uploadFtpFile(file,"/",file.getName()));
    }
    @Test
    public void downloadTest(){
        ftpUtil.downLoadFtpDirFile("/ftpTest/test.csv","C:\\Users\\Administrator\\Desktop");
    }
}
