package com.kellte.demo.util;

import com.kellte.demo.service.TransationLogService;
import com.kellte.demo.util.KellteUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KellteRunTest {
    @Autowired
    private TransationLogService transationLogService;
    @Test
    public void test(){
        System.out.println("*****kettle定时任务运行开始******");
        String transFileName="C:\\Users\\Administrator\\Desktop\\File\\KettleLearn\\test2\\err_test.ktr";

        try {
           KellteUtil.callNativeTrans(transFileName);
           //KellteUtil.callNativeTransWithParams(new String[]{"a=12","b=13"},transFileName);
           /* Map map=new HashMap();
            map.put("a","1111111");
            map.put("b","21111111");
            KellteUtil.trancallNativeTransWithMap(map,transFileName);*/
           //KellteUtil.callNativeJob(transFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("*****kettle定时任务运行结束******");
    }

    @Test
    public void testTime() {

        //获取现在的时间
        Date nowTime=new Date();
        //格式化日期
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        //获取日志表最后的时间
        Date lastTime=new Date(new SimpleDateFormat("yyyy/MM/dd").format(transationLogService.lastTime()));
        //最后的日期加一天
        lastTime.setDate(lastTime.getDate()+1);
        //运行日志文件的参数
        Map<String,String> variables=new HashMap<>();
        String dirPath=simpleDateFormat.format(lastTime);
        //ftp
        FtpUtil ftpUtil=new FtpUtil();

        while(lastTime.compareTo(nowTime)<=0){
            dirPath=simpleDateFormat.format(lastTime);
            try {
                if(ftpUtil.existsDir("/convert/"+dirPath)){
                    variables.put("dir_path",dirPath);
                    //ftp创建文件夹
                    ftpUtil.createDirectorys("/kettle_test/"+dirPath);

                    KellteUtil.callNativeJob("C:\\Users\\Administrator\\Desktop\\File\\KettleLearn\\test3\\test_job.kjb",variables);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //最后的日期加一天
                lastTime.setDate(lastTime.getDate()+1);
            }

        }
    }

    @Test
    public void testJob(){

        //获取现在的时间
        Date nowTime=new Date();
        //格式化日期
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        //获取日志表最后的时间
        Date lastTime=new Date(new SimpleDateFormat("yyyy/MM/dd").format(transationLogService.lastTime()));
        //最后的日期加一天
        lastTime.setDate(lastTime.getDate()+1);
        //运行日志文件的参数
        Map<String,String> variables=new HashMap<>();
        String dirName;
        String dirPath=simpleDateFormat.format(lastTime);
        //ftp
        FtpUtil ftpUtil=new FtpUtil();
        String oldPath=PropertiesUtil.getValue("old_path","config.properties");
        String newPath=PropertiesUtil.getValue("new_path","config.properties");
        String transName="C:/Users/Administrator/Desktop/File/KettleLearn/test3/test_excel.ktr";
        while(lastTime.compareTo(nowTime)<=0){
            dirName=simpleDateFormat.format(lastTime);
            dirPath="/convert/"+dirName;
            try {
                if(ftpUtil.existsDir(dirPath)){
                    //从ftp上下载指定的日期目录
                    ftpUtil.downLoadFtpDirFile(dirPath,oldPath);
                    //遍历本地目录文件
                    for(File f:new File(oldPath,dirName).listFiles()){
                        variables.put("file_name",f.getName());
                        variables.put("old_file_name",f.getAbsolutePath());
                        variables.put("new_file_path",newPath+"\\"+dirName);
                        System.out.println(variables);
                        KellteUtil.trancallNativeTransWithMap(variables,transName);
                    }
                    //上传到ftp上
                  // ftpUtil.uploadFtpFile(new File(newPath+"\\"+dirName),"/kettle_test",dirName);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //最后的日期加一天
                lastTime.setDate(lastTime.getDate()+1);
            }

        }
    }

    @Test
    public void testTrans(){
        Map<String,String> variables=new HashMap<>();

        String transName="C:/Users/Administrator/Desktop/File/KettleLearn/test3/test_excel.ktr";
        variables.put("file_name","log0822-0829 - 副本 (2)");
        variables.put("old_file_name","C:\\Users\\Administrator\\Desktop\\File\\KettleLearn\\ftp_down\\2018-09-12\\log0822-0829 - 副本 (2).xlsx");
        variables.put("new_file_path","C:\\Users\\Administrator\\Desktop\\File\\KettleLearn\\conf\\2018-09-12");
        System.out.println(variables);
        try {
            KellteUtil.trancallNativeTransWithMap(variables,transName);
        } catch (KettleException e) {
            e.printStackTrace();
        }
    }

}
