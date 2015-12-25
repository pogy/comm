package com.vipkid.repository;

import java.util.List;

import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.vipkid.service.pojo.parent.StatisticsRenewStudentView;

@Repository
public class StatisticsRenewStudentRepository extends BaseRepository<StatisticsRenewStudentView>{
	
	public List<StatisticsRenewStudentView>list(){
		StringBuffer sql = new StringBuffer();
		sql.append("select t.*,s.chinese_lead_teacher_id as cltTeacherId,u1.name as cltTeacherName from(");
		sql.append("select lp.student_id,u.name,lp.left_class_hour from learning_progress lp join user u on lp.student_id=u.id  WHERE course_id=5102 and u.status = 'NORMAL' and u.account_type='NORMAL' and lp.left_class_hour<=12 and lp.total_class_hour>=12 and student_id not in ( select student_id from statistics_renew_student )");
		sql.append(") t LEFT JOIN student s on t.student_id=s.id LEFT JOIN user u1 on s.chinese_lead_teacher_id = u1.id");
		Query query = entityManager.createNativeQuery(sql.toString());
		List<Object> rows = query.getResultList();
		List<StatisticsRenewStudentView> stViews = Lists.newArrayList();
        if (CollectionUtils.isEmpty(rows)) {
            return stViews;
        }
		for (Object row : rows) {
			StatisticsRenewStudentView stView = new StatisticsRenewStudentView();
			Object[] cells = (Object[])row;
			stView.setStudentId((long) (cells[0]));
			stView.setName((String)(cells[1]));
			stView.setLeftClassHour((Integer) (cells[2]));
			stView.setCltTeacherId((Long) (cells[3]));
			stView.setCltTeacherName((String) (cells[4]));
			stViews.add(stView);
		}
		return stViews;
	}
	
	
	public void insertStatisticsRenewStudent(){
		//首先清空statistics_renew_student
		String sql1="TRUNCATE table statistics_renew_student";
		Query query1 = entityManager.createNativeQuery(sql1);
		query1.executeUpdate();
		
		//将learningprogress 中所有符合条件的数据 全部插入到statistics_renew_student
		String sql2="insert into statistics_renew_student(student_id,name,left_class_hour) select lp.student_id,u.name,lp.left_class_hour from learning_progress lp join user u on lp.student_id=u.id  WHERE course_id=5102 and u.status = 'NORMAL' and u.account_type='NORMAL' and lp.left_class_hour<=12 and lp.total_class_hour>=12";
		Query query2 = entityManager.createNativeQuery(sql2);
		query2.executeUpdate();
	}

}
