package com.vipkid.repository;

import com.vipkid.model.Agent;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AgentRepository extends BaseRepository<Agent>{

	public AgentRepository(){
		super(Agent.class);
	}
	
	public List<Agent> list(String search,String lock, Integer start, Integer length) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a FROM Agent a  WHERE a.name LIKE :search");
		if(search==null){
			search="";
		}else{
			search=search.trim();
		}
		if(lock!=null){
			sql.append(" AND a.isLocked = :locked");
		}
		TypedQuery<Agent> typedQuery = entityManager.createQuery(sql.toString(), Agent.class);
		typedQuery.setParameter("search", "%" + search + "%");
		if(lock!=null){
			if(lock.equals("false"))
				typedQuery.setParameter("locked", false);
			if(lock.equals("true"))
				typedQuery.setParameter("locked", true);
		}
		if (start != null) {
			typedQuery.setFirstResult(start);
		}
		if (length != null) {
			typedQuery.setMaxResults(length);
		}
		
		return typedQuery.getResultList();
		
	}
	
	public long count(String search,String lock) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count(a.name) FROM Agent a  WHERE a.name LIKE :search");
		
		if(search==null){
			search="";
		}else{
			search=search.trim();
		}
		if(lock!=null){
			sql.append(" AND a.isLocked = :locked");
		}
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql.toString(),Long.class);
		typedQuery.setParameter("search", "%" + search + "%");
		if(lock!=null){
			if(lock.equals("false"))
				typedQuery.setParameter("locked", false);
			if(lock.equals("true"))
				typedQuery.setParameter("locked", true);
		}
		
		return typedQuery.getSingleResult();
	}
	
	/**
	 * 
	 * @param username(email is username)
	 * @return
	 */
	public Agent findByUsername(String username) {
		String sql = "SELECT a FROM Agent a WHERE a.email = :username";
		TypedQuery<Agent> typedQuery = entityManager.createQuery(sql, Agent.class);
		typedQuery.setParameter("username", username);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public Agent find(long id) {
		String sql = "SELECT a FROM Agent a WHERE a.id = :id";
		TypedQuery<Agent> typedQuery = entityManager.createQuery(sql, Agent.class);
		typedQuery.setParameter("id", id);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}

}
