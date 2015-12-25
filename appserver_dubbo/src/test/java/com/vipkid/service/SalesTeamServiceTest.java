package com.vipkid.service;

import com.vipkid.BaseTest;
import com.vipkid.model.SalesTeam;
import com.vipkid.model.SalesTeam.Type;
import com.vipkid.repository.SalesTeamRepository;

import javax.annotation.Resource;

import org.junit.Ignore;
import org.junit.Test;

public class SalesTeamServiceTest extends BaseTest {
    @Resource
    SalesTeamRepository salesTeamRepository;
    /*
    @Test
    public void testFind() throws Exception {
    	SalesTeam salesTeam = new SalesTeam();
    	salesTeam.setType(Type.SALES);
    	salesTeam.setManagerId(8L);
    	salesTeamRepository.create(salesTeam);
    	
//    	SalesTeam salesTeam = salesTeamRepository.findByManagerId(2L);
//    	System.out.print(salesTeam.toString());
    }*/
    
    @Ignore
    @Test
    public void testListForSalesTeam() {
    	salesTeamRepository.listForSalesTeam(null, null, true,null, null, 0, 100);
    	salesTeamRepository.countForSalesTeam(null, null, true, null, null);
    	
    }
}