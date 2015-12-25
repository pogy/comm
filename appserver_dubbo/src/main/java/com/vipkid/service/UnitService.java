package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.model.Audit.Category;
import com.vipkid.model.Course.Type;
import com.vipkid.model.Level;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Unit;
import com.vipkid.repository.UnitRepository;
import com.vipkid.security.SecurityService;

@Service
public class UnitService {
private Logger logger = LoggerFactory.getLogger(UnitService.class.getSimpleName());
	
	@Resource
	private UnitRepository unitRepository;
	@Resource
	private SecurityService securityService;
	
	public Unit find(long id) {
		logger.debug("find unit for id = {}", id);
		Unit unit = unitRepository.find(id);
		return unit;
	}
	
	
	public List<Unit> findByCourseId( long courseId) {
		logger.debug("find units for courseId = {}", courseId);
		return unitRepository.findByCourseId(courseId);
	}
	
	
	public List<Unit> findByLevelId( long levelId) {
		logger.debug("find units for levelId = {}", levelId);
		return unitRepository.findByLevelId(levelId);
	}
	
	
	public List<Unit> findByCourseType( Type type) {
		logger.debug("find units for type = {}", type);
		return unitRepository.findByCourseType(type);
	}
	
	
	public List<Unit> findByCourseIdAndLevel( long courseId,  Level level) {
		logger.debug("find units for courseId = {} level = {} ", courseId, level);
		return unitRepository.findByCourseIdAndLevel(courseId, level);
	}
	
	
	public List<Unit> findStartedUnits( long courseId){
		return unitRepository.findUnitsByStatus(OnlineClass.Status.FINISHED.toString(), courseId);
	}
	
	
	public List<Unit> findStartingUnits( long courseId){
		return unitRepository.findUnitsByStatus(OnlineClass.Status.BOOKED.toString(), courseId);
	}
	
	
	public List<Unit> findBySequenceRangeAndCourseId( int firstSequence,  int lastSequence,  long courseId){
		return unitRepository.findBySequenceRangeAndCourseId(firstSequence, lastSequence, courseId);
	}
	
	
	public List<Unit> findByFirstSequenceAndCourseId( int firstSequence,  long courseId){
		return unitRepository.findByFirstSequenceAndCourseId(firstSequence, courseId);
	}
	
	
	public Unit update(Unit unit) {
		logger.debug("update unit: {}", unit);
		unitRepository.update(unit);
		StringBuffer strbuf = new StringBuffer(unit.getSerialNumber());//课程的序列号不会更改，所以以他为基准。
		securityService.logAudit(com.vipkid.model.Audit.Level.INFO, Category.UNIT_BASIC_INFO_UPDATE, "Update: The "+strbuf.toString()+" Unit has been updated！" );
		return unit;
	}
	
//	@GET
//	@Path("/findUnstartUnits")
//	public List<OnlineClass> getUnstartUnits(long courseId){
//		return unitAccessor.getUnstartUnits(courseId);
//	}
	
	public List<Unit> findUnitsByStatus(String status, long courseId) {
		return unitRepository.findUnitsByStatus(status, courseId);
	}
}
