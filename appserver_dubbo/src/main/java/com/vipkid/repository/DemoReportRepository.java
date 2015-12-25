package com.vipkid.repository;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.DemoReport;
import com.vipkid.model.DemoReport.LifeCycle;

@Repository
public class DemoReportRepository extends BaseRepository<DemoReport> {

	public DemoReportRepository() {
		super(DemoReport.class);
	}

	public DemoReport findByOnlineClassId(long onlineClassId) {
		String sql = "SELECT d FROM DemoReport d WHERE d.onlineClass.id = :onlineClassId";
		TypedQuery<DemoReport> typedQuery = entityManager.createQuery(sql, DemoReport.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		
		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public DemoReport findByStudentId(long studentId) {
		String sql = "SELECT d FROM DemoReport d WHERE d.student.id = :studentId AND d.lifeCycle = :lifeCycle ORDER BY d.confirmDateTime DESC";
		TypedQuery<DemoReport> typedQuery = entityManager.createQuery(sql, DemoReport.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("lifeCycle", LifeCycle.CONFIRMED);
		
		List<DemoReport> demoReports = typedQuery.getResultList();
		if(demoReports.isEmpty()) {
			return null;
		}else {
			return demoReports.get(0);
		}
	}

}
