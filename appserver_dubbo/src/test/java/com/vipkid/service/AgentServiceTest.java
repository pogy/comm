package com.vipkid.service;

import com.vipkid.BaseTest;

import javax.annotation.Resource;

public class AgentServiceTest extends BaseTest {
    @Resource
    AgentService agentService;

    public void testFind() throws Exception {

        agentService.find(1022);

    }
}