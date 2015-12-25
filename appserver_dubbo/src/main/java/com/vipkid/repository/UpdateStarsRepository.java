package com.vipkid.repository;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.Parent;


@Repository
public class UpdateStarsRepository extends BaseRepository<Parent>{
	
	public UpdateStarsRepository(){
		super(Parent.class);
	}
	
	public Parent findParentByMobile(String mobile){
		String sql = "select p from Parent p where p.mobile =:mobile";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql,Parent.class);
		typedQuery.setParameter("mobile", mobile);
		try {
			return typedQuery.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
	public void updateStarsByStudentId(long id,int num){
		String sql = "update student set stars = CASE when ISNULL(stars) THEN ? ELSE stars + ? END where  id = ?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, num);
		query.setParameter(2, num);
		query.setParameter(3, id);
		query.executeUpdate();
	}
	
	public void updateStarsAndOperatorIdByTeacherCommentId(long teacherCommentId,int num,long operatorId){
		String sql = "update teacher_comment set stars= ?,operator_id = ? where id = ?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, num);
		query.setParameter(2, operatorId);
		query.setParameter(3, teacherCommentId);
		query.executeUpdate();
	}

}
