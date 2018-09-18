package com.kellte.demo.util;

import org.apache.poi.ss.formula.functions.T;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import java.util.Map;

public class KellteUtil {
    /**
     * 调用trans文件
     * @param transFileName
     */
    public static void callNativeTrans(String transFileName) throws Exception {
        callNativeTransWithParams(null,transFileName);
    }

    public static void callNativeTransWithParams(String[] params,String transFileName) throws Exception {
        //初始化
        KettleEnvironment.init();
        EnvUtil.environmentInit();
        TransMeta transMeta=new TransMeta(transFileName);
        //转换
        Trans trans=new Trans(transMeta);
        //执行
        trans.execute(params);
        //等待结束
        trans.waitUntilFinished();
        //抛出异常
        if(trans.getErrors()>0){
            throw new Exception("here are errors during transformation exception!(传输过程中发生异常)");

        }
    }
    /**
     * 调用trans文件，传递参数
     * @param transFileName
     */
    public static void trancallNativeTransWithMap(Map<String,String> map,String transFileName) throws KettleException {
        //初始化
        KettleEnvironment.init();
        EnvUtil.environmentInit();
        //创建转换元数据
        TransMeta transMeta=new TransMeta(transFileName);
        //创建转换
        Trans trans=new Trans(transMeta);
        String[] x=new String[map.size()];
        int i=0;
        //设置参数
        for(Map.Entry<String,String> entry:map.entrySet()){
            trans.setVariable(entry.getKey(),entry.getValue());
            trans.setParameterValue(entry.getKey(),entry.getValue());
            x[i++]=entry.getValue();
        }
        //执行
        trans.execute(x);
        //等待结束
        trans.waitUntilFinished();
        //抛出异常
        if(trans.getErrors()>0){
            new Exception("here are errors during transformation exception!(传输过程中发生异常)");
        }
    }
    /**
     * 调用job文件
     * @param jobName
     * @throws Exception
     */
    public static void callNativeJob(String jobName) throws Exception{
        // 初始化
        KettleEnvironment.init();

        JobMeta jobMeta = new JobMeta(jobName,null);
        Job job = new Job(null, jobMeta);
        //向Job 脚本传递参数，脚本中获取参数值：${参数名}
        //job.setVariable(paraname, paravalue);
        job.start();
        job.waitUntilFinished();
        if (job.getErrors() > 0) {
            throw new Exception("There are errors during job exception!(执行job发生异常)");
        }
    }

    /**
     * 调用job文件
     * @param jobName
     * @throws Exception
     */
    public static void callNativeJob(String jobName,Map<String,String> variables) throws Exception{
        // 初始化
        KettleEnvironment.init();

        JobMeta jobMeta = new JobMeta(jobName,null);
        Job job = new Job(null, jobMeta);
        //向Job 脚本传递参数，脚本中获取参数值：${参数名}
        for(Map.Entry<String,String> entry:variables.entrySet()){
            job.setVariable(entry.getKey(),entry.getValue());
            //job.setParameterValue(entry.getKey(),entry.getValue());
        }

        job.execute(1,new Result());
        job.waitUntilFinished();
        if (job.getErrors() > 0) {
            throw new Exception("There are errors during job exception!(执行job发生异常)");
        }
    }
}
