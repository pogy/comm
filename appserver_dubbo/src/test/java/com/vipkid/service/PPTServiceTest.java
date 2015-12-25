package com.vipkid.service;

import com.vipkid.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;

public class PPTServiceTest extends BaseTest {

    @Resource
    PPTService pptService;

    @Test
    public void testFindByLessonIdAndType() throws Exception {
        pptService.findByLessonIdAndType(0, com.vipkid.model.Resource.Type.AUDIO);
    }
}