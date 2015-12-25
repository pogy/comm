package com.vipkid.service;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.context.AppContext;
import com.vipkid.model.Channel;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.OpenClassDesc;
import com.vipkid.model.OpenClassDesc.OpenClassType;
import com.vipkid.model.Student;
import com.vipkid.model.User;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.OpenClassRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.security.CustomizedPrincipal;
import com.vipkid.service.exception.OpenClassHasBeenSignUpException;
import com.vipkid.service.exception.UserAlreadyExistServiceException;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.OView;
import com.vipkid.service.pojo.OpenClassDescView;
import com.vipkid.service.pojo.TeacherView;


@Service
public class OpenClassService {

	private Logger logger = LoggerFactory.getLogger(OpenClassService.class.getSimpleName());

	@Resource
	private OpenClassRepository openClassRepository;
	
	@Resource
	private StudentRepository studentRepository;
	
	@Resource
	private LearningProgressRepository learningProgressRepository;

	/**
	 * 创建
	 * @param openclass
	 * @return
	 */
	public OpenClassDesc create(OpenClassDesc openclass) {
		logger.info("create open-class");
		User user = null;
		OpenClassDesc op = openClassRepository.findOpenClassDescByOnlineClassId(openclass.getOnlineClassId());
		if(op == null) {
			Principal principal = AppContext.getPrincipal();
			if (null != principal) {
				user=  ((CustomizedPrincipal) principal).getUser();
			}
			openclass.setCreateTime(new Date());
			openclass.setStatus(false);//初始默认为下架状态
			openclass.setCreateId(user==null?0:user.getId());
			OpenClassDesc newOpenClass = openClassRepository.create(openclass);
			return newOpenClass;
		}else {
			throw new UserAlreadyExistServiceException("openClass already exist.");
		}
	}

	/**
	 * 
	* @Title: update 
	* @Description: TODO 
	* @param parameter
	* @author zhangfeipeng 
	* @return OpenClassDesc
	* @throws
	 */
	public OpenClassDesc update(OpenClassDesc openclass) {
		logger.info("update open-class");
		OpenClassDesc op = openClassRepository.findOpenClassDescByOnlineClassId(openclass.getOnlineClassId());
		op.setImgSrc(openclass.getImgSrc());
		op.setImgSrcPhone(openclass.getImgSrcPhone());
		op.setInitNum(openclass.getInitNum());
		op.setIntroduce(openclass.getIntroduce());
		op.setAgeRange(openclass.getAgeRange());
		OpenClassDesc newOpenClass = openClassRepository.update(op);
		return newOpenClass;
	}
	/**
	 * 按照条件获取列表
	 * @param search
	 * @param startDate
	 * @param endDate
	 * @param start
	 * @param length
	 * @return
	 */
	public List<OpenClassDescView> list(String search, DateTimeParam startDate, DateTimeParam endDate, int start, int length) {
		List<OpenClassDescView> openClassList = openClassRepository.list(search, startDate, endDate, start, length);
		return openClassList;
	}

	public Count countBySearch(String search, DateTimeParam startDate,
			DateTimeParam endDate) {
		long cnt = openClassRepository.countBySearch(search, startDate, endDate);
		Count count = new Count(cnt);
		return count;
	}
	
	/**
	 * 获取一个指定id的公开课
	 * @param id
	 * @return
	 */
	public OpenClassDescView findById(long id) {
		OpenClassDescView openClass = openClassRepository.findById(id);
		return openClass;
	}
	
	/**
	 * 
	* @Title: findTeacherByName 
	* @Description: 通过名称模糊查询出拥有教导open1公开课de老师
	* @param parameter
	* @author zhangfeipeng 
	* @return List<TeacherView>
	* @throws
	 */
	public  List<TeacherView> findTeacherByName(String teacherName) {
		return openClassRepository.findTeacherByName(teacherName);
	}
	
	public List<OView> findOnlineClassSelect(String teacherName,
			DateTimeParam startDate,
			DateTimeParam endDate,
			Long teacherId,
			String serialNumber) {
		return openClassRepository.findOnlineClassSelect(teacherName, startDate, endDate, teacherId, serialNumber);
	}
	/**
	 * 
	* @Title: changeStatus 
	* @Description:改变公开课上架 下架 状态
	* @param parameter
	* @author zhangfeipeng 
	* @return OpenClassDesc
	* @throws
	 */
	public OpenClassDesc changeStatus(long id,int status){
		OpenClassDesc openClass = openClassRepository.find(id);
		if(status==0){
			long cou = openClassRepository.countOpenClassStudentById(openClass.getOnlineClassId());
			if(cou>0){
				throw new OpenClassHasBeenSignUpException("OpenClassAlreadySignUpException[id: {}] is not exist.", openClass.getOnlineClassId());
			}
			openClass.setStatus(false);
		}else{
			openClass.setStatus(true);
		}
		return openClassRepository.update(openClass);
	}
	
	
	public List<OpenClassDescView> listOpenClass(int ageRange,long studentId,
			Integer rowNum,
			Integer currNum,
			String type){
		logger.info("listOpenClass ");
		List<OpenClassDescView>op = openClassRepository.listOpenClass(ageRange, rowNum, currNum,type);
		Student st = studentRepository.find(studentId);
		
		if(op!=null&&!op.isEmpty()){
			for(OpenClassDescView o :op){
				long cou = openClassRepository.countOpenClassStudentById(o.getOnlineClassId(), studentId);
				o.setSign(cou==0?false:true);
				setHasPower(st, o);
			}
		}
		return op;
	}

	private void setHasPower(Student st, OpenClassDescView o) {
		o.setHasPower(false);
		if(st!=null){
			int leftHour = 0;
			LearningProgress lp = learningProgressRepository.findByStudentIdAndOnlineClassId(st.getId(), o.getOnlineClassId());
			if(lp!=null) leftHour = lp.getLeftClassHour();
			if(leftHour >0 || o.isSign()){
				o.setHasPower(true);
			}else{
				if(o.getOpType()==OpenClassType.DEDICATED && st.getChannel()!=null){
					String[] cid = o.getChannelId().split(",");
					for(int i=0;i<cid.length;i++){
						String id= cid[i]==null?"-1":cid[i];
						if(Long.valueOf(id)==st.getChannel().getId()){
							o.setHasPower(true);
							break;
						}
					}
				}
			}
		}
	}
	
	public long countOpenClass(int ageRange,
			String type){
		logger.info("countOpenClass ");
		return openClassRepository.countOpenClass(ageRange,type);
	}
	
	public List<OpenClassDescView> listOpenClassForMobile(int ageRange){
		logger.info("listOpenClassForMobile ");
		List<OpenClassDescView>op = openClassRepository.listOpenClassForMobile(ageRange);
		return op;
	}
	
	/**
	 * 
	* @Title: findChannelBySourceName 
	* @Description: 通过sourceName 模糊查询channel 
	* @param parameter
	* @author zhangfeipeng 
	* @return List<Channel>
	* @throws
	 */
	public List<Channel> findChannelBySourceName(String sourceName){
		return openClassRepository.findChannelBySourceName(sourceName);
	}
	
	public long countByCourseTypeAndTime(long studentId,String type,long time){
		Date begTime = new Date(new Date().getTime() - time);
		return openClassRepository.countByCourseTypeAndTime(studentId,type, begTime, new Date());
	}
	
}
