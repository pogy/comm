package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.OnlineClassOperation;

@Repository
public class OnlineClassOperationRepository extends BaseRepository<OnlineClassOperation> {

	OnlineClassOperationRepository() {
		super(OnlineClassOperation.class);
	}
	
	
	public List<OnlineClassOperation> findByStudentId(long studentId){
		String sql = "SELECT oco FROM OnlineClassOperation oco JOIN oco.students ocos WHERE ocos.id = :studentId";
		TypedQuery<OnlineClassOperation> typedQuery = entityManager.createQuery(sql, OnlineClassOperation.class);
		typedQuery.setParameter("studentId", studentId);
		
		return typedQuery.getResultList();
	}

}
