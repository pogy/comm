package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.EducationalComment;

@Repository
public class EducationalCommentRepository extends BaseRepository<EducationalComment> {

	public EducationalCommentRepository() {
		super(EducationalComment.class);
	}
	
	public List<EducationalComment> findRecentByStudentIdAndClassIdAndAmount(long studentId, Long onlineClassId, long amount) {

		String sql = "SELECT tc FROM EducationalComment tc JOIN tc.onlineClass toc JOIN toc.students tocss WHERE tc.onlineClass.id = :onlineClassId AND tocss.id = :studentId ORDER BY toc.scheduledDateTime DESC";
		TypedQuery<EducationalComment> typedQuery = entityManager.createQuery(sql, EducationalComment.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setFirstResult(0);
		typedQuery.setMaxResults((int)amount);
		
		return typedQuery.getResultList();
	}

	public List<EducationalComment> findRecentByStudentIdAndAmount(
			long studentId, long amount) {

		String sql = "SELECT tc FROM EducationalComment tc JOIN tc.onlineClass toc JOIN toc.students tocss WHERE tocss.id = :studentId ORDER BY toc.scheduledDateTime DESC";
		TypedQuery<EducationalComment> typedQuery = entityManager.createQuery(sql, EducationalComment.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setFirstResult(0);
		typedQuery.setMaxResults((int)amount);
		
		return typedQuery.getResultList();
	
	}
	
	public List<EducationalComment> findByStudentId(long studentId) {

		String sql = "SELECT tc FROM EducationalComment tc WHERE tc.student.id = :studentId";
		TypedQuery<EducationalComment> typedQuery = entityManager.createQuery(sql, EducationalComment.class);
		typedQuery.setParameter("studentId", studentId);
		
		return typedQuery.getResultList();
	}
	
	public List<EducationalComment> findByOnlineClassId(long onlineClassId) {

		String sql = "SELECT tc FROM EducationalComment tc WHERE tc.onlineClass.id = :onlineClassId";
		TypedQuery<EducationalComment> typedQuery = entityManager.createQuery(sql, EducationalComment.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		
		return typedQuery.getResultList();
	}
	
	public EducationalComment findByOnlineClassIdAndStudentId(long onlineClassId, long studentId) {
		String sql = "SELECT a FROM EducationalComment a WHERE a.onlineClass.id = :onlineClassId AND a.student.id = :studentId";
		TypedQuery<EducationalComment> typedQuery = entityManager.createQuery(sql, EducationalComment.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		typedQuery.setParameter("studentId", studentId);
		
		if(!typedQuery.getResultList().isEmpty()){
			return typedQuery.getResultList().get(0);
		} else {
			return null;
		}
	}


}
