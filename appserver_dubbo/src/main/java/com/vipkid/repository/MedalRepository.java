package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.MarketActivity;
import com.vipkid.model.Medal;

@Repository
public class MedalRepository extends BaseRepository<Medal> {
	
	public MedalRepository(){
		super(Medal.class);
	}
	
	/**
	 * 根据学生ID，获取勋章
	 * @param studentId
	 * @return
	 */
	public List<Medal> findByStudentId(long studentId){
		String sql = "SELECT m FROM Medal m WHERE m.student.id = :studentId";
		TypedQuery<Medal> typedQuery = entityManager.createQuery(sql, Medal.class);
		typedQuery.setParameter("studentId", studentId);
		
		return typedQuery.getResultList();
	}
	
	public List<Medal> findByStudentIdAndWellcome(long studentId,MarketActivity activity){
		String sql = "SELECT m FROM Medal m WHERE m.student.id = :studentId AND m.activity = :activity";
		TypedQuery<Medal> typedQuery = entityManager.createQuery(sql, Medal.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("activity", activity);
		if(typedQuery.getResultList()==null){
			return null;
		}
		else{
			return typedQuery.getResultList();
		}
	}
	
	public List<Medal> findByStudentIdAndUnitId(long studentId,long unitId){
		String sql = "SELECT m FROM Medal m WHERE m.student.id = :studentId AND m.unit.id = :unitId";
		TypedQuery<Medal> typedQuery = entityManager.createQuery(sql, Medal.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("unitId", unitId);
		if(typedQuery.getResultList()==null){
			return null;
		}
		else{
			return typedQuery.getResultList();
		}
	}
	
	public long count(long studentId){
		String sql = "SELECT m FROM Medal m WHERE m.student.id = :studentId";
		TypedQuery<Medal> typedQuery = entityManager.createQuery(sql, Medal.class);
		typedQuery.setParameter("studentId", studentId);
		List<Medal> medals = typedQuery.getResultList();
		return medals.size();
	}
}
