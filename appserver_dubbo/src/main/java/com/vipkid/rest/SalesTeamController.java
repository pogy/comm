package com.vipkid.rest;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.SalesTeam;
import com.vipkid.model.SalesTeam.Type;
import com.vipkid.rest.vo.query.SalesTeamVO;
import com.vipkid.rest.vo.query.SalesVO;
import com.vipkid.service.SalesTeamService;
import com.vipkid.service.pojo.BooleanWrapper;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping("/api/service/private/salesTeams")
public class SalesTeamController {
//	private Logger logger = LoggerFactory.getLogger(SalesTeamController.class.getSimpleName());

	@Resource
	private SalesTeamService salesTeamService;

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public SalesTeam find(@RequestParam("id") long id) {
		return salesTeamService.find(id);
	}
	
	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public List<SalesTeam> find() {
		return salesTeamService.findAll();
	}
	
	@RequestMapping(value = "/findByType", method = RequestMethod.GET)
	public List<SalesTeamVO> findByType(@RequestParam("type") Type type) {
		return salesTeamService.findByType(type);
	}

	@RequestMapping(value = "/createSalesTeam", method = RequestMethod.POST)
	public SalesTeam createSalesTeam(@RequestBody SalesTeam salesTeam) {
		SalesTeam returnSalesTeam = salesTeamService.createSalesTeam(salesTeam);
		salesTeamService.doAddMangerToTeam(returnSalesTeam);
		return returnSalesTeam;
	}
	
	@RequestMapping(value = "/createTMKTeam", method = RequestMethod.POST)
	public SalesTeam createTMKTeam(@RequestBody SalesTeam TMKTeam) {
		SalesTeam returnTMKTeam = salesTeamService.createTMKTeam(TMKTeam);
		salesTeamService.doAddMangerToTeam(returnTMKTeam);
		return returnTMKTeam;
	}
	
	@RequestMapping(value = "/listForSalesTeam", method = RequestMethod.GET,produces = "application/json")
	public List<SalesVO> listForSalesTeam(
			@RequestParam(value = "role",required = false) String role,
			@RequestParam(value = "salesTeamId",required = false) Long salesTeamId,
			@RequestParam(value = "managerId",required = false) Long managerId,
			@RequestParam(value = "autoAssignLeads",required = false) Boolean autoAssignLeads,
			@RequestParam(value = "searchText",required = false) String searchText,
			@RequestParam(value = "isInTeam",required = false) Boolean isInTeam,
			@RequestParam(value = "start",required = false) Integer start,
			@RequestParam(value = "length",required = false) Integer length) {
		return salesTeamService.listForSalesTeam(role, salesTeamId, managerId, autoAssignLeads, searchText,isInTeam, start, length);
	}

	@RequestMapping(value = "/countForSalesTeam", method = RequestMethod.GET,produces = "application/json")
	public Count countForSalesTeam(
			@RequestParam(value = "role",required = false) String role,
			@RequestParam(value = "salesTeamId",required = false) Long salesTeamId,
			@RequestParam(value = "managerId",required = false) Long managerId,
			@RequestParam(value = "autoAssignLeads",required = false) Boolean autoAssignLeads,
			@RequestParam(value = "searchText",required = false) String searchText,
			@RequestParam(value = "isInTeam",required = false) Boolean isInTeam){
		return salesTeamService.countForSalesTeam(role, salesTeamId, managerId, autoAssignLeads, searchText,isInTeam);
	}
	
	@RequestMapping(value = "/assignToSalesTeam", method = RequestMethod.PUT)
	public void doAssignToSalesTeam(@RequestBody List<SalesVO> salesVOs) {
		salesTeamService.doAssignToSalesTeam(salesVOs);
	}
	
	@RequestMapping(value = "/assignToTMKTeam", method = RequestMethod.PUT)
	public void doAssignToTMKTeam(@RequestBody List<SalesVO> salesVOs) {
		salesTeamService.doAssignToTMKTeam(salesVOs);
	}
	
	@RequestMapping(value = "/findIfHasQueueNodeByUserId", method = RequestMethod.GET ,produces = "application/json")
	public BooleanWrapper findIfHasQueueNodeByUserId(@RequestParam(value = "userId") Long userId) {
		return salesTeamService.findIfHasQueueNodeByUserId(userId);
	}
}
