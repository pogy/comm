package com.vipkid.repository;

import com.vipkid.model.Agent;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;

@Repository
public class AgentAuthRepository extends BaseRepository<Agent>{
	
	public Agent findByUsernameAndPassword(String username, String password) {
		String sql = "SELECT a FROM Agent a WHERE a.email = :username AND a.password = :password";
		TypedQuery<Agent> typedQuery = entityManager.createQuery(sql, Agent.class);
		typedQuery.setParameter("username", username);
		typedQuery.setParameter("password", password);

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public Agent findByIdAndToken(long id, String token) {
		String sql = "SELECT a FROM Agent a WHERE a.id = :id AND a.token = :token";
		TypedQuery<Agent> typedQuery = entityManager.createQuery(sql, Agent.class);
		typedQuery.setParameter("id", id);
		typedQuery.setParameter("token", token);

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}


}
