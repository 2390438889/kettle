package com.kellte.demo.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import sun.net.ftp.FtpClient;

import java.io.*;
import java.util.regex.Pattern;

public class FtpUtil {
    //FTP服务器ip
    private  String ftpHost=PropertiesUtil.getValue("ftp.host","config.properties");
    //FTP服务器端口
    private  Integer ftpPort=Integer.parseInt(PropertiesUtil.getValue("ftp.port","config.properties"));
    //FTP服务器用户名
    private  String ftpUser=PropertiesUtil.getValue("ftp.user","config.properties");;
    //FTP服务器密码
    private  String ftpPwd=PropertiesUtil.getValue("ftp.pwd","config.properties");;
    //本地字符编码
    private String localCharset="UTF-8";
    //ftp协议中，规定文件名编码为iso-8859-1
    private String serverCharset="8859_1";
    //UTF-8字符编码
    private static final String  CHARSET_UTF8="GBK";
    //OPTS UTF-8字符串常量
    private static final String OPTS_UTF8="OPTS UTF8";
    //设置缓冲区大小
    private int BUFFER_SIZE=1024*1024*4;
    //FtpClient对象
    private FTPClient ftpClient=new FTPClient();
    public FtpUtil(){
        login();
    }

    public FtpUtil(String ftpHost, Integer ftpPort, String ftpUser, String ftpPwd) {
        this.ftpHost = ftpHost;
        this.ftpPort = ftpPort;
        this.ftpUser = ftpUser;
        this.ftpPwd = ftpPwd;
        login();
    }

    //登陆ftp服务器
    public void login(){
        try {
            ftpClient.connect(ftpHost,ftpPort);
            if(ftpClient.isConnected()){
               if(!ftpClient.login(ftpUser,ftpPwd)){
                   System.out.println("ftp服务器登陆失败>>>〒_〒 ");
               }
            }else{
                System.out.println("ftp服务器连接失败>>>〒_〒 ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //注销ftp服务器
    public void loginout(){
        try {
            if(ftpClient.isConnected()){
                ftpClient.logout();
                ftpClient.disconnect();
            }else{
                System.out.println("没有连接到ftp服务器>>>〒_〒 ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //递归创建文件夹
    public boolean createDirectorys(String dirPath){
        if(!Pattern.matches("^/([\\u4e00-\\u9fa5\\d\\w\\-]+/?)*",dirPath)){
            System.out.println("路径名格式错误！0^0!");
            return false;
        }
        String[] dirs=dirPath.split("/");

        try {
            //跳转到根目录
            if(ftpClient.changeWorkingDirectory("/")){
                //递归创建所有没有的文件夹
                for(String dir:dirs){

                    if(!ftpClient.changeWorkingDirectory(dir)){
                        //如果目录不存在则创建目录
                        ftpClient.makeDirectory(new String(dir.getBytes(localCharset),serverCharset));
                        //跳转到目录
                        ftpClient.changeWorkingDirectory(dir);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    //判断文件夹是否存在

    public boolean existsDir(String path) throws IOException {
        return ftpClient.changeWorkingDirectory(path);
    }

    //上文件/文件夹到ftp服务器
    public boolean uploadFtpFile(File file,String ftpPath,String fileName){

        try {
            ftpClient.setBufferSize(BUFFER_SIZE);
            ftpClient.setControlEncoding(localCharset);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            if(file.exists()){
                //如果上传目录不存在在创建该目录
                if(!ftpClient.changeWorkingDirectory(ftpPath)){
                    if(!createDirectorys(ftpPath)){
                        return false;
                    }
                }
                if(!ftpPath.endsWith("/")){
                    ftpPath+="/";
                }
                BufferedInputStream bis;
                FileInputStream fis;
                if(file.isFile()){
                    fis=new FileInputStream(file);
                    bis=new BufferedInputStream(fis);
                    ftpClient.storeFile(new String((ftpPath+fileName).getBytes(localCharset),serverCharset),bis);
                    bis.close();
                    fis.close();
                    System.out.println(file.getName()+"\t>>>>>>>>>>>>>>>>>>上传成功     ^_^!");
                }else{
                    if(createDirectorys(ftpPath+fileName)){
                        for(File f:file.listFiles()){
                            uploadFtpFile(f,ftpPath+fileName,f.getName());
                        }
                    }
                }
                return true;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //下载文件夹下所有文件
    public boolean downLoadFtpFile(String ftpPath,String savePath){
        //判断ftp上该文件夹是否存在
        try {
            //开启被动模式
            ftpClient.enterLocalPassiveMode();
            FileOutputStream fileOutputStream;
            BufferedOutputStream bufferedOutputStream;
            if(ftpClient.changeWorkingDirectory(ftpPath)){
                //获得该ftp文件夹下所有的文件名
                //遍历每个文件，将文件下载到本地
                String[] fileNames=ftpClient.listNames();
                for(String fileName:fileNames){
                    if(fileName.indexOf(".")>0){
                        fileOutputStream=new FileOutputStream(new File(savePath+"\\"+new String(fileName.getBytes(serverCharset),localCharset)));
                        bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
                        ftpClient.retrieveFile(ftpPath+"/"+fileName,bufferedOutputStream);
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                        fileOutputStream.close();
                    }else{
                        File dir=new File(savePath+"\\"+new String(fileName.getBytes(serverCharset),localCharset));
                        dir.mkdir();
                        downLoadFtpFile(ftpPath+"/"+fileName,savePath+"\\"+dir.getName());
                    }
                    System.out.println(new String(fileName.getBytes(serverCharset),localCharset)+">>>>>>>>>>>>>>>下载成功！");
                }
                System.out.println(savePath+":下载完毕>>>>>>>>>>>>>>>");
                return true;

            }else{
                System.out.println(ftpPath+"不存在>>>>>>>>>>>>>>");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return false;
        }





    }

    //下载文件/文件夹
    public boolean downLoadFtpDirFile(String ftpPath,String savePath){
        //判断ftp上该文件夹是否存在
        try {
            //开启被动模式
            ftpClient.enterLocalPassiveMode();
            FileOutputStream fileOutputStream;
            BufferedOutputStream bufferedOutputStream;
            String path=ftpPath.substring(0,ftpPath.lastIndexOf("/"));
            String name=ftpPath.substring(ftpPath.lastIndexOf("/")+1);
            //如果是文件
            if(ftpPath.indexOf(".")>0){
                if(ftpClient.changeWorkingDirectory(path)){
                    fileOutputStream=new FileOutputStream(new File(savePath+"\\"+new String(name.getBytes(serverCharset),localCharset)));
                    bufferedOutputStream=new BufferedOutputStream(fileOutputStream);
                    ftpClient.retrieveFile(ftpPath,bufferedOutputStream);
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                    fileOutputStream.close();
                    System.out.println(new String(name.getBytes(serverCharset),localCharset)+">>>>>>>>>>>>>>>下载成功！");
                }
            }else{
                if(ftpClient.changeWorkingDirectory(ftpPath)){
                    //获得该ftp文件夹下所有的文件名
                    //遍历每个文件，将文件下载到本地
                    File dir=new File(savePath+"\\"+new String(name.getBytes(serverCharset),localCharset));
                    dir.mkdir();
                    for(String fileName:ftpClient.listNames()){
                        downLoadFtpFile(ftpPath+"/"+fileName,savePath+"\\"+name);

                    }
                    System.out.println(savePath+":下载完毕>>>>>>>>>>>>>>>");
                    return true;

                }else{
                    System.out.println(ftpPath+"不存在>>>>>>>>>>>>>>");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return false;
        }





    }




}
