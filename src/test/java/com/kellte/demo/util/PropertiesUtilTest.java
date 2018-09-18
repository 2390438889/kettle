package com.kellte.demo.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PropertiesUtilTest {
    @Test
    public void getValue(){
        System.out.println(PropertiesUtil.getValue("host","config.properties"));
    }
}
