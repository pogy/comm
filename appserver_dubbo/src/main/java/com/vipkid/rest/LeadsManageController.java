package com.vipkid.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.vipkid.model.Leads;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.Order;
import com.vipkid.model.Order.PayBy;
import com.vipkid.model.Role;
import com.vipkid.model.SalesTeam;
import com.vipkid.model.Staff;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.User;
import com.vipkid.security.SecurityService;
import com.vipkid.service.LeadsManageService;
import com.vipkid.service.SalesTeamService;
import com.vipkid.service.StaffService;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.LeadsAgeRange;
import com.vipkid.service.pojo.leads.DashboardInfo;
import com.vipkid.service.pojo.leads.LeadsDispatchVo;
import com.vipkid.service.pojo.leads.LeadsVo;
import com.vipkid.service.pojo.leads.OnlineClassVo;
import com.vipkid.service.pojo.leads.OrderVo;
import com.vipkid.service.pojo.leads.ResponseVo;
import com.vipkid.service.pojo.leads.ResponseVo.RespStatus;

@RestController
@RequestMapping(value="/api/service/private/leadsDispatch")
public class LeadsManageController {
	
	private Logger logger = LoggerFactory.getLogger(LeadsManageController.class.getSimpleName());
	
	@Resource
	private LeadsManageService leadsManageService;
	@Resource
	private StaffService staffService;
	@Resource
	SalesTeamService salesTeamService;
	@Resource
	SecurityService securityService;

	@RequestMapping(value="/listLeads",method = RequestMethod.GET)
	public List<LeadsVo> listLeads(
			@RequestParam(value = "tmkAssignTimeFrom", required=false) Long tmkAssignTimeFrom,
			@RequestParam(value = "tmkAssignTimeTo", required=false) Long tmkAssignTimeTo,
			@RequestParam(value = "salesAssignTimeFrom", required=false) Long salesAssignTimeFrom,
			@RequestParam(value = "salesAssignTimeTo", required=false) Long salesAssignTimeTo,
			@RequestParam(value = "followUpTimeFrom", required=false) Long followUpTimeFrom,
			@RequestParam(value = "followUpTimeTo", required=false) Long followUpTimeTo,
			@RequestParam(value = "lifeCycle", required=false) LifeCycle lifeCycle,
			@RequestParam(value = "channelId", required=false) Long channelId,
			@RequestParam(value = "customerStage", required=false) Integer customerStage,
			@RequestParam(value = "salesId", required=false) Long salesId,
			@RequestParam(value = "tmkId", required=false) Long tmkId,
			@RequestParam(value = "searchText", required=false) String searchText,
			@RequestParam(value = "status", required=false) Integer status,
			@RequestParam(value = "contact", required=false) Integer contact,
			@RequestParam(value = "locked", required=false) Boolean locked,
			@RequestParam(value = "channelLevel", required=false) String channelLevel,
			@RequestParam(value = "ageRange", required=false) LeadsAgeRange ageRange,
			@RequestParam(value="start",required=false) Integer start,
			@RequestParam(value="length",required=false) Integer length) {
		logger.info("listLeads, params: tmkAssignTimeFrom = {},tmkAssignTimeTo = {},salesAssignTimeFrom = {},salesAssignTimeTo = {},followUpTimeFrom = {},followUpTimeTo = {},lifeCycle = {},channelId = {},customerStage = {},salesId = {},tmkId = {},searchText = {}, status = {},contact = {},locked = {},channelLevel = {},ageRange = {}, start = {},length = {}",
				tmkAssignTimeFrom,tmkAssignTimeTo,salesAssignTimeFrom,salesAssignTimeTo,followUpTimeFrom,followUpTimeTo,lifeCycle,channelId,customerStage,salesId,tmkId,searchText,status,contact,locked,channelLevel,ageRange,start,length);
		
		return leadsManageService.listLeads(
				tmkAssignTimeFrom != null ? new Date(tmkAssignTimeFrom) : null,
				tmkAssignTimeTo != null ? new Date(tmkAssignTimeTo) : null,
				salesAssignTimeFrom != null ? new Date(salesAssignTimeFrom) : null,
				salesAssignTimeTo != null ? new Date(salesAssignTimeTo) : null,
				followUpTimeFrom != null ? new Date(followUpTimeFrom) : null,
				followUpTimeTo != null ? new Date(followUpTimeTo) : null,
			lifeCycle, channelId, customerStage, salesId, tmkId, searchText, status, contact, locked, channelLevel, ageRange, start, length);
	}
	
	@RequestMapping(value="/countLeads",method = RequestMethod.GET)
	public Count countLeads(
			@RequestParam(value = "tmkAssignTimeFrom", required=false) Long tmkAssignTimeFrom,
			@RequestParam(value = "tmkAssignTimeTo", required=false) Long tmkAssignTimeTo,
			@RequestParam(value = "salesAssignTimeFrom", required=false) Long salesAssignTimeFrom,
			@RequestParam(value = "salesAssignTimeTo", required=false) Long salesAssignTimeTo,
			@RequestParam(value = "followUpTimeFrom", required=false) Long followUpTimeFrom,
			@RequestParam(value = "followUpTimeTo", required=false) Long followUpTimeTo,
			@RequestParam(value = "lifeCycle", required=false) LifeCycle lifeCycle,
			@RequestParam(value = "channelId", required=false) Long channelId,
			@RequestParam(value = "customerStage", required=false) Integer customerStage,
			@RequestParam(value = "salesId", required=false) Long salesId,
			@RequestParam(value = "tmkId", required=false) Long tmkId,
			@RequestParam(value = "searchText", required=false) String searchText,
			@RequestParam(value = "status", required=false) Integer status,
			@RequestParam(value = "contact", required=false) Integer contact,
			@RequestParam(value = "locked", required=false) Boolean locked,
			@RequestParam(value = "channelLevel", required=false) String channelLevel,
			@RequestParam(value = "ageRange", required=false) LeadsAgeRange ageRange){
		
		long count = leadsManageService.countLeads(
				tmkAssignTimeFrom != null ? new Date(tmkAssignTimeFrom) : null,
				tmkAssignTimeTo != null ? new Date(tmkAssignTimeTo) : null,
				salesAssignTimeFrom != null ? new Date(salesAssignTimeFrom) : null,
				salesAssignTimeTo != null ? new Date(salesAssignTimeTo) : null,
				followUpTimeFrom != null ? new Date(followUpTimeFrom) : null,
				followUpTimeTo != null ? new Date(followUpTimeTo) : null,
			lifeCycle, channelId, customerStage, salesId, tmkId, searchText, status,contact, locked,channelLevel, ageRange);
			return new Count(count);
	}
	
	@RequestMapping(value="/listDashboardInfo",method = RequestMethod.GET)
	public DashboardInfo listDashboardInfo(@RequestParam(value = "id", required=true) Long id) {
		logger.info("listDashboardInfo, params: id = {}", id);

		DashboardInfo dashboardInfo = new DashboardInfo();
		Staff staff = staffService.find(id);
		if (staff != null) {
			if (this.hasRole(staff, Role.STAFF_SALES)) {
				dashboardInfo = leadsManageService.listDashboardInfoForSales(Lists.newArrayList(id));
			} else if (this.hasRole(staff, Role.STAFF_TMK)) {
				dashboardInfo = leadsManageService.listDashboardInfoForTmk(Lists.newArrayList(id));
			}
		}
		return dashboardInfo;
	}

	@RequestMapping(value="/listCLTDashboardInfo",method = RequestMethod.GET)
	public DashboardInfo listCLTDashboardInfo(@RequestParam(value = "id", required=true) Long id) {
		return leadsManageService.listDashboardInfoForCLT(id);
	}
	
	@RequestMapping(value="/listTeamDashboardInfo",method = RequestMethod.GET)
	public DashboardInfo listTeamDashboardInfo(@RequestParam(value = "teamId", required=true) Long teamId) {
		logger.info("listTeamDashboardInfo, params: teamId = {}", teamId);
		DashboardInfo dashboardInfo = new DashboardInfo();
		
		SalesTeam salesTeam = salesTeamService.find(teamId);
		
		if (salesTeam != null) {
			List<Staff> staffs = staffService.findByTeamId(salesTeam.getId());
			if (CollectionUtils.isNotEmpty(staffs)) {
				List<Long> staffIds = Lists.newArrayList();
				for (Staff staff : staffs) {
					staffIds.add(staff.getId());
				}
				if (salesTeam.getType() == SalesTeam.Type.SALES) {
					dashboardInfo = leadsManageService.listDashboardInfoForSales(staffIds);
				} else if (salesTeam.getType() == SalesTeam.Type.TMK) {
					dashboardInfo = leadsManageService.listDashboardInfoForTmk(staffIds);
				}
			}
		}
		
		
		return dashboardInfo;
	}
	
	@RequestMapping(value="/listOrder",method = RequestMethod.GET)
	public List<OrderVo> listOrder(
			@RequestParam(value = "createDateTimeFrom", required = false) Long createDateTimeFrom,
			@RequestParam(value = "createDateTimeTo", required = false) Long createDateTimeTo,
			@RequestParam(value = "paidDateTimeFrom", required = false) Long paidDateTimeFrom,
			@RequestParam(value = "paidDateTimeTo", required = false) Long paidDateTimeTo,
			@RequestParam(value = "status", required = false) Order.Status status,
			@RequestParam(value = "payBy", required = false) PayBy payBy,
			@RequestParam(value = "salesId", required = false) Long salesId,
			@RequestParam(value = "tmkId", required = false) Long tmkId,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length) {
		logger.info("listLeads, params: createDateTimeFrom = {},createDateTimeTo = {},paidDateTimeFrom = {},paidDateTimeTo = {},status = {},payBy = {},salesId = {},tmkId = {},searchText = {},start = {},length = {}",
				createDateTimeFrom,createDateTimeTo,paidDateTimeFrom,paidDateTimeTo,status,payBy,salesId,tmkId,searchText,start,length);
	
		return leadsManageService.listOrder(
				createDateTimeFrom != null ? new Date(createDateTimeFrom) : null,
				createDateTimeTo != null ? new Date(createDateTimeTo) : null,
				paidDateTimeFrom != null ? new Date(paidDateTimeFrom) : null,
				paidDateTimeTo != null ? new Date() : null,
				status, payBy, salesId, tmkId, searchText, start, length);
	}
	
	@RequestMapping(value="/countOrder",method = RequestMethod.GET)
	public Count countOrder(
			@RequestParam(value = "createDateTimeFrom", required = false) Long createDateTimeFrom,
			@RequestParam(value = "createDateTimeTo", required = false) Long createDateTimeTo,
			@RequestParam(value = "paidDateTimeFrom", required = false) Long paidDateTimeFrom,
			@RequestParam(value = "paidDateTimeTo", required = false) Long paidDateTimeTo,
			@RequestParam(value = "status", required = false) Order.Status status,
			@RequestParam(value = "payBy", required = false) PayBy payBy,
			@RequestParam(value = "salesId", required = false) Long salesId,
			@RequestParam(value = "tmkId", required = false) Long tmkId,
			@RequestParam(value = "searchText", required = false) String searchText) {
		logger.info("listLeads, params: createDateTimeFrom = {},createDateTimeTo = {},paidDateTimeFrom = {},paidDateTimeTo = {},status = {},payBy = {},salesId = {},tmkId = {},searchText = {}",
				createDateTimeFrom,createDateTimeTo,paidDateTimeFrom,paidDateTimeTo,status,payBy,salesId,tmkId,searchText);
	
		long count = leadsManageService.countOrder(
				createDateTimeFrom != null ? new Date(createDateTimeFrom) : null,
				createDateTimeTo != null ? new Date(createDateTimeTo) : null,
				paidDateTimeFrom != null ? new Date(paidDateTimeFrom) : null,
				paidDateTimeTo != null ? new Date() : null,
				status, payBy, salesId, tmkId, searchText);
		return new Count(count);
	}
	
	@RequestMapping(value="/listOnlineClass",method = RequestMethod.GET)
	public List<OnlineClassVo> listOnlineClass(
			@RequestParam(value = "scheduledTimeFrom", required = false) Long scheduledTimeFrom,
			@RequestParam(value = "scheduledTimeTo", required = false) Long scheduledTimeTo,
			@RequestParam(value = "courseIds", required = false) Long[] courseIds,
			@RequestParam(value = "status", required = false) Status status,
			@RequestParam(value = "finishType", required = false) String finishType,
			@RequestParam(value = "salesId", required = false) Long salesId,
			@RequestParam(value = "tmkId", required = false) Long tmkId,
			@RequestParam(value = "teacherName", required = false) String teacherName,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "start", required = false) Integer start,
			@RequestParam(value = "length", required = false) Integer length) {
		
		logger.info("listOnlineClass, params: scheduledTimeFrom = {},scheduledTimeTo = {},courseIds = {},status = {},finishType = {},salesId = {},tmkId = {},teacherName = {},searchText = {},start = {},length = {}",
				scheduledTimeFrom,scheduledTimeTo,courseIds,status,finishType,salesId,tmkId,teacherName,searchText,start,length);
	
		return leadsManageService.listOnlineClass(
				scheduledTimeFrom != null ? new Date(scheduledTimeFrom) : null,
				scheduledTimeTo != null ? new Date(scheduledTimeTo) : null,
				courseIds != null ? Lists.newArrayList(courseIds) : null,
				status, finishType, salesId, tmkId,teacherName, searchText, start, length);
	}
	
	@RequestMapping(value="/countOnlineClass",method = RequestMethod.GET)
	public Count countOnlineClass(
			@RequestParam(value = "scheduledTimeFrom", required = false) Long scheduledTimeFrom,
			@RequestParam(value = "scheduledTimeTo", required = false) Long scheduledTimeTo,
			@RequestParam(value = "courseIds", required = false) Long[] courseIds,
			@RequestParam(value = "status", required = false) Status status,
			@RequestParam(value = "finishType", required = false) String finishType,
			@RequestParam(value = "salesId", required = false) Long salesId,
			@RequestParam(value = "tmkId", required = false) Long tmkId,
			@RequestParam(value = "teacherName", required = false) String teacherName,
			@RequestParam(value = "searchText", required = false) String searchText) {
		
		logger.info("listOnlineClass, params: scheduledTimeFrom = {},scheduledTimeTo = {},courseIds = {},status = {},finishType = {},salesId = {},tmkId = {},teacherName = {},searchText = {}",
				scheduledTimeFrom,scheduledTimeTo,courseIds,status,finishType,salesId,tmkId,teacherName,searchText);
	
		long count =  leadsManageService.countOnlineClass(
				scheduledTimeFrom != null ? new Date(scheduledTimeFrom) : null,
				scheduledTimeTo != null ? new Date(scheduledTimeTo) : null,
				courseIds != null ? Lists.newArrayList(courseIds) : null,
				status, finishType, salesId, tmkId,teacherName, searchText);
		return new Count(count);
	}
	
	
	@RequestMapping(value="/manualLeadsAssign",method = RequestMethod.PUT)
	public ResponseVo doManualDispatch(@RequestBody(required = true) LeadsDispatchVo leadsDispatchVo) {
		if (leadsDispatchVo == null || leadsDispatchVo.getStaffId() == null
				|| CollectionUtils.isEmpty(leadsDispatchVo.getLeadsList())) {
			return new ResponseVo(RespStatus.FAIL, "request params error");
		}

		Staff staff = staffService.find(leadsDispatchVo.getStaffId());
		if (staff == null) {
			return new ResponseVo(RespStatus.FAIL, "staff is not exist");
		}
		
		ResponseVo response = null;
		if (this.hasRole(staff, Role.STAFF_SALES)) {
			response = leadsManageService.doManualLeadsDispatchToSales(staff, leadsDispatchVo.getLeadsList());
		} else if (this.hasRole(staff, Role.STAFF_TMK)) {
			response = leadsManageService.doManualLeadsDispatchToTmk(staff, leadsDispatchVo.getLeadsList());
		}
		
		return response;
	}
	
	@RequestMapping(value="/selfLeadsAssign",method = RequestMethod.PUT)
	public ResponseVo doSelfLeadsAssign(@RequestBody(required = true) LeadsDispatchVo leadsDispatchVo) {
		if (leadsDispatchVo == null || leadsDispatchVo.getStaffId() == null
				|| CollectionUtils.isEmpty(leadsDispatchVo.getLeadsList())) {
			return new ResponseVo(RespStatus.FAIL, "request params error");
		}

		User curUser = securityService.getCurrentUser();
		if (Long.compare(curUser.getId(), leadsDispatchVo.getStaffId()) != 0) {
			return new ResponseVo(RespStatus.FAIL, " assignee is not match current user");
		}
		
		ResponseVo response = null;
		if (this.hasRole(curUser, Role.STAFF_SALES)) {
			if (!leadsManageService.isReachMaxLeadsSizeTakeLeadsFromLibraryForSales(curUser.getId())) {
				response = leadsManageService.doManualLeadsDispatchToSales(curUser, leadsDispatchVo.getLeadsList().get(0));
			} else {
				response = new ResponseVo(RespStatus.FAIL,"您今天挖取LEADS个数已经达到上限");
			}
		} else if (this.hasRole(curUser, Role.STAFF_TMK)) {
			if (!leadsManageService.isReachMaxLeadsSizeTakeFromLibraryForTmk(curUser.getId())) {
				response = leadsManageService.doManualLeadsDispatchToTmk(curUser, leadsDispatchVo.getLeadsList().get(0));
			} else {
				response = new ResponseVo(RespStatus.FAIL,"您今天挖取LEADS个数已经达到上限");
			}
		} else {
			response = new ResponseVo(RespStatus.FAIL,"您没有权限挖取LEADS");
		}
		
		return response;
	}
	
	
	@RequestMapping(value="/releaseLeads",method = RequestMethod.POST)
	public ResponseVo doReleaseLeads(@RequestParam(value = "leadsId", required = true) Long leadsId) {
		Leads leads = leadsManageService.find(leadsId);
		if (leads == null) {
			return new ResponseVo(RespStatus.FAIL, " leads is not exist");
		}
		
		User curUser = securityService.getCurrentUser();
		if (this.hasRole(curUser, Role.STAFF_SALES_DIRECTOR) || this.hasRole(curUser, Role.STAFF_ADMIN)
				|| (this.hasRole(curUser, Role.STAFF_SALES) && leads.getOwnerType() == Leads.OwnerType.STAFF_SALES.getCode()
					&& Long.compare(curUser.getId(), leads.getSalesId()) == 0)
				|| (this.hasRole(curUser, Role.STAFF_TMK) && leads.getOwnerType() == Leads.OwnerType.STAFF_TMK.getCode()
					&& Long.compare(curUser.getId(), leads.getTmkId()) == 0)) {
			if (this.hasRole(curUser, Role.STAFF_SALES)
					&& leadsManageService.isReachMaxLeadsReleaseSizeForSales(curUser.getId())) {
				return new ResponseVo(RespStatus.FAIL, "您今天释放LEADS个数已经达到上限");
			}
			leadsManageService.doReleaseLeads(leads);
		} else {
			return new ResponseVo(RespStatus.FAIL, " you have no right to release this leads");
		}		
		return new ResponseVo(RespStatus.SUCCESS, "");
	}
	
	@RequestMapping(value="/lockLeads",method = RequestMethod.POST)
	public ResponseVo doLockLeads(@RequestParam(value = "leadsId", required = true) Long leadsId) {
		Leads leads = leadsManageService.find(leadsId);
		if (leads == null) {
			return new ResponseVo(RespStatus.FAIL, " leads is not exist");
		}
		
		User curUser = securityService.getCurrentUser();
		if ((this.hasRole(curUser, Role.STAFF_SALES) && leads.getOwnerType() == Leads.OwnerType.STAFF_SALES.getCode()
					&& Long.compare(curUser.getId(), leads.getSalesId()) == 0)
				|| (this.hasRole(curUser, Role.STAFF_TMK) && leads.getOwnerType() == Leads.OwnerType.STAFF_TMK.getCode()
					&& Long.compare(curUser.getId(), leads.getTmkId()) == 0)) {
				if (this.hasRole(curUser, Role.STAFF_SALES)
						&& leadsManageService.isReachMaxLockedLeadsSizeForSales(curUser.getId())) {
					return new ResponseVo(RespStatus.FAIL, "您 lock LEADS个数已经达到上限");
				}
			leadsManageService.doLockLeads(leads);
		} else {
			return new ResponseVo(RespStatus.FAIL, " you have no right to release this leads");
		}
		
		return new ResponseVo(RespStatus.SUCCESS, "");
	}
	
	@RequestMapping(value="/unlockLeads",method = RequestMethod.POST)
	public ResponseVo doUnLockLeads(@RequestParam(value = "leadsId", required = true) Long leadsId) {
		Leads leads = leadsManageService.find(leadsId);
		if (leads == null) {
			return new ResponseVo(RespStatus.FAIL, " leads is not exist");
		}
		
		User curUser = securityService.getCurrentUser();
		if ((this.hasRole(curUser, Role.STAFF_SALES) && leads.getOwnerType() == Leads.OwnerType.STAFF_SALES.getCode()
					&& Long.compare(curUser.getId(), leads.getSalesId()) == 0)
				|| (this.hasRole(curUser, Role.STAFF_TMK) && leads.getOwnerType() == Leads.OwnerType.STAFF_TMK.getCode()
					&& Long.compare(curUser.getId(), leads.getTmkId()) == 0)) {
			leadsManageService.doUnLockLeads(leads);
		} else {
			return new ResponseVo(RespStatus.FAIL, " you have no right to release this leads");
		}
		
		return new ResponseVo(RespStatus.SUCCESS, "");
	}
	
	

	@RequestMapping(value="/findByStudentId",method = RequestMethod.GET)
	public Leads findByStudentId(@RequestParam(value = "studentId") Long studentId) {
		return leadsManageService.findByStudentId(studentId);
	}
	
	private boolean hasRole(User staff,Role role) {
		boolean flag = false;
		if (staff != null && role != null && StringUtils.isNotBlank(staff.getRoles())) {
			String[] roleArray = staff.getRoles().split("\\s+");
			List<String> roleList = Lists.newArrayList(roleArray);
			if (roleList.contains(role.name())) {
				flag = true;
			}
		}
		return flag;
	}
}
