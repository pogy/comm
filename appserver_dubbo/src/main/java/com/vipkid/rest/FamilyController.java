package com.vipkid.rest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Family;
import com.vipkid.model.Parent;
import com.vipkid.model.Student;
import com.vipkid.rest.vo.Response;
import com.vipkid.rest.vo.query.FamilyQueryFamilyView;
import com.vipkid.rest.vo.query.FamilyQueryParentView;
import com.vipkid.rest.vo.query.FamilyQueryStudentView;
import com.vipkid.security.SecurityService;
import com.vipkid.service.FamilyService;
import com.vipkid.service.pojo.Count;

@RestController
@RequestMapping(value="/api/service/private/families")
public class FamilyController {
	private Logger logger = LoggerFactory.getLogger(FamilyController.class.getSimpleName());
	
	
	@Resource
	private SecurityService securityService;
	
	@Resource
	private FamilyService familyService;
	
	@RequestMapping(value="/find",method = RequestMethod.GET)
	public Family find(@RequestParam("id") long id) {
		logger.info("find family for id = {}", id);
		return familyService.find(id);
	}
	
	@RequestMapping(value="/list",method = RequestMethod.GET)
	public List<Family> list(@RequestParam(value="search",required=false) String search, @RequestParam(value="province",required=false) String province, @RequestParam(value="city",required=false) String city, @RequestParam("start") int start, @RequestParam("length") int length) {
		logger.info("list family with params: search = {}, province = {}, city = {}, start = {}, length = {}.", search, province, city, start, length);
		return familyService.list(search, province, city, start, length);
	}

	@RequestMapping(value="/count",method = RequestMethod.GET)
	public Count count(@RequestParam(value="search",required=false) String search, @RequestParam(value="province",required=false) String province, @RequestParam(value="city",required=false) String city) {
		logger.info("count family with params: search = {}, province = {}, city = {}, start = {}, length = {}.", search, province, city);
		return familyService.count(search, province, city);
	}
	
	@RequestMapping(value="/findByStudentId",method = RequestMethod.GET)
	public Family findByStudentId(@RequestParam("studentId") long studentId) {
		logger.info("find family for student id = {}", studentId);
		return familyService.findByStudentId(studentId);
	}
	
	@RequestMapping(value="/getInvitation",method = RequestMethod.GET)
	public Family getInvitation(@RequestParam("familyId") long familyId, @RequestParam("cost") int cost) {
		logger.info("getInvitation for familyId = {},cost={}", familyId,cost);
		return familyService.getInvitation(familyId, cost);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public Family create(@RequestBody Family family) {
		logger.info("create family: {}", family);
		return familyService.create(family);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public Family update(@RequestBody Family family) {
		logger.info("update family: {}", family);
		return familyService.update(family);
	}
	
	@RequestMapping(value="/delete", method = RequestMethod.DELETE)
	public Response delete(@RequestParam("familyId") long familyId) {
		logger.info("delete family with familyId: {}", familyId);
		return familyService.delete(familyId);
	}
	
	@RequestMapping(value="/filter",method = RequestMethod.GET)
	public List<FamilyQueryFamilyView> filter(@RequestParam(value="search",required=false) String search, @RequestParam(value="province",required=false) String province, @RequestParam(value="city",required=false) String city, @RequestParam("start") int start, @RequestParam("length") int length) {
		logger.info("list family with params: search = {}, province = {}, city = {}, start = {}, length = {}.", search, province, city, start, length);
		List<Family> familyList =  familyService.list(search, province, city, start, length);
		
		return this.getFamilyQueryResultView(familyList);
	}
	
	private List<FamilyQueryFamilyView> getFamilyQueryResultView(List<Family> familyList) {
		List<FamilyQueryFamilyView> familyViewList = new ArrayList<FamilyQueryFamilyView>();
		if (familyList != null && familyList.size() > 0) {
			Iterator<Family> iterator = familyList.iterator();
			while (iterator.hasNext()) {
				Family family = iterator.next();
				FamilyQueryFamilyView familyView = new FamilyQueryFamilyView();
				
				//parents
				List<FamilyQueryParentView> parentViewList = new ArrayList<FamilyQueryParentView>();
				if (family.getParents() != null && family.getParents().size() > 0) {
					for (Parent parent : family.getParents()) {
						FamilyQueryParentView parentView = new FamilyQueryParentView();
						parentView.setId(parent.getId());
						parentView.setName(parent.getName());
						parentView.setMobile(parent.getMobile());
						parentView.setRelation(parent.getRelation());
						parentViewList.add(parentView);
					}
				}
				
				//student
				List<FamilyQueryStudentView> studentViewList = new ArrayList<FamilyQueryStudentView>(); 
				if (family.getStudents() != null && family.getStudents().size() > 0) {
					for (Student student : family.getStudents()) {
						FamilyQueryStudentView studentView = new FamilyQueryStudentView();
						studentView.setId(student.getId());
						studentView.setName(student.getName());
						studentViewList.add(studentView);
					}
					
				}
				
				familyView.setId(family.getId());
				familyView.setName(family.getName());
				familyView.setPhone(family.getPhone());
				familyView.setProvince(family.getProvince());
				familyView.setCity(family.getCity());
				familyView.setDistrict(family.getDistrict());
				familyView.setAddress(family.getAddress());
				familyView.setParents(parentViewList);
				familyView.setStudents(studentViewList);
				
				familyViewList.add(familyView);
			}
			
		}
		
		return familyViewList;
		
	}
}
