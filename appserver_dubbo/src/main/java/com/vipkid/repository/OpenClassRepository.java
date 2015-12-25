package com.vipkid.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vipkid.model.Channel;
import com.vipkid.model.Course.Type;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.OnlineClass.Status;
import com.vipkid.model.OpenClassDesc;
import com.vipkid.model.Teacher.LifeCycle;
import com.vipkid.model.User.AccountType;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.OView;
import com.vipkid.service.pojo.OpenClassDescView;
import com.vipkid.service.pojo.TeacherView;
import com.vipkid.util.DateTimeUtils;

@Repository
public class OpenClassRepository extends BaseRepository<OpenClassDesc> {

	private Logger logger = LoggerFactory.getLogger(OpenClassRepository.class.getSimpleName());

	public OpenClassRepository() {
		super(OpenClassDesc.class);
	}
	
	
	public List<OpenClassDescView> list(String search, DateTimeParam startDate, DateTimeParam endDate, int start, int length) {
		logger.info("OpenClassRepository list");
		StringBuffer sql = new StringBuffer();
		sql.append(" select op.id,op.online_class_id,op.init_num,op.age_range,op.introduce,op.img_src,op.create_time,op.status,l.topic,l.serial_number,u.name,u.id,op.img_src_phone,o.scheduled_date_time,op.channel_id,op.openclass_type");
		sql.append(" from open_class_desc op join online_class o on op.online_class_id = o.id  join lesson l on o.lesson_id = l.id join user u on o.teacher_id = u.id");
		sql.append(" where  o.status != 'CANCELED' and o.status != 'EXPIRED' and o.status != 'REMOVED'");
		sql.append(" order by op.create_time desc");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setFirstResult(start);
		query.setMaxResults(length);
		List<Object> rows = query.getResultList();
		List<OpenClassDescView> openClassDescViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return openClassDescViews;
        }
		for (Object row : rows) {
			OpenClassDescView openDescView = new OpenClassDescView();
			Object[] cells = (Object[])row;
			openDescView.setId((long) (cells[0]));
			openDescView.setOnlineClassId((long) (cells[1]));
			openDescView.setInitNum(cells[2]==null?0:(int) (cells[2]));
			openDescView.setAgeRange((String)cells[3]);
			openDescView.setIntroduce((String)cells[4]);
			openDescView.setImgSrc((String)cells[5]);
			openDescView.setCreateTime(cells[6]==null?null:(Date)cells[6]);
			openDescView.setStatus(cells[7]==null?false:((int)cells[7]==1?true:false));
			openDescView.setLessonTopic((String)cells[8]);
			openDescView.setLessonSerialNumber((String)cells[9]);
			openDescView.setTeacherName((String)cells[10]);
			openDescView.setTeacherId((long)cells[11]);
			openDescView.setImgSrcPhone((String)cells[12]);
			if(cells[13]!=null){
				Date t = (Date)cells[13];
				openDescView.setScheduledDateTime(DateTimeUtils.format(t, DateTimeUtils.DATETIME_FORMAT2));
			}
			openDescView.setChannelId((String) (cells[14]));
			openDescView.setOpType(cells[15]==null?OpenClassDesc.OpenClassType.NORMAL:OpenClassDesc.OpenClassType.valueOf((String)cells[15]));
			openClassDescViews.add(openDescView);
		}
		return openClassDescViews;
	}

	public long countBySearch(String search, DateTimeParam startDate,
			DateTimeParam endDate) {
		logger.info("OpenClassRepository list");
		StringBuffer sql = new StringBuffer();
		sql.append(" select count(op.id)");
		sql.append(" from open_class_desc op join online_class o on op.online_class_id = o.id  join lesson l on o.lesson_id = l.id join user u on o.teacher_id = u.id");
		sql.append(" where o.status != 'CANCELED' and o.status != 'EXPIRED' and o.status != 'REMOVED'");
		Query query = entityManager.createNativeQuery(sql.toString());
		Long count = query.getSingleResult()==null?0l:(Long)query.getSingleResult();
		return count;
	}
	
	public  List<TeacherView> findTeacherByName(String teacherName){
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from (SELECT	t.id,t.real_name as name FROM  teacher t LEFT JOIN user u ON t.id = u.id");
		sql.append(" WHERE t.life_cycle='REGULAR' AND u.status='NORMAL' AND u.account_type='NORMAL'  GROUP BY t.id");
		sql.append(" ) t1 where  t1.name like ? and  EXISTS");
		sql.append("(select tc.teacher_id from teacher_certificated_course tc join course co on tc.course_id = co.id  where  t1.id=tc.teacher_id and (co.type = 'OPEN1' or co.type = 'OPEN2'))");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, "%"+teacherName+"%");
		List<Object> rows = query.getResultList();
		List<TeacherView> teacherViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return teacherViews;
        }
		for (Object row : rows) {
			TeacherView teachView = new TeacherView();
			Object[] cells = (Object[])row;
			teachView.setId((long) (cells[0]));
			teachView.setName((String)(cells[1]));
			teacherViews.add(teachView);
		}
		return teacherViews;
	}
	
	public List<OView> findOnlineClassSelect(String teacherName,
			DateTimeParam startDate,
			DateTimeParam endDate,
			Long teacherId,
			String serialNumber) {
		StringBuffer sql = new StringBuffer();
		sql.append("select NEW com.vipkid.service.pojo.OView(o.id,o.teacher.name,o.scheduledDateTime,o.lesson.serialNumber) from OnlineClass o ");
		sql.append(" where o.status =:status");
		sql.append(" and o.id not in (select op.onlineClassId from OpenClassDesc op)");
		sql.append(" and (o.lesson.learningCycle.unit.course.type =:type1 or o.lesson.learningCycle.unit.course.type =:type2 )");
		if(StringUtils.isNotBlank(teacherName)){
			sql.append(" and o.teacher.realName like :teacherName");
		}
		if(teacherId!=null&&teacherId!=0){
			sql.append(" and o.teacher.id = :teacherId");
		}
		if(startDate!=null){
			sql.append(" and o.scheduledDateTime>=:startDate");
		}
		if(endDate!=null){
			sql.append(" and o.scheduledDateTime<=:endDate");
		}
		if(StringUtils.isNotBlank(serialNumber)){
			sql.append(" and o.lesson.serialNumber like :serialNumber");
		}
		sql.append(" and o.teacher.lifeCycle = :lifeCycle");
		sql.append(" and o.teacher.status = :Status");
		sql.append(" and o.teacher.accountType = :AccountType");
		TypedQuery<OView> query = entityManager.createQuery(sql.toString(),OView.class);
		query.setParameter("status", Status.OPEN);
		query.setParameter("type1", Type.OPEN1);
		query.setParameter("type2", Type.OPEN2);
		query.setParameter("lifeCycle", LifeCycle.REGULAR);
		query.setParameter("Status", com.vipkid.model.User.Status.NORMAL);
		query.setParameter("AccountType", AccountType.NORMAL);
		if(StringUtils.isNotBlank(teacherName)){
			query.setParameter("teacherName", "%"+teacherName+"%");
		}
		if(teacherId!=null&&teacherId!=0){
			query.setParameter("teacherId", teacherId);
		}
		if(startDate!=null){
			query.setParameter("startDate", startDate.getValue());
		}
		if(endDate!=null){
			Date date = new Date(endDate.getValue().getTime()+24*60*60*1000-1);
			query.setParameter("endDate", date);
		}
		if(StringUtils.isNotBlank(serialNumber)){
			query.setParameter("serialNumber", "%"+serialNumber+"%");
		}
		return query.getResultList();
	}
	public OpenClassDesc findOpenClassDescByOnlineClassId(long id){
		String sql = "select o from OpenClassDesc o where o.onlineClassId = :id";
		TypedQuery<OpenClassDesc> query = entityManager.createQuery(sql,OpenClassDesc.class);
		query.setParameter("id", id);
		query.setMaxResults(1);
		List<OpenClassDesc> opDescs = query.getResultList();
		if(opDescs!=null&&!opDescs.isEmpty()){
			return opDescs.get(0);
		}
		return null;
	}
	
	public OpenClassDescView findById(long id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select op.id,op.online_class_id,op.init_num,op.age_range,op.introduce,op.img_src,op.create_time,op.status,l.topic,l.serial_number,u.name,u.id,op.img_src_phone,o.scheduled_date_time");
		sql.append(" from open_class_desc op join online_class o on op.online_class_id = o.id  join lesson l on o.lesson_id = l.id join user u on o.teacher_id = u.id");
		sql.append(" where  o.status != 'CANCELED' and o.status != 'EXPIRED' and o.status != 'REMOVED'");
		sql.append(" and op.id = ?");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, id);
		Object obj = query.getSingleResult();
        if (obj==null) {
            return new OpenClassDescView();
        }
		OpenClassDescView openDescView = new OpenClassDescView();
		Object[] cells = (Object[])obj;
		openDescView.setId((long) (cells[0]));
		openDescView.setOnlineClassId((long) (cells[1]));
		openDescView.setInitNum(cells[2]==null?0:(int) (cells[2]));
		openDescView.setAgeRange((String)cells[3]);
		openDescView.setIntroduce((String)cells[4]);
		openDescView.setImgSrc((String)cells[5]);
		openDescView.setCreateTime(cells[6]==null?null:(Date)cells[6]);
		openDescView.setStatus(cells[7]==null?false:((int)cells[7]==1?true:false));
		openDescView.setLessonTopic((String)cells[8]);
		openDescView.setLessonSerialNumber((String)cells[9]);
		openDescView.setTeacherName((String)cells[10]);
		openDescView.setTeacherId((long)cells[11]);
		openDescView.setImgSrcPhone((String)cells[12]);
		if(cells[13]!=null){
			Date t = (Date)cells[13];
			if(new Date().after(t)){
				openDescView.setHasOnClass(true);
			}
			openDescView.setScheduledDateTime(DateTimeUtils.format(t, DateTimeUtils.DATETIME_FORMAT2));
		}
		openDescView.setStudentCount(countOpenClassStudentById(openDescView.getOnlineClassId())+openDescView.getInitNum());
		return openDescView;
	}
	
	/**
	 * 
	* @Title: countOpenClassStudentById 
	* @Description: 判断摸个学生是否已经报名某节公开课
	* @param parameter
	* @author zhangfeipeng 
	* @return long
	* @throws
	 */
	public long countOpenClassStudentById(long onlineClassId,long studentId){
		String sql = "select count(os.student_id) from online_class_student os where os.online_class_id= ? and os.student_id= ?";
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, onlineClassId);
		query.setParameter(2, studentId);
		Long count = query.getSingleResult()==null?0l:(Long)query.getSingleResult();
		return count;
	}
	
	/**
	 * 
	* @Title: countOpenClassStudentById 
	* @Description: 统计某节公开课已经报名多少位学生
	* @param parameter
	* @author zhangfeipeng 
	* @return long
	* @throws
	 */
	public long countOpenClassStudentById(long onlineClassId){
		String sql = "select count(os.student_id) from online_class_student os where os.online_class_id= ?";
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, onlineClassId);
		Long count = query.getSingleResult()==null?0l:(Long)query.getSingleResult();
		return count;
	}
	
	public List<OpenClassDescView> listOpenClass(int ageRange,
			Integer rowNum,
			Integer currNum,
			String type){
		StringBuffer sql = new StringBuffer();
		sql.append(" select t.* from (");
		sql.append(" select t1.* from (");
		sql.append(" select op.id,op.online_class_id,op.init_num,op.age_range,op.introduce,op.img_src,op.create_time,op.status,l.topic,l.serial_number,u.name,u.id uid,op.img_src_phone,o.scheduled_date_time,op.channel_id,op.openclass_type");
		sql.append(" from open_class_desc op join online_class o on op.online_class_id = o.id  join lesson l on o.lesson_id = l.id join user u on o.teacher_id = u.id");
		sql.append(" where op.status=1 and o.status != 'CANCELED' and o.status != 'EXPIRED' and o.status != 'REMOVED'");
		if(ageRange!=-1){
			sql.append(" and op.age_range = ?");
		}
		if(!type.equals("ALL")){
			sql.append(" and op.openclass_type = ?");
		}
		sql.append(" and o.scheduled_date_time>= ?");
		sql.append(" order by o.scheduled_date_time asc");
		sql.append(" ) t1");
		sql.append(" union");
		sql.append(" select t2.* from (");
		sql.append(" select op.id,op.online_class_id,op.init_num,op.age_range,op.introduce,op.img_src,op.create_time,op.status,l.topic,l.serial_number,u.name,u.id uid,op.img_src_phone,o.scheduled_date_time,op.channel_id,op.openclass_type");
		sql.append(" from open_class_desc op join online_class o on op.online_class_id = o.id  join lesson l on o.lesson_id = l.id join user u on o.teacher_id = u.id");
		sql.append(" where op.status=1 and o.status != 'CANCELED' and o.status != 'EXPIRED' and o.status != 'REMOVED'");
		if(ageRange!=-1){
			sql.append(" and op.age_range = ?");
		}
		if(!type.equals("ALL")){
			sql.append(" and op.openclass_type = ?");
		}
		sql.append(" and o.scheduled_date_time< ?");
		sql.append(" order by o.scheduled_date_time desc");
		sql.append(" ) t2");
		sql.append(" ) t");
		
		Date dateTime = new Date(new Date().getTime() - 60*60*1000);
		
		Query query = entityManager.createNativeQuery(sql.toString());
		if(ageRange==1){
			if(!type.equals("ALL")){
				query.setParameter(1, "4-8岁");
				query.setParameter(2, type);
				query.setParameter(3, dateTime);
				query.setParameter(4, "4-8岁");
				query.setParameter(5, type);
				query.setParameter(6, dateTime);
			}else{
				query.setParameter(1, "4-8岁");
				query.setParameter(2, dateTime);
				query.setParameter(3, "4-8岁");
				query.setParameter(4, dateTime);
			}
		}else if(ageRange==2){
			if(!type.equals("ALL")){
				query.setParameter(1, "9-12岁");
				query.setParameter(2, type);
				query.setParameter(3, dateTime);
				query.setParameter(4, "9-12岁");
				query.setParameter(5, type);
				query.setParameter(6, dateTime);
			}else{
				query.setParameter(1, "9-12岁");
				query.setParameter(2, dateTime);
				query.setParameter(3, "9-12岁");
				query.setParameter(4, dateTime);
			}
		}else{
			if(!type.equals("ALL")){
				query.setParameter(1, type);
				query.setParameter(2, dateTime);
				query.setParameter(3, type);
				query.setParameter(4, dateTime);
			}else{
				query.setParameter(1, dateTime);
				query.setParameter(2, dateTime);
			}
		}
		query.setFirstResult((currNum-1)*rowNum);
		query.setMaxResults(rowNum);
		List<OpenClassDescView> rows = query.getResultList();
		List<OpenClassDescView> openClassDescViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return openClassDescViews;
        }
		for (Object row : rows) {
			OpenClassDescView openDescView = new OpenClassDescView();
			Object[] cells = (Object[])row;
			openDescView.setId((long) (cells[0]));
			openDescView.setOnlineClassId((long) (cells[1]));
			openDescView.setInitNum(cells[2]==null?0:(int) (cells[2]));
			openDescView.setAgeRange((String)cells[3]);
			openDescView.setIntroduce((String)cells[4]);
			openDescView.setImgSrc((String)cells[5]);
			openDescView.setCreateTime(cells[6]==null?null:(Date)cells[6]);
			openDescView.setStatus(cells[7]==null?false:((int)cells[7]==1?true:false));
			openDescView.setLessonTopic((String)cells[8]);
			openDescView.setLessonSerialNumber((String)cells[9]);
			openDescView.setTeacherName((String)cells[10]);
			openDescView.setTeacherId((long)cells[11]);
			openDescView.setImgSrcPhone((String)cells[12]);
			if(cells[13]!=null){
				Date t = (Date)cells[13];
				if(new Date().after(t)){
					openDescView.setHasOnClass(true);
				}
				openDescView.setScheduledDateTime(DateTimeUtils.format(t, DateTimeUtils.DATETIME_FORMAT2));
			}
			openDescView.setStudentCount(countOpenClassStudentById(openDescView.getOnlineClassId())+openDescView.getInitNum());
			openDescView.setChannelId((String) (cells[14]));
			openDescView.setOpType(cells[15]==null?OpenClassDesc.OpenClassType.NORMAL:OpenClassDesc.OpenClassType.valueOf((String)cells[15]));
			openClassDescViews.add(openDescView);
		}
		return openClassDescViews;
	}
	public long countOpenClass(int ageRange,
			String type){
		StringBuffer sql = new StringBuffer();
		sql.append(" select count(op.id)");
		sql.append(" from open_class_desc op join online_class o on op.online_class_id = o.id  join lesson l on o.lesson_id = l.id join user u on o.teacher_id = u.id");
		sql.append(" where op.status=1 and o.status != 'CANCELED' and o.status != 'EXPIRED' and o.status != 'REMOVED'");
		if(ageRange!=-1){
			sql.append(" and op.age_range = ?");
		}
		if(!type.equals("ALL")){
			sql.append(" and op.openclass_type = ?");
		}
		Query query = entityManager.createNativeQuery(sql.toString());
		if(ageRange==1){
			query.setParameter(1, "4-8岁");
			if(!type.equals("ALL")){
				query.setParameter(2, type);
			}
		}else if(ageRange==2){
			query.setParameter(1, "9-12岁");
			if(!type.equals("ALL")){
				query.setParameter(2, type);
			}
		}else{
			if(!type.equals("ALL")){
				query.setParameter(1, type);
			}
		}
		Long count = query.getSingleResult()==null?0l:(Long)query.getSingleResult();
		return count;
	}
	
	/**
	 * 
	* @Title: findOpenClassBeforeTwoHours 
	* @Description: 查询两小时将要开始的公开课，用于发送提示短信 
	* @param parameter
	* @author zhangfeipeng 
	* @return List<OnlineClass>
	* @throws
	 */
	public List<OnlineClass> findOpenClassBeforeTwoHours() {
		String sql = "SELECT o FROM OnlineClass o WHERE o.status = :status AND o.scheduledDateTime >= :startDate AND o.scheduledDateTime <= :endDate AND (o.lesson.learningCycle.unit.course.type = :type1 or o.lesson.learningCycle.unit.course.type = :type2) ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
	    Date startDate=DateTimeUtils.getNthMinutesLater(95);
	    Date endDate=DateTimeUtils.getNthMinutesLater(125);
	    typedQuery.setParameter("status", Status.OPEN);
	    typedQuery.setParameter("type1", Type.OPEN1);
	    typedQuery.setParameter("type2", Type.OPEN2);
	    typedQuery.setParameter("startDate", startDate);
	    typedQuery.setParameter("endDate", endDate);
		return typedQuery.getResultList();
	}
	
	public List<OpenClassDescView> listOpenClassForMobile(int ageRange){
		StringBuffer sql = new StringBuffer();
		sql.append(" select t.* from (");
		sql.append(" select t1.* from (");
		sql.append(" select op.id,op.online_class_id,op.init_num,op.age_range,op.introduce,op.img_src,op.create_time,op.status,l.topic,l.serial_number,u.name,u.id uid,op.img_src_phone,o.scheduled_date_time");
		sql.append(" from open_class_desc op join online_class o on op.online_class_id = o.id  join lesson l on o.lesson_id = l.id join user u on o.teacher_id = u.id");
		sql.append(" where op.status=1 and o.status != 'CANCELED' and o.status != 'EXPIRED' and o.status != 'REMOVED'");
		if(ageRange!=-1){
			sql.append(" and op.age_range = ?");
		}
		sql.append(" and o.scheduled_date_time>= ?");
		sql.append(" order by o.scheduled_date_time asc");
		sql.append(" ) t1");
		sql.append(" union");
		sql.append(" select t2.* from (");
		sql.append(" select op.id,op.online_class_id,op.init_num,op.age_range,op.introduce,op.img_src,op.create_time,op.status,l.topic,l.serial_number,u.name,u.id uid,op.img_src_phone,o.scheduled_date_time");
		sql.append(" from open_class_desc op join online_class o on op.online_class_id = o.id  join lesson l on o.lesson_id = l.id join user u on o.teacher_id = u.id");
		sql.append(" where op.status=1 and o.status != 'CANCELED' and o.status != 'EXPIRED' and o.status != 'REMOVED'");
		if(ageRange!=-1){
			sql.append(" and op.age_range = ?");
		}
		sql.append(" and o.scheduled_date_time< ?");
		sql.append(" order by o.scheduled_date_time desc");
		sql.append(" ) t2");
		sql.append(" ) t");
		
		Date dateTime = new Date(new Date().getTime() - 60*60*1000);
		
		Query query = entityManager.createNativeQuery(sql.toString());
		if(ageRange==1){
			query.setParameter(1, "4-8岁");
			query.setParameter(2, dateTime);
			query.setParameter(3, "4-8岁");
			query.setParameter(4, dateTime);
		}else if(ageRange==2){
			query.setParameter(1, "9-12岁");
			query.setParameter(2, dateTime);
			query.setParameter(3, "9-12岁");
			query.setParameter(4, dateTime);
		}else{
			query.setParameter(1, dateTime);
			query.setParameter(2, dateTime);
		}
		List<OpenClassDescView> rows = query.getResultList();
		List<OpenClassDescView> openClassDescViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return openClassDescViews;
        }
		for (Object row : rows) {
			OpenClassDescView openDescView = new OpenClassDescView();
			Object[] cells = (Object[])row;
			openDescView.setId((long) (cells[0]));
			openDescView.setOnlineClassId((long) (cells[1]));
			openDescView.setInitNum(cells[2]==null?0:(int) (cells[2]));
			openDescView.setAgeRange((String)cells[3]);
			openDescView.setIntroduce((String)cells[4]);
			openDescView.setImgSrc((String)cells[5]);
			openDescView.setCreateTime(cells[6]==null?null:(Date)cells[6]);
			openDescView.setStatus(cells[7]==null?false:((int)cells[7]==1?true:false));
			openDescView.setLessonTopic((String)cells[8]);
			openDescView.setLessonSerialNumber((String)cells[9]);
			openDescView.setTeacherName((String)cells[10]);
			openDescView.setTeacherId((long)cells[11]);
			openDescView.setImgSrcPhone((String)cells[12]);
			if(cells[13]!=null){
				Date t = (Date)cells[13];
				if(new Date().after(t)){
					openDescView.setHasOnClass(true);
				}
				openDescView.setScheduledDateTime(DateTimeUtils.format(t, DateTimeUtils.DATETIME_FORMAT2));
			}
			openDescView.setStudentCount(countOpenClassStudentById(openDescView.getOnlineClassId())+openDescView.getInitNum());
			openClassDescViews.add(openDescView);
		}
		return openClassDescViews;
	}
	
	public List<Channel> findChannelBySourceName(String sourceName){
		StringBuffer sql = new StringBuffer();
		sql.append(" select id , source_name from channel where hidden =1 and source_name like ? ");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, "%"+sourceName+"%");
		List<Object> rows = query.getResultList();
		List<Channel> channels = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return channels;
        }
		for (Object row : rows) {
			Channel channel = new Channel();
			Object[] cells = (Object[])row;
			channel.setId((long) (cells[0]));
			channel.setSourceName((String)(cells[1]));
			channels.add(channel);
		}
		return channels;
	}
	
	public long countByCourseTypeAndTime(long studentId,String type,Date begTime,Date endTime){
		StringBuffer sql = new StringBuffer();
		sql.append("select count(os.online_class_id) from ");
		sql.append("online_class_student os, ");
		sql.append("open_class_desc od, ");
		sql.append("online_class o, ");
		sql.append("lesson l, ");
		sql.append("unit u, ");
		sql.append("learning_cycle lc, ");
		sql.append("course c ");
		sql.append("where os.online_class_id = od.online_class_id ");
		sql.append(" and os.student_id = ? ");
		sql.append(" and od.online_class_id = o.id ");
		sql.append(" and o.lesson_id=l.id ");
		sql.append(" and l.learning_cycle_id = lc.id ");
		sql.append(" and lc.unit_id = u.id ");
		sql.append(" and u.course_id = c.id ");
		sql.append(" and c.type = ? ");
		sql.append(" and o.scheduled_date_time BETWEEN ? and ? ");
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, studentId);
		query.setParameter(2, type);
		query.setParameter(3, begTime);
		query.setParameter(4, endTime);
		Long count = query.getSingleResult()==null?0l:(Long)query.getSingleResult();
		return count;
	}
}
