package com.vipkid.service;

import java.util.List;

import javax.annotation.Resource;

import com.vipkid.rest.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vipkid.ext.email.EMail;
import com.vipkid.model.Family;
import com.vipkid.model.ItTest;
import com.vipkid.model.ItTest.FinalResult;
import com.vipkid.model.ItTest.Result;
import com.vipkid.model.ItTestJSON;
import com.vipkid.model.ItTestRole;
import com.vipkid.model.Student;
import com.vipkid.model.Teacher;
import com.vipkid.model.json.gson.GsonManager;
import com.vipkid.repository.FamilyRepository;
import com.vipkid.repository.ItTestRepository;
import com.vipkid.repository.TeacherRepository;
import com.vipkid.security.SecurityService;

@Service
public class DBYtestService {
	private Logger logger = LoggerFactory.getLogger(DBYtestService.class.getSimpleName());
	
	@Resource
	private FamilyRepository familyRepository;
	
	@Resource
	private ItTestRepository itTestRepository;
	
	@Resource
	private TeacherRepository teacherRepository;

	public Response doReceive(String testResult, String lang, ItTestRole itTestRole, String id) {
		logger.info("find testResult = {}, lang = {}, role = {}, id = {}", testResult, lang, itTestRole, id);
		if(itTestRole != null && id != null) {
			ItTestJSON itTestJSON = GsonManager.getInstance().getGson().fromJson(testResult, ItTestJSON.class);
			long idForFind = Long.valueOf(id);
			ItTest itTest = new ItTest();
			
			tranformItTestJSONtoItTest(itTestJSON, itTest);				
			setTestedUser(itTestRole, idForFind, itTest);
			resetCurrent(itTestRole, idForFind, itTest);
			setFinalResult(itTest);
				
			itTestRepository.create(itTest);
			
			sendEmailToRelatedPerson(itTest, itTestRole);
		}		
		
		return new Response(HttpStatus.OK.value());
	}
	
	private void tranformItTestJSONtoItTest(ItTestJSON itTestJSON, ItTest itTest) {
		itTest.setSystem(itTestJSON.getSystem().getValue());
		itTest.setSystemResult(tranformResturnCodeToResult(itTestJSON.getSystem().getReturnCode()));
		
		itTest.setBrowser(itTestJSON.getBrowser().getValue());
		itTest.setBrowserResult(tranformResturnCodeToResult(itTestJSON.getBrowser().getReturnCode()));
		
		itTest.setFlash(itTestJSON.getFlash().getValue());
		itTest.setFlashResult(tranformResturnCodeToResult(itTestJSON.getFlash().getReturnCode()));
		
		itTest.setConnect(itTestJSON.getConnect().getValue());
		itTest.setConnectResult(tranformResturnCodeToResult(itTestJSON.getConnect().getReturnCode()));
		
		itTest.setDelay(itTestJSON.getDelay().getValue());
		itTest.setDelayResult(tranformResturnCodeToResult(itTestJSON.getDelay().getReturnCode()));
		
		itTest.setBandWidth(itTestJSON.getBandWidth().getValue());
		itTest.setBandWidthResult(tranformResturnCodeToResult(itTestJSON.getBandWidth().getReturnCode()));
		
		itTest.setSound(itTestJSON.getSound().getValue());
		itTest.setSoundResult(tranformResturnCodeToResult(itTestJSON.getSound().getReturnCode()));
		
		itTest.setMic(itTestJSON.getMic().getValue());
		itTest.setMicResult(tranformResturnCodeToResult(itTestJSON.getMic().getReturnCode()));
		
		itTest.setCamera(itTestJSON.getCamera().getValue());
		itTest.setCameraResult(tranformResturnCodeToResult(itTestJSON.getCamera().getReturnCode()));
	}
	
	private Result tranformResturnCodeToResult(int returnCode) {
		if(returnCode == 1) {
			return Result.NORMAL;
		}else if(returnCode == 2) {
			return Result.ABNORMAL;
		}
		return null;
	}
	
	private void setTestedUser(ItTestRole itTestRole, long userId, ItTest itTest) {
		switch(itTestRole) {
		case FAMILY:				
			Family family = familyRepository.find(userId);
			family.setHasTested(true);
			itTest.setFamily(family);
			break;
		case TEACHER:
			Teacher teacher = teacherRepository.find(userId);
			teacher.setHasTested(true);
			itTest.setTeacher(teacher);
			break;
		}
	}
	
	private void resetCurrent(ItTestRole itTestRole, long userId, ItTest currentItTest) {
		switch(itTestRole) {
		case FAMILY:
			List<ItTest> familyItTests = itTestRepository.findByFamilyId(userId);
			for(ItTest familyItTest : familyItTests) {
				familyItTest.setCurrent(false);
			}
			break;
		case TEACHER:
			List<ItTest> teacherItTests = itTestRepository.findByFamilyId(userId);
			for(ItTest teacherItTest : teacherItTests) {
				teacherItTest.setCurrent(false);
			}
			break;
		}
		currentItTest.setCurrent(true);
	}
	
	private void setFinalResult(ItTest itTest) {
		if(itTest.getSystemResult() == Result.NORMAL &&
		   itTest.getBrowserResult() == Result.NORMAL &&	
		   itTest.getFlashResult() == Result.NORMAL &&
		   itTest.getConnectResult() == Result.NORMAL &&
		   itTest.getDelayResult() == Result.NORMAL &&
		   itTest.getBandWidthResult() == Result.NORMAL &&
		   itTest.getSoundResult() == Result.NORMAL &&
		   itTest.getMicResult() == Result.NORMAL &&
		   itTest.getCameraResult() == Result.NORMAL) {
			itTest.setFinalResult(FinalResult.NORMAL);
		}else {
			itTest.setFinalResult(FinalResult.ABNORMAL);
		}
	}
	
	private void sendEmailToRelatedPerson(ItTest itTest, ItTestRole itTestRole) {
		try {
			if(itTestRole == ItTestRole.FAMILY) {
				Family family = familyRepository.find(itTest.getFamily().getId());
				switch(itTest.getFinalResult()) {
				case NORMAL:	
					for(Student student : family.getStudents()) {
						EMail.sendToRelatedPersonAfterFamilyItTestNormalEmail(itTest, student);
					}					
					break;
				case ABNORMAL:
					for(Student student : family.getStudents()) {
						EMail.sendToRelatedPersonAfterFamilyItTestAbnormalEmail(itTest, student);
					}
					break;
				default:
					break;
				}
			}else if(itTestRole == ItTestRole.TEACHER) {
				EMail.sendToRelatedPersonAfterTeacherItTestEmail(itTest, itTest.getTeacher());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
