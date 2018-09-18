package com.kellte.demo.dao.impl;

import com.kellte.demo.dao.TransationLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Repository
public class TransationLogDaoImpl implements TransationLogDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public Date lastTime() {
        String sql="select max(c_add_time) last_time from c_logdata_parsingrecode";
        List<Date> maps=jdbcTemplate.queryForList(sql,Date.class);
        return maps.get(0);
    }
}
