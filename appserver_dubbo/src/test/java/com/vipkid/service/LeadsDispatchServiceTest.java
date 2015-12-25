package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.vipkid.BaseTest;
import com.vipkid.model.Leads;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Order;
import com.vipkid.model.Order.Status;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.repository.FollowUpRepository;
import com.vipkid.repository.LeadsRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.OrderRepository;
import com.vipkid.repository.ParentRepository;
import com.vipkid.service.pojo.leads.LeadsVo;
import com.vipkid.service.pojo.leads.OrderVo;
import com.vipkid.util.DateTimeUtils;

public class LeadsDispatchServiceTest extends BaseTest {
	
	
	@Resource
	private LeadsRepository leadsRepository;
	@Resource
	private OnlineClassRepository onlineClassRepository;
	@Resource
	private FollowUpRepository followUpRepository;
	@Resource
	private OrderRepository orderRepository;
	@Resource
	LeadsManageService leadsManageService;
	@Resource
	ParentRepository parentRepository;
	
	@Ignore
	@Test
	public void testListStudent() {
		Date tmkAssignTimeFrom = null;
		//tmkAssignTimeFrom = DateUtils.setDays(new Date(), 28);
		Date tmkAssignTimeTo = null;
		//tmkAssignTimeTo = DateUtils.setDays(new Date(), 30);
		Date salesAssignTimeFrom = null;
		//salesAssignTimeFrom = DateUtils.setDays(new Date(), 28);
		Date salesAssignTimeTo = null;
		//salesAssignTimeTo = DateUtils.setDays(new Date(), 30);
		LifeCycle lifeCycle = null;
		//lifeCycle = LifeCycle.ASSIGNED;
		
		Date followupTimeFrom = null;
		//followupTimeFrom = DateUtils.setDays(new Date(), 15);
		//followupTimeFrom = DateUtils.setMonths(followupTimeFrom, 6);
		Date followupTimeTo = null;
		//followupTimeTo = DateUtils.setDays(new Date(), 18);
		//followupTimeTo = DateUtils.setMonths(followupTimeTo, 6);
		
		Long channelId = null;
		//channelId = 1760L;
		Integer customerStage = null;
		//customerStage = 1;
		Long salesId = null;
		//salesId = 38L;
		Long tmkId = null;
		//tmkId = 1769L;
		String searchText = null;
		//searchText = "Yang1";
		
		
		long start = System.currentTimeMillis();
		System.out.println("------------> start :");
		List<LeadsVo> leadsList = leadsRepository.listLeads(
				tmkAssignTimeFrom, tmkAssignTimeTo, salesAssignTimeFrom,
				salesAssignTimeTo, followupTimeFrom, followupTimeTo, lifeCycle,
				channelId, customerStage, 80271L, tmkId, searchText,null,2,null,null,null,null,null);
		
		System.out.println("------------> size :" + leadsList.size());
		long cost = System.currentTimeMillis() - start;
		System.out.println("------------> cost :" + cost);
	}
	
	@Ignore
	@Test
	public void testListOnlineClassForLeads() {
		Date scheduledTimeFrom = null;
		scheduledTimeFrom = DateUtils.setDays(new Date(), 1);
		scheduledTimeFrom = DateUtils.setMonths(scheduledTimeFrom, 4);
		Date scheduledTimeTo = null;
		scheduledTimeTo = DateUtils.setDays(new Date(), 15);
		scheduledTimeTo = DateUtils.setMonths(scheduledTimeTo, 5);
//		List<Long> courseIds = Lists.newArrayList(1L,9L,17L,25L,67L,73L,74L,75L,631L);
		List<Long> courseIds = Lists.newArrayList(9L,73L,74L,75L,631L);
		long start = System.currentTimeMillis();
//		onlineClassRepository.listOnlineClassForLeads(scheduledTimeFrom,
//				scheduledTimeTo, courseIds, null, null, 42L, null, null,
//				null, 0, 100);
		
//		onlineClassRepository.listOnlineClassForLeads(scheduledTimeFrom,
//				scheduledTimeTo, courseIds, null, null, null, 1769L, null,
//				"13800138001", 0, 100);
		
		onlineClassRepository.listOnlineClassForLeads(null,
				null, null, null, OnlineClass.FinishType.AS_SCHEDULED.name(), null, null, null,
				null, 0, 100);
		long cost = System.currentTimeMillis() - start;
		System.out.println("------------> cost :" + cost);
	}
	
	@Ignore
	@Test
	public void testCountForSales() {
	//	long count = leadsRepository.countLeadsForSales(Lists.newArrayList(42L), null, null, null, null);
		long count = leadsRepository.countLeadsForSales(Lists.newArrayList(42L,38L), DateTimeUtils.getBeginningOfTheDay(), null, null, null, false, null,null);
		System.out.println("count for sales 42 :" + count);
	}
	
	@Ignore
	@Test
	public void testCountForTmk() {
		Long count = leadsRepository.countLeadsForTmk(Lists.newArrayList(1772L), null, null, Lists.newArrayList(-1), null,false,null,null);
		
		System.out.println("count for tmk 1772 :" + count);
	}
	
	@Ignore
	@Test
	public void testCountForFollowup() {
//		long count = followUpRepository.countForLeads(Leads.OwnerType.STAFF_SALES, Lists.newArrayList(186171L), DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), null, null);
		followUpRepository.countForLeads(Leads.OwnerType.STAFF_SALES,  Lists.newArrayList(80271L), DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), null, null,false);
		System.out.println("count for followup staffid: 38L :");
	}
	
	@Ignore
	@Test
	public void testCountForOrder() {
		long count = orderRepository.countForLeads(Leads.OwnerType.STAFF_SALES, Lists.newArrayList(38L), null,null, Status.PAID);
		System.out.println("count for order staffid: 38L :" + count);
	}
	
	@Ignore
	@Test
	public void testListDashboardInfoForSales() {
		long start = System.currentTimeMillis();
		leadsManageService.listDashboardInfoForSales(Lists.newArrayList(42L));
		long cost = System.currentTimeMillis() - start;
		System.out.println("--------**************----> sales cost :" + cost);
	}
	
	@Ignore
	@Test
	public void testListDashboardInfoForTmk() {
//		long start = System.currentTimeMillis();
//		leadsDispatchService.listDashboardInfoForSales(1772L);
//		long cost = System.currentTimeMillis() - start;
//		System.out.println("--------**************----> tmk cost :" + cost);
		
		long leadsToday = leadsRepository.countLeadsForTmk(Lists.newArrayList(1772L), DateTimeUtils.getBeginningOfTheDay(), null, null, null,false,null,null);
		System.out.println("--------**************----> leadsToday  :" + leadsToday);
		
	}
	
	
	@Ignore
	@Test
	public void testListOrder() {
		Date createTimeFrom = null;
		createTimeFrom = DateUtils.setDays(new Date(), 1);
		createTimeFrom = DateUtils.setMonths(createTimeFrom, 5);
		Date createTimeTo = null;
		createTimeTo = DateUtils.setDays(new Date(), 9);
		createTimeTo = DateUtils.setMonths(createTimeTo, 5);
		
		Date paidTimeFrom = null;
		paidTimeFrom = DateUtils.setDays(new Date(), 1);
		paidTimeFrom = DateUtils.setMonths(paidTimeFrom, 5);
		Date paidTimeTo = null;
		paidTimeTo = DateUtils.setDays(new Date(), 15);
		paidTimeTo = DateUtils.setMonths(paidTimeTo, 5);
		List<OrderVo> list = leadsManageService.listOrder(createTimeFrom, createTimeTo, paidTimeFrom, paidTimeTo, Order.Status.PAY_CONFIRMED, Order.PayBy.ALIPAY, null, 1769L, "13800138000", 0, 100);
		System.out.println("---------***********-------> count:   " + list.size());
	}
	
	@Ignore
	@Test
	public void testCountLeadsForStaff() {
		Long tmkId = 1772L;
		Long salesId = 42L;
		
		long salesCount = leadsRepository.countLeadsForSales(salesId);
		long tmkCount = leadsRepository.countLeadsForTmk(tmkId);
		System.out.println("---------***********-------> salesCount:   " + salesCount + " ,tmkCount: " + tmkCount);
	}
	
	@Ignore
	@Test
	public void testCountLeads() {
//		Long tmkId = 1772L;
//		Long salesId = 42L;
		
		long leadsCount = leadsRepository.countLeads(null, null, null, null, null, null, null, null, null, null, null, null,null,null,null,null,null);
		System.out.println("---------***********-------> leadsCount:   " + leadsCount);
		long onlineClassCount = onlineClassRepository.countOnlineClassForLeads(null, null, null, null, null, null,null, null, null);
		System.out.println("---------***********-------> onlineClassCount:   " + onlineClassCount);
	}
	
	@Ignore
	@Test
	public void testUpdateLeadsStatus() {
		Long studentId = 2922L;
		Long userId = 305L;
		int status = 1;
		
		leadsManageService.updateLeadsStatus(studentId, userId, Leads.Status.PAYED.getCode());
		System.out.println("---------***********-------> testUpdateLeadsStatus:   " );
	}
	
	@Ignore
	@Test
	public void testUcountLeadsForSales() {
		long leadsWithoutTrialExpireTmw = leadsRepository.countLeadsForSales(Lists.newArrayList(1089029L), null, DateTimeUtils.getBeginningOfTheDay(DateTimeUtils.getDateByOffset(1-30, 0)), null, Lists.newArrayList(2,3),false,null,null);
		System.out.println("---------***********-------> testUpdateLeadsStatus:   " + leadsWithoutTrialExpireTmw );
	}
	
	@Ignore
	@Test
	public void testIsReachMaxLeadsReleaseSizeForSales () {
		Long salesId = 308470L;
		leadsManageService.isReachMaxLeadsReleaseSizeForSales(salesId);
	}
	
	

}
