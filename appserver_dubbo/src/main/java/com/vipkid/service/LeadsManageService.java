package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.vipkid.model.Course;
import com.vipkid.model.Leads;
import com.vipkid.model.Leads.OwnerType;
import com.vipkid.model.Leads.Status;
import com.vipkid.model.LeadsDispatchHistory;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Order;
import com.vipkid.model.Order.PayBy;
import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.User;
import com.vipkid.repository.CourseRepository;
import com.vipkid.repository.FollowUpRepository;
import com.vipkid.repository.LeadsDispatchHistoryRepository;
import com.vipkid.repository.LeadsRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.OrderRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.pojo.CltStudent;
import com.vipkid.service.pojo.LeadsAgeRange;
import com.vipkid.service.pojo.leads.DashboardInfo;
import com.vipkid.service.pojo.leads.LeadsVo;
import com.vipkid.service.pojo.leads.OnlineClassVo;
import com.vipkid.service.pojo.leads.OrderVo;
import com.vipkid.service.pojo.leads.ResponseVo;
import com.vipkid.service.pojo.leads.ResponseVo.RespStatus;
import com.vipkid.util.DateTimeUtils;


@Service
public class LeadsManageService {
	
	@Value("#{configProperties['leads.days_trial_book_for_sales']}")
	private int DAYS_TRIAL_BOOK_FOR_SALES;
	@Value("#{configProperties['leads.days_pay_for_sales']}")
	private int DAYS_PAY_FOR_SALES;
	@Value("#{configProperties['leads.days_trial_book_for_tmk']}")
	private int DAYS_TRIAL_BOOK_FOR_TMK;
	@Value("#{configProperties['leads.max_leads_for_sales']}")
	private int MAX_LEADS_FOR_SALES;
	@Value("#{configProperties['leads.max_leads_for_tmk']}")
	private int MAX_LEADS_FOR_TMK;
	@Value("#{configProperties['leads.max_leads_take_from_library_everyday_for_sales']}")
	private int MAX_LEADS_TAKE_FROM_LIBRARY_EVERYDAY_FOR_SALES;
	@Value("#{configProperties['leads.max_leads_take_from_library_everyday_for_tmk']}")
	private int MAX_LEADS_TAKE_FROM_LIBRARY_EVERYDAY_FOR_TMK;
	@Value("#{configProperties['leads.max_leads_release_everyday_for_sales']}")
	private int MAX_LEADS_RELEASE_EVERYDAY_FOR_SALES;
	@Value("#{configProperties['leads.max_leads_lock_for_sales']}")
	private int MAX_LEADS_LOCK_FOR_SALES;
	
	@Resource
	private LeadsRepository leadsRepository;
	@Resource
	private FollowUpRepository followUpRepository;
	@Resource
	private OnlineClassRepository onlineClassRepository;
    @Resource
    private OrderRepository orderRepository;
    @Resource
    private LeadsDispatchHistoryRepository leadsDispatchHistoryRepository;
    @Resource
    private StudentService studentService;
    @Resource
    private StudentLifeCycleLogService studentLifeCycleLogService;
	@Resource
	SecurityService securityService;
	@Resource
	CourseRepository courseRepository;
	
	public List<LeadsVo> listLeads(Date tmkAssignTimeFrom, Date tmkAssignTimeTo,Date salesAssignTimeFrom, Date salesAssignTimeTo,
			Date followUpTimeFrom, Date followUpTimeTo, LifeCycle lifeCycle, Long channelId,
			Integer customerStage, Long salesId, Long tmkId, String searchText,Integer status,Integer contact,Boolean locked,String channelLevel, LeadsAgeRange ageRange, Integer start, Integer length) {
		List<LeadsVo> list = leadsRepository.listLeads(tmkAssignTimeFrom, tmkAssignTimeTo, salesAssignTimeFrom, salesAssignTimeTo, followUpTimeFrom, followUpTimeTo, lifeCycle, channelId, customerStage, salesId, tmkId, searchText, status, contact, locked, channelLevel, ageRange, start, length);
		return list;
	}
	
	public long countLeads(Date tmkAssignTimeFrom, Date tmkAssignTimeTo,Date salesAssignTimeFrom, Date salesAssignTimeTo,
			Date followUpTimeFrom, Date followUpTimeTo, LifeCycle lifeCycle, Long channelId,
			Integer customerStage, Long salesId, Long tmkId, String searchText, Integer status, Integer contact, Boolean locked, String channelLevel, LeadsAgeRange ageRange) {
		
		return leadsRepository.countLeads(tmkAssignTimeFrom, tmkAssignTimeTo, salesAssignTimeFrom, salesAssignTimeTo,
				 followUpTimeFrom, followUpTimeTo, lifeCycle, channelId, customerStage, salesId, tmkId, searchText, status, contact, locked, channelLevel, ageRange);
	}
	
	public DashboardInfo listDashboardInfoForSales(List<Long> staffIds) {
		
		long leadsToday = leadsRepository.countLeadsForSales(staffIds, DateTimeUtils.getBeginningOfTheDay(), null, null, null, true, null, null);
		long leadsTodayNotFollow = leadsRepository.countLeadsForSales(staffIds, DateTimeUtils.getBeginningOfTheDay(), null, null, null, true, false, null);
		long leadsPreviousNotContact = leadsRepository.countLeadsForSales(staffIds, null, DateTimeUtils.getYesterday(0), null, null, true, false, null);
		//long leadsPreviousCallFailed = leadsRepository.countLeadsForSales(staffIds, null,  DateTimeUtils.getYesterday(0), Lists.newArrayList(Status.ASSIGNED.getCode()), null);
		long leadsWithTrialExpireTmw = leadsRepository.countLeadsForSales(staffIds, null, DateTimeUtils.getBeginningOfTheDay(DateTimeUtils.getDateByOffset(1-DAYS_PAY_FOR_SALES, 0)), Lists.newArrayList(Status.BOOKEDTRIAL.getCode()), null, true, null,false);
		long leadsWithoutTrialExpireTmw = leadsRepository.countLeadsForSales(staffIds, null, DateTimeUtils.getBeginningOfTheDay(DateTimeUtils.getDateByOffset(1-DAYS_TRIAL_BOOK_FOR_SALES, 0)), null, Lists.newArrayList(Status.BOOKEDTRIAL.getCode(),Status.PAYED.getCode()), true, null, false);
		long leadsTotal = leadsRepository.countLeadsForSales(staffIds, null, null, null, null, true, null, null);
		long needFollowupToday = followUpRepository.countForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), null,null,true);
		long followupedToday = followUpRepository.countForLeads(Leads.OwnerType.STAFF_SALES, staffIds, null, null, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0),null);
		long trialTotalToday = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), null, null, null, null);
		long trialFinishAsScheduledToday = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), Lists.newArrayList(OnlineClass.Status.FINISHED), null, Lists.newArrayList(OnlineClass.FinishType.AS_SCHEDULED), null);
		long trialFinishWithProblemToday = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), Lists.newArrayList(OnlineClass.Status.FINISHED), null, null, Lists.newArrayList(OnlineClass.FinishType.AS_SCHEDULED));
		long trialCanceledToday = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), Lists.newArrayList(OnlineClass.Status.CANCELED),null,null,null);
		long trialBookedToday = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), Lists.newArrayList(OnlineClass.Status.BOOKED),null,null,null);
		long trialTotalYest = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), null, null, null, null);
		long trialFinishAsScheduledYest = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), Lists.newArrayList(OnlineClass.Status.FINISHED), null, Lists.newArrayList(OnlineClass.FinishType.AS_SCHEDULED), null);
		long trialFinishWithProblemYest = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), Lists.newArrayList(OnlineClass.Status.FINISHED), null, null, Lists.newArrayList(OnlineClass.FinishType.AS_SCHEDULED));
		long trialCanceledYest = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_SALES, staffIds,  DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), Lists.newArrayList(OnlineClass.Status.CANCELED),null,null,null);
		//long trialBookedYest = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), Lists.newArrayList(OnlineClass.Status.BOOKED),null,null,null);
		long orderTodayPayConfirmed = orderRepository.countForLeads(Leads.OwnerType.STAFF_SALES, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), Order.Status.PAY_CONFIRMED);
		long orderToPay = orderRepository.countForLeads(Leads.OwnerType.STAFF_SALES, staffIds, null, null, Order.Status.TO_PAY);		
		
		DashboardInfo dashboardInfo = new DashboardInfo();
		dashboardInfo.setLeadsToday(leadsToday);
		dashboardInfo.setLeadsTodayNotFollow(leadsTodayNotFollow);
		dashboardInfo.setLeadsPreviousNotContact(leadsPreviousNotContact);
		//dashboardInfo.setLeadsPreviousCallFailed(leadsPreviousCallFailed);
		dashboardInfo.setLeadsWithTrialExpireTmw(leadsWithTrialExpireTmw);
		dashboardInfo.setLeadsWithoutTrialExpireTmw(leadsWithoutTrialExpireTmw);
		dashboardInfo.setLeadsTotal(leadsTotal);
		dashboardInfo.setNeedFollowupToday(needFollowupToday);
		dashboardInfo.setFollowupedToday(followupedToday);
		dashboardInfo.setTrialTotalToday(trialTotalToday);
		dashboardInfo.setTrialFinishAsScheduledToday(trialFinishAsScheduledToday);
		dashboardInfo.setTrialFinishWithProblemToday(trialFinishWithProblemToday);
		dashboardInfo.setTrialCanceledToday(trialCanceledToday);
		dashboardInfo.setTrialBookedToday(trialBookedToday);
		dashboardInfo.setTrialTotalYest(trialTotalYest);
		dashboardInfo.setTrialFinishAsScheduledYest(trialFinishAsScheduledYest);
		dashboardInfo.setTrialFinishWithProblemYest(trialFinishWithProblemYest);
		dashboardInfo.setTrialCanceledYest(trialCanceledYest);
		//dashboardInfo.setTrialBookedYest(trialBookedYest);
		dashboardInfo.setOrderTodayPayConfirmed(orderTodayPayConfirmed);
		dashboardInfo.setOrderToPay(orderToPay);
		
		return dashboardInfo;
	}
	
	public DashboardInfo listDashboardInfoForTmk(List<Long> staffIds) {
		
		long leadsToday = leadsRepository.countLeadsForTmk(staffIds, DateTimeUtils.getBeginningOfTheDay(), null, null, null, true, null,null);
		long leadsTodayNotFollow = leadsRepository.countLeadsForTmk(staffIds, DateTimeUtils.getBeginningOfTheDay(), null, Lists.newArrayList(Status.DEFAULT.getCode(),Status.ASSIGNED.getCode()), null, true,false,null);
		long leadsPreviousNotContact = leadsRepository.countLeadsForTmk(staffIds, null, DateTimeUtils.getYesterday(0), Lists.newArrayList(Status.DEFAULT.getCode(),Status.ASSIGNED.getCode()), null, true,false,null);
		//long leadsPreviousCallFailed = leadsRepository.countLeadsForTmk(staffIds, null, DateTimeUtils.getYesterday(0), Lists.newArrayList(Status.ASSIGNED.getCode()), null);
		long leadsWithoutTrialExpireTmw = leadsRepository.countLeadsForTmk(staffIds, null, DateTimeUtils.getBeginningOfTheDay(DateTimeUtils.getDateByOffset(1-DAYS_TRIAL_BOOK_FOR_TMK, 0)), null, Lists.newArrayList(Status.BOOKEDTRIAL.getCode(),Status.PAYED.getCode()), true, null,false);
		//long leadsWithTrialExpireTmw = leadsRepository.countLeadsForTmk(staffIds, null, DateTimeUtils.getBeginningOfTheDay(DateTimeUtils.getDateByOffset(1-DAYS_PAY_FOR_SALES, 0)), Lists.newArrayList(Status.BOOKEDTRIAL.getCode()), null, true, null,false);
		long leadsTotal = leadsRepository.countLeadsForTmk(staffIds, null, null, null, null, true, null,null);
		long needFollowupToday = followUpRepository.countForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), null, null,true);
		long followupedToday = followUpRepository.countForLeads(Leads.OwnerType.STAFF_TMK, staffIds, null, null, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0),null);
		long trialTotalToday = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), null, null, null, null);
		long trialFinishAsScheduledToday = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), Lists.newArrayList(OnlineClass.Status.FINISHED), null, Lists.newArrayList(OnlineClass.FinishType.AS_SCHEDULED), null);
		long trialFinishWithProblemToday = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), Lists.newArrayList(OnlineClass.Status.FINISHED), null, null, Lists.newArrayList(OnlineClass.FinishType.AS_SCHEDULED));
		long trialCanceledToday = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), Lists.newArrayList(OnlineClass.Status.CANCELED),null,null,null);
		long trialBookedToday = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), Lists.newArrayList(OnlineClass.Status.BOOKED),null,null,null);
		long trialTotalYest = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), null, null, null, null);
		long trialFinishAsScheduledYest = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), Lists.newArrayList(OnlineClass.Status.FINISHED), null, Lists.newArrayList(OnlineClass.FinishType.AS_SCHEDULED), null);
		long trialFinishWithProblemYest = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), Lists.newArrayList(OnlineClass.Status.FINISHED), null, null, Lists.newArrayList(OnlineClass.FinishType.AS_SCHEDULED));
		long trialCanceledYest = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_TMK, staffIds,  DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), Lists.newArrayList(OnlineClass.Status.CANCELED),null,null,null);
		//long trialBookedYest = onlineClassRepository.countTrialClassForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), Lists.newArrayList(OnlineClass.Status.BOOKED),null,null,null);
		long orderTodayPayConfirmed = orderRepository.countForLeads(Leads.OwnerType.STAFF_TMK, staffIds, DateTimeUtils.getBeginningOfTheDay(), DateTimeUtils.getToday(0), Order.Status.PAY_CONFIRMED);
		long orderToPay = orderRepository.countForLeads(Leads.OwnerType.STAFF_TMK, staffIds, null, null, Order.Status.TO_PAY);

		DashboardInfo dashboardInfo = new DashboardInfo();
		dashboardInfo.setLeadsToday(leadsToday);
		dashboardInfo.setLeadsTodayNotFollow(leadsTodayNotFollow);
		dashboardInfo.setLeadsPreviousNotContact(leadsPreviousNotContact);
		//dashboardInfo.setLeadsPreviousCallFailed(leadsPreviousCallFailed);
		dashboardInfo.setLeadsWithTrialExpireTmw(0);
		dashboardInfo.setLeadsWithoutTrialExpireTmw(leadsWithoutTrialExpireTmw);
		dashboardInfo.setLeadsTotal(leadsTotal);
		dashboardInfo.setNeedFollowupToday(needFollowupToday);
		dashboardInfo.setFollowupedToday(followupedToday);
		dashboardInfo.setTrialTotalToday(trialTotalToday);
		dashboardInfo.setTrialFinishAsScheduledToday(trialFinishAsScheduledToday);
		dashboardInfo.setTrialFinishWithProblemToday(trialFinishWithProblemToday);
		dashboardInfo.setTrialCanceledToday(trialCanceledToday);
		dashboardInfo.setTrialBookedToday(trialBookedToday);
		dashboardInfo.setTrialTotalYest(trialTotalYest);
		dashboardInfo.setTrialFinishAsScheduledYest(trialFinishAsScheduledYest);
		dashboardInfo.setTrialFinishWithProblemYest(trialFinishWithProblemYest);
		dashboardInfo.setTrialCanceledYest(trialCanceledYest);
		//dashboardInfo.setTrialBookedYest(trialBookedYest);
		dashboardInfo.setOrderTodayPayConfirmed(orderTodayPayConfirmed);
		dashboardInfo.setOrderToPay(orderToPay);
		
		return dashboardInfo;
	}
	
	/**
	 * CLT mini dashboard 信息
	 * @param cltId
	 * @return
	 */
	public DashboardInfo listDashboardInfoForCLT(long cltId) {
		DashboardInfo cltInfo = new DashboardInfo();
		cltInfo.setCltNeverFollow(studentService.findCLTStudentsCount(null, null, null, null, null, null, CltStudent.DashType.NeverContact, null, null, cltId, null, null).getTotal());
		cltInfo.setCltTotal(studentService.findCLTStudentsCount(null, null, null, null, null, null, null, null, null, cltId, null, null).getTotal());
		cltInfo.setCltNewNeedFollowupToday(studentService.findCLTStudentsCount(null, null, null, null, null, null, CltStudent.DashType.NeedContact, null, null, cltId, null, "0").getTotal());
		cltInfo.setCltNewFollowUpedToday(studentService.findCLTStudentsCount(null, null, null, null, null, null, CltStudent.DashType.AlreadyContact, null, null, cltId, null, "0").getTotal());
		cltInfo.setCltOldNeedFollowupToday(studentService.findCLTStudentsCount(null, null, null, null, null, null, CltStudent.DashType.NeedContact, null, null, cltId, null, "1").getTotal());
		cltInfo.setCltOldFollowUpedToday(studentService.findCLTStudentsCount(null, null, null, null, null, null, CltStudent.DashType.AlreadyContact, null, null, cltId, null, "1").getTotal());
		
		List<Course> courseList =  courseRepository.findAll();
		//major
		cltInfo.setCltMajorCourseTotalToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.MAJOR), null, null, cltId, null, null));
		cltInfo.setCltMajorCourseBookedToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.MAJOR), OnlineClass.Status.BOOKED, null, cltId, null, null));
		cltInfo.setCltMajorCourseWithProblemToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.MAJOR), OnlineClass.Status.FINISHED, "WITH_PROBLEM", cltId, null, null));
		cltInfo.setCltMajorCourseTotalYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.MAJOR), null, null, cltId, null, null));
		cltInfo.setCltMajorCourseBookedYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.MAJOR), OnlineClass.Status.BOOKED, null, cltId, null, null));
		cltInfo.setCltMajorCourseWithProblemYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.MAJOR), OnlineClass.Status.FINISHED, "WITH_PROBLEM", cltId, null, null));
		//ASSESSMENT
		cltInfo.setCltAssessmentCourseTotalToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.DEMO), null, null, cltId, null, null));
		cltInfo.setCltAssessmentCourseBookedToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.DEMO), OnlineClass.Status.BOOKED, null, cltId, null, null));
		cltInfo.setCltAssessmentCourseWithProblemToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.DEMO), OnlineClass.Status.FINISHED, "WITH_PROBLEM", cltId, null, null));
		cltInfo.setCltAssessmentCourseTotalYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.DEMO), null, null, cltId, null, null));
		cltInfo.setCltAssessmentCourseBookedYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.DEMO), OnlineClass.Status.BOOKED, null, cltId, null, null));
		cltInfo.setCltAssessmentCourseWithProblemYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.DEMO), OnlineClass.Status.FINISHED, "WITH_PROBLEM", cltId, null, null));

		//Kick-off - GUIDE
		cltInfo.setCltGuideCourseTotalToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.GUIDE), null, null, cltId, null, null));
		cltInfo.setCltGuideCourseBookedToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.GUIDE), OnlineClass.Status.BOOKED, null, cltId, null, null));
		cltInfo.setCltGuideCourseWithProblemToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.GUIDE), OnlineClass.Status.FINISHED, "WITH_PROBLEM", cltId, null, null));
		cltInfo.setCltGuideCourseTotalYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.GUIDE), null, null, cltId, null, null));
		cltInfo.setCltGuideCourseBookedYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.GUIDE), OnlineClass.Status.BOOKED, null, cltId, null, null));
		cltInfo.setCltGuideCourseWithProblemYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.GUIDE), OnlineClass.Status.FINISHED, "WITH_PROBLEM", cltId, null, null));

		//CLT-REVIEW
		cltInfo.setCltReviewCourseTotalToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.REVIEW), null, null, cltId, null, null));
		cltInfo.setCltReviewCourseBookedToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.REVIEW), OnlineClass.Status.BOOKED, null, cltId, null, null));
		cltInfo.setCltReviewCourseWithProblemToday(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getToday(0), DateTimeUtils.getToday(0), getCourseIdsByCourseType(courseList,Course.Type.REVIEW), OnlineClass.Status.FINISHED, "WITH_PROBLEM", cltId, null, null));
		cltInfo.setCltReviewCourseTotalYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.REVIEW), null, null, cltId, null, null));
		cltInfo.setCltReviewCourseBookedYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.REVIEW), OnlineClass.Status.BOOKED, null, cltId, null, null));
		cltInfo.setCltReviewCourseWithProblemYest(onlineClassRepository.countOnlineClassForCLT(DateTimeUtils.getYesterday(0), DateTimeUtils.getYesterday(0), getCourseIdsByCourseType(courseList,Course.Type.REVIEW), OnlineClass.Status.FINISHED, "WITH_PROBLEM", cltId, null, null));

		
		return cltInfo;
	}
	
	private List<Long> getCourseIdsByCourseType(List<Course> courseList,Course.Type type) {
		List<Long> courseIds = Lists.newArrayList();
		for (Course c : courseList) {
			if (c.getType() == type) {
				courseIds.add(c.getId());
			}
		}
		return courseIds;
	}
	
	public List<OrderVo> listOrder(Date createDateTimeFrom, Date createDateTimeTo,Date paidDateTimeFrom, Date paidDateTimeTo,
			Order.Status status, PayBy payBy, Long salesId, Long tmkId, String searchText, Integer start, Integer length) {
		
		return orderRepository.listOrderForLeads(createDateTimeFrom, createDateTimeTo, paidDateTimeFrom, paidDateTimeTo, status, payBy, salesId, tmkId, searchText, start, length);
	}

	public long countOrder(Date createDateTimeFrom, Date createDateTimeTo,Date paidDateTimeFrom, Date paidDateTimeTo,
			Order.Status status, PayBy payBy,Long salesId, Long tmkId, String searchText) {
		return orderRepository.countOrderForLeads(createDateTimeFrom, createDateTimeTo, paidDateTimeFrom, paidDateTimeTo, status, payBy, salesId, tmkId, searchText);
	}

	public List<OnlineClassVo> listOnlineClass(Date scheduledTimeFrom, Date scheduledTimeTo, List<Long> courseIds, OnlineClass.Status status,
			String finishType,Long salesId, Long tmkId, String teacherName, String searchText, Integer start, Integer length) {

		return onlineClassRepository.listOnlineClassForLeads(scheduledTimeFrom, scheduledTimeTo, courseIds, status, finishType, salesId, tmkId, teacherName, searchText, start, length);
	}
	
	public long countOnlineClass(Date scheduledTimeFrom, Date scheduledTimeTo, List<Long> courseIds, OnlineClass.Status status,
			 String finishType,Long salesId, Long tmkId, String teacherName, String searchText) {
		
		return onlineClassRepository.countOnlineClassForLeads(scheduledTimeFrom, scheduledTimeTo, courseIds, status, finishType, salesId, tmkId,
				teacherName, searchText);
	}
	
	public long countLeadsForSales(Long salesId) {
		return leadsRepository.countLeadsForSales(salesId);
	}
	
	public long countLeadsForTmk(Long tmkId) {
		return leadsRepository.countLeadsForTmk(tmkId);
	}
	
	private void changeFollowup(long studentId) {
		Student student = studentService.find(studentId);
		if (null != student && student.getLifeCycle().equals(Student.LifeCycle.SIGNUP)) {
			Long payConfirmedOrdersCount = orderRepository.countPayConfirmedByStudentId(student.getId());
			if (payConfirmedOrdersCount > 0) {
				studentLifeCycleLogService.doChangeLifeCycle(student, Student.LifeCycle.SIGNUP, Student.LifeCycle.LEARNING);
			} else {
				studentLifeCycleLogService.doChangeLifeCycle(student, Student.LifeCycle.SIGNUP, Student.LifeCycle.ASSIGNED);
			}
		}
	}
	
	public ResponseVo doManualLeadsDispatchToSales(Staff sales,List<Long> leadsIds) {
		ResponseVo response = new ResponseVo();
		StringBuffer errorMsg = new StringBuffer();
		if (CollectionUtils.isNotEmpty(leadsIds)) {
			User assinger = securityService.getCurrentUser();
			Long leadsId = null;
			for (int i = 0; i < leadsIds.size(); i++) {
				leadsId = leadsIds.get(i);
				Leads leads = leadsRepository.find(leadsId);
				if (leads != null) {
					long totalLeads = leadsRepository.countLeadsForSales(sales.getId());
					if (MAX_LEADS_FOR_SALES > totalLeads) {
						LeadsDispatchHistory leadsDispatchHistory = this.buildDispatchHistory(leads,sales,assinger);
						assignLeadsToSales(leads, sales);
						leadsDispatchHistoryRepository.create(leadsDispatchHistory);
						changeFollowup(leads.getStudentId());
					} else {
						errorMsg.append("leads总数已经达到上限,部分leads分配失败,成功:")
								.append(i).append("条,失败:").append(leadsIds.size() - i).append("条");
					}
				}
				
			}
		}
		if (errorMsg.length() > 0) {
			response.setStatus(RespStatus.FAIL);
			response.setMessage(errorMsg.toString());
		} else {
			response.setStatus(RespStatus.SUCCESS);
		}
		return response;
	}
	
	public ResponseVo doManualLeadsDispatchToSales(User sales,Long leadsId) {
		long totalLeads = leadsRepository.countLeadsForSales(sales.getId());
		if (MAX_LEADS_FOR_SALES > totalLeads) {
			Leads leads = leadsRepository.find(leadsId);
			if (leads != null) {
				LeadsDispatchHistory leadsDispatchHistory = this.buildDispatchHistory(leads,sales,securityService.getCurrentUser());
				assignLeadsToSales(leads, sales);
				leadsDispatchHistoryRepository.create(leadsDispatchHistory);
				changeFollowup(leads.getStudentId());
			} else {
				return new ResponseVo(RespStatus.FAIL,"leads不存在");
			}
		} else {
			return new ResponseVo(RespStatus.FAIL,"leads分配失败,leads总数已经达到上限");
		}
		return new ResponseVo(RespStatus.SUCCESS,"");
	}
	
	public ResponseVo doManualLeadsDispatchToTmk(Staff tmk,List<Long> leadsIds) {

		ResponseVo response = new ResponseVo();
		StringBuffer errorMsg = new StringBuffer();
		if (CollectionUtils.isNotEmpty(leadsIds)) {
			User assinger = securityService.getCurrentUser();
			Long leadsId = null;
			for (int i = 0; i < leadsIds.size(); i++) {
				leadsId = leadsIds.get(i);
				Leads leads = leadsRepository.find(leadsId);
				if (leads != null) {
					long totalLeads = leadsRepository.countLeadsForTmk(tmk.getId());
					if (MAX_LEADS_FOR_TMK > totalLeads) {
						LeadsDispatchHistory leadsDispatchHistory = this.buildDispatchHistory(leads,tmk,assinger);
						assignLeadsToTmk(leads, tmk);
						leadsDispatchHistoryRepository.create(leadsDispatchHistory);
						changeFollowup(leads.getStudentId());
					} else {
						errorMsg.append("leads总数已经达到上限,部分leads分配失败,成功:")
								.append(i).append("条,失败:").append(leadsIds.size() - i).append("条");
					}
				}
				
			}
		}
		if (errorMsg.length() > 0) {
			response.setStatus(RespStatus.FAIL);
			response.setMessage(errorMsg.toString());
		} else {
			response.setStatus(RespStatus.SUCCESS);
		}
		return response;
	}
	
	public ResponseVo doManualLeadsDispatchToTmk(User tmk,Long leadsId) {
		long totalLeads = leadsRepository.countLeadsForTmk(tmk.getId());
		if (MAX_LEADS_FOR_TMK > totalLeads) {
			Leads leads = leadsRepository.find(leadsId);
			if (leads != null) {
				LeadsDispatchHistory leadsDispatchHistory = this.buildDispatchHistory(leads,tmk,securityService.getCurrentUser());
				assignLeadsToTmk(leads, tmk);
				leadsDispatchHistoryRepository.create(leadsDispatchHistory);
				changeFollowup(leads.getStudentId());
			} else {
				return new ResponseVo(RespStatus.FAIL,"leads不存在");
			}
		} else {
			return new ResponseVo(RespStatus.FAIL,"leads分配失败,leads总数已经达到上限");
		}
		return new ResponseVo(RespStatus.SUCCESS,"");
	}
	
	public void doReleaseLeads(Leads leads) {
		LeadsDispatchHistory leadsDispatchHistory = this.buildDispatchHistory(leads,null,securityService.getCurrentUser());
		leads.setLocked(false);
		leads.setSalesId(-1L);
		leads.setSalesName(null);
		leads.setSalesAssignTime(null);
		leads.setTmkId(-1L);
		leads.setTmkName(null);
		leads.setTmkAssignTime(null);
		leads.setLockedTime(null);
		leads.setLocked(false);
		leads.setStatus(Status.DEFAULT.getCode());
		leads.setLibrary(true);
		leads.setContact(false);
		leads.setOwnerType(Leads.OwnerType.DEFAULT.getCode());
		leadsRepository.update(leads);
		leadsDispatchHistoryRepository.create(leadsDispatchHistory);
		
	}
	
	public void doLockLeads(Leads leads) {
		leads.setLocked(true);
		leads.setLockedTime(new Date());
		leadsRepository.update(leads);
	}
	
	public void doUnLockLeads(Leads leads) {
		leads.setLocked(false);
		leads.setLockedTime(null);
		leadsRepository.update(leads);
	}


	public Leads findByStudentId (Long studentId) {
		return leadsRepository.findByStudentId(studentId);
	}
	
	
	public Leads find(Long id) {
		return leadsRepository.find(id);
	}
	
	public boolean  isReachMaxLeadsSizeTakeLeadsFromLibraryForSales(Long salesId) {
		long count = leadsDispatchHistoryRepository.findDispatchHistorySize(salesId,
				salesId, DateTimeUtils.getToday(0),DateTimeUtils.getToday(0));
		if (MAX_LEADS_TAKE_FROM_LIBRARY_EVERYDAY_FOR_SALES > count) {
			return false;
		} else {
			return true;
		}
	}
	public boolean isReachMaxLeadsSizeTakeFromLibraryForTmk(Long tmkId) {
		long count = leadsDispatchHistoryRepository.findDispatchHistorySize(tmkId,
				tmkId, DateTimeUtils.getToday(0),DateTimeUtils.getToday(0));
		if (MAX_LEADS_TAKE_FROM_LIBRARY_EVERYDAY_FOR_TMK > count) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isReachMaxLeadsReleaseSizeForSales(Long salesId) {
		long count = leadsDispatchHistoryRepository.findDispatchHistorySize(salesId,
				-1L, DateTimeUtils.getToday(0),DateTimeUtils.getToday(0));
		if (MAX_LEADS_RELEASE_EVERYDAY_FOR_SALES > count) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isReachMaxLockedLeadsSizeForSales(Long salesId) {
		long count = leadsRepository.countLockedLeadsForSales(salesId);
		if (MAX_LEADS_LOCK_FOR_SALES > count) {
			return false;
		} else {
			return true;
		}
	}
	
	public void updateLeadsStatus(Long studentId,Long userId,int status) {
		if (studentId == null || userId == null) {
			return;
		}
		
		Leads leads = leadsRepository.findByStudentId(studentId);
		if (leads != null) {
			if (leads.getOwnerType() == Leads.OwnerType.STAFF_SALES.getCode()) {
				if (leads.getSalesId() == null || !leads.getSalesId().equals(userId)) {
					return;
				}
			} else if (leads.getOwnerType() == Leads.OwnerType.STAFF_TMK.getCode()) {
				if (leads.getTmkId() == null || !leads.getTmkId().equals(userId)) {
					return;
				}
			} else {
				return;
			}
			
			if (status == Leads.Status.CONTACTED.getCode()) {
				if (leads.getStatus() != Leads.Status.CONTACTED.getCode()
						&& leads.getStatus() != Leads.Status.BOOKEDTRIAL.getCode()
						&& leads.getStatus() != Leads.Status.PAYED.getCode()) {
					leads.setStatus(Leads.Status.CONTACTED.getCode());
				}
			} else if (status == Leads.Status.BOOKEDTRIAL.getCode()) {
				if (leads.getStatus() != Leads.Status.BOOKEDTRIAL.getCode()
						&& leads.getStatus() != Leads.Status.PAYED.getCode()) {
					leads.setStatus(Leads.Status.BOOKEDTRIAL.getCode());
				}
			} else if (status == Leads.Status.PAYED.getCode()) {
				if (leads.getStatus() != Leads.Status.PAYED.getCode()) {
					leads.setStatus(Leads.Status.PAYED.getCode());
				}
			}
			leads.setContact(true);
			leadsRepository.update(leads);
		}
	}
	
	private LeadsDispatchHistory buildDispatchHistory(Leads leads,User nextUser, User assinger) {
		
		LeadsDispatchHistory dispatchHistory = new LeadsDispatchHistory();
		dispatchHistory.setLeadsId(leads.getId());
		dispatchHistory.setLeadsStatus(leads.getStatus());
		dispatchHistory.setAssignTime(new Date());
		
		if (nextUser != null) {
			dispatchHistory.setUserId(nextUser.getId());
			dispatchHistory.setUserName(nextUser.getName());
		} else {
			dispatchHistory.setUserId(-1L);
			dispatchHistory.setUserName(null);
		}
		
		if (assinger != null) {
			dispatchHistory.setAssignerId(assinger.getId());
		} else {
			dispatchHistory.setAssignerId(-1L);
		}
		
		if (leads.getOwnerType() == Leads.OwnerType.DEFAULT.getCode()) {
			dispatchHistory.setPreUserId(-1L);
			dispatchHistory.setPreUserName(null);
		} else if (leads.getOwnerType() == Leads.OwnerType.STAFF_SALES_DIRECTOR.getCode()) {
			dispatchHistory.setPreUserId(leads.getTmkId());
			dispatchHistory.setPreUserName(leads.getTmkName());
		} else if (leads.getOwnerType() == Leads.OwnerType.STAFF_SALES.getCode()) {
			dispatchHistory.setPreUserId(leads.getSalesId());
			dispatchHistory.setPreUserName(leads.getSalesName());
		} else if (leads.getOwnerType() == Leads.OwnerType.STAFF_TMK.getCode()) {
			dispatchHistory.setPreUserId(leads.getTmkId());
			dispatchHistory.setPreUserName(leads.getTmkName());
		}
		
		return dispatchHistory;
	}
	private void assignLeadsToTmk(Leads leads, User tmk) {
		leads.setTmkId(tmk.getId());
		leads.setTmkName(tmk.getName());
		leads.setTmkAssignTime(new Date());
		leads.setSalesId(-1L);
		leads.setSalesName(null);
		leads.setSalesAssignTime(null);
		leads.setLocked(false);
		leads.setLockedTime(null);
		leads.setLibrary(false);
		leads.setContact(false);
		leads.setOwnerType(Leads.OwnerType.STAFF_TMK.getCode());
		if (leads.getStatus() == Status.DEFAULT.getCode() || leads.getStatus() == Status.CONTACTED.getCode()) {
			leads.setStatus(Status.ASSIGNED.getCode());
		}
		leadsRepository.update(leads);
	}
	
	private void assignLeadsToSales(Leads leads, User sales) {
		leads.setSalesId(sales.getId());
		leads.setSalesName(sales.getName());
		leads.setSalesAssignTime(new Date());
		leads.setLocked(false);
		leads.setLockedTime(null);
		leads.setLibrary(false);
		leads.setContact(false);
		leads.setOwnerType(Leads.OwnerType.STAFF_SALES.getCode());
		if (leads.getStatus() == Status.DEFAULT.getCode() || leads.getStatus() == Status.CONTACTED.getCode()
				|| leads.getStatus() == Status.ASSIGNED.getCode()) {
			//手动分配,leads状态没有在trial booked 之前,需清空tmk信息
			leads.setTmkId(-1L);
			leads.setTmkName(null);
			leads.setTmkAssignTime(null);
			leads.setStatus(Status.ASSIGNED.getCode());
		}
		leadsRepository.update(leads);
	}
	
	public Leads creatDefaultLeadsInfo(Long studentId) {
		Student student = studentService.find(studentId);
		if (student == null) {
			return null;
		}
		
    	Leads leads = new Leads();
  
    	String channel = null;
    	String leadType = null;
    	if (student.getChannel() != null) {
    		channel = student.getChannel().getSourceName();
    		leadType = student.getChannel().getLevel();
    	}
    	
    	leads.setStudentId(student.getId());
    	leads.setFamilyId(student.getFamily().getId());
    	leads.setCreateTime(new Date());
    	leads.setRegisterTime(student.getRegisterDateTime());
    	leads.setChannel(StringUtils.isBlank(channel) ? "unknown" : channel);
    	leads.setLeadType(StringUtils.isBlank(leadType) ? 'C' : leadType.toCharArray()[0]);
    	leads.setSalesId(-1L);
    	leads.setTmkId(-1L);
    	leads.setOwnerType(OwnerType.DEFAULT.getCode());
    	leads.setLocked(false);
    	leads.setLockedTime(null);
    	leads.setLibrary(false);
    	leads.setContact(false);
    	leads.setStatus(Status.DEFAULT.getCode());
    	
    	return leadsRepository.create(leads);
	}
}
