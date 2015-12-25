package com.vipkid.service;

import com.vipkid.BaseTest;

import javax.annotation.Resource;

import org.junit.Test;

public class BillNoServiceTest extends BaseTest {
	@Resource
	BillNoService billNoService;

	@Test
    public void testGetOrderNo() throws Exception {
//		int i = 0;
//		while(i++ < 10) {
//		}
		String orderNo = billNoService.doGetNextOrderNo();
		System.out.println(" orderNo -- > " + orderNo);

    }
}