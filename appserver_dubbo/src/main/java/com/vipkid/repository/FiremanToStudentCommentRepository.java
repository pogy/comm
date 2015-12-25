package com.vipkid.repository;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.FiremanToStudentComment;

@Repository
public class FiremanToStudentCommentRepository extends BaseRepository<FiremanToStudentComment> {

	public FiremanToStudentCommentRepository() {
		super(FiremanToStudentComment.class);
	}

	

	public long count(Boolean empty, Long courseId, Long teacherId, Long studentId) {
		return 0;	
	}

	public List<FiremanToStudentComment> findByStudentId(long studentId) {
		String sql = "SELECT fc FROM FiremanToStudentComment fc WHERE fc.student.id = :studentId";
		TypedQuery<FiremanToStudentComment> typedQuery = entityManager.createQuery(sql, FiremanToStudentComment.class);
		typedQuery.setParameter("studentId", studentId);
		
		return typedQuery.getResultList();
	}
	
	
	public FiremanToStudentComment findByOnlineClassIdAndStudentId(long onlineClassId,long studentId) {
		String sql = "SELECT fc FROM FiremanToStudentComment fc WHERE fc.onlineClass.id = :onlineClassId AND fc.student.id = :studentId";
		TypedQuery<FiremanToStudentComment> typedQuery = entityManager.createQuery(sql, FiremanToStudentComment.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("studentId", studentId);
		
		try {
			return typedQuery.getSingleResult();
			
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<FiremanToStudentComment> findByOnlineClassId(long onlineClassId) {
		String sql = "SELECT fc FROM FiremanToStudentComment fc WHERE fc.onlineClass.id = :onlineClassId";
		TypedQuery<FiremanToStudentComment> typedQuery = entityManager.createQuery(sql, FiremanToStudentComment.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		
		return typedQuery.getResultList();
	}

}
