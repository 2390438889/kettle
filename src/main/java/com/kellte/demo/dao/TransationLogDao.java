package com.kellte.demo.dao;

import java.util.Date;

public interface TransationLogDao {

    //获得最后一天日志时间
    public Date lastTime();
}
