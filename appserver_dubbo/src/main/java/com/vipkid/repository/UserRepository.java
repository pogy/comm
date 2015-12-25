package com.vipkid.repository;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.User;


@Repository
public class UserRepository extends BaseRepository<User> {
	
	public UserRepository(){
		super(User.class);
	}

	public User findByUsername(String username) {
		String sql = "SELECT t FROM User t WHERE t.username = :username";
		TypedQuery<User> typedQuery = entityManager.createQuery(sql, User.class);
		typedQuery.setParameter("username", username);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
}
