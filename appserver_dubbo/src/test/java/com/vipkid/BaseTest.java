package com.vipkid;

import com.alibaba.fastjson.JSON;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 类描述：
 * User: winni.li@qunar.com
 * Date: 2015-03-23
 * Time: 下午6:02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:spring/*.xml"})
@ActiveProfiles("dev")
public abstract class BaseTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    public void printf(Object o) {
        LOGGER.info(JSON.toJSONStringWithDateFormat(o, "yyyy-MM-dd HH:mm:ss"));
    }
}
