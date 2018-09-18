package com.kellte.demo.service.impl;

import com.kellte.demo.dao.TransationLogDao;
import com.kellte.demo.service.TransationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service
public class TransationLogServiceImpl implements TransationLogService {
    @Autowired
    private TransationLogDao transationLogDao;
    @Override
    public Date lastTime() {
        return transationLogDao.lastTime();
    }
}
