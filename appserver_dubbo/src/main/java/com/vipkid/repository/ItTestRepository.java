package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.ItTest;

@Repository
public class ItTestRepository extends BaseRepository<ItTest> {

	public ItTestRepository() {
		super(ItTest.class);
	}
	
	public List<ItTest> findByTeacherId(long teacherId) {
		String sql = "SELECT it FROM ItTest it WHERE it.teacher.id = :teacherId ORDER BY it.testDateTime DESC";
		TypedQuery<ItTest> typedQuery = entityManager.createQuery(sql, ItTest.class);
		typedQuery.setParameter("teacherId", teacherId);
		
		return typedQuery.getResultList();
	}
	
	public List<ItTest> findByFamilyId(long familyId) {
		String sql = "SELECT it FROM ItTest it WHERE it.family.id = :familyId ORDER BY it.testDateTime DESC";
		TypedQuery<ItTest> typedQuery = entityManager.createQuery(sql, ItTest.class);
		typedQuery.setParameter("familyId", familyId);
		
		return typedQuery.getResultList();
	}
	
	public List<ItTest> findByStudentId(long studentId) {
		String sql = "SELECT DISTINCT it FROM ItTest it JOIN it.family.students itfss WHERE itfss.id = :studentId ORDER BY it.testDateTime DESC";
		TypedQuery<ItTest> typedQuery = entityManager.createQuery(sql, ItTest.class);
		typedQuery.setParameter("studentId", studentId);
		
		return typedQuery.getResultList();
	}
	
}
