package com.vipkid.repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.FiremanToTeacherComment;

@Repository
public class FiremanToTeacherCommentRepository extends BaseRepository<FiremanToTeacherComment> {

	public FiremanToTeacherCommentRepository() {
		super(FiremanToTeacherComment.class);
	}

	public FiremanToTeacherComment findByOnlineClassIdAndTeacherId(long onlineClassId,
			long teacherId) {
		String sql = "SELECT t FROM FiremanToTeacherComment t WHERE t.onlineClass.id = :onlineClassId AND t.teacher.id = :teacherId";
		TypedQuery<FiremanToTeacherComment> typedQuery = entityManager.createQuery(sql, FiremanToTeacherComment.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("teacherId", teacherId);
		
		
		try {
			return typedQuery.getSingleResult();
			
		} catch (NoResultException e) {
			return null;
		}
	}

	public long count(Boolean empty, Long courseId, Long teacherId, Long studentId) {
		return 0;	
	}

	public FiremanToTeacherComment findByOnlineClassId(long onlineClassId) {
		String sql = "SELECT fc FROM FiremanToTeacherComment fc WHERE fc.onlineClass.id = :onlineClassId";
		TypedQuery<FiremanToTeacherComment> typedQuery = entityManager.createQuery(sql, FiremanToTeacherComment.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		
		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
