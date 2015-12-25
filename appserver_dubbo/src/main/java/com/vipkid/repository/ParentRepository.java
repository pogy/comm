package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.Parent;
import com.vipkid.model.Student.LifeCycle;
import com.vipkid.model.User.Status;
import com.vipkid.model.Course.Type;
import com.vipkid.security.PasswordEncryptor;

@Repository
public class ParentRepository extends BaseRepository<Parent> {

	public ParentRepository() {
		super(Parent.class);
	}
	
	public List<Parent> list(String search, Status status, int start, int length) {
		return null;
	}
	
	public long count(String search, Status status) {
		return 0;
	}
	
	public List<Parent> findAll() {
		String sql = "SELECT p FROM Parent p";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
	    
	    return typedQuery.getResultList();
	}
	
	public List<Parent> findStudentsByLifeCycle(com.vipkid.model.Student.LifeCycle lifeCycle) {
		String sql = "SELECT DISTINCT p FROM Parent p JOIN p.family.students ss WHERE ss.lifeCycle = :lifeCycle";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("lifeCycle", lifeCycle);
	    
	    return typedQuery.getResultList();
	}
	
	public List<Parent> findStudentsByLifeCycleAndCourseTyeAndTotalClassHour(LifeCycle lifeCycle, Type type, int totalClassHour) {
		String sql = "SELECT DISTINCT p FROM Parent p JOIN p.family.students ss JOIN ss.learningProgresses ssls WHERE ss.lifeCycle = :lifeCycle AND ssls.course.type = :type AND ssls.totalClassHour >= :totalClassHour";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("lifeCycle", lifeCycle);
		typedQuery.setParameter("type", type);
		typedQuery.setParameter("totalClassHour", totalClassHour);
	    
	    return typedQuery.getResultList();
	}
	
	public Parent findByMobile(String mobile) {
		String sql = "SELECT p FROM Parent p WHERE p.mobile = :mobile";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("mobile", mobile);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public Parent findByUsername(String username) {
		String sql = "SELECT p FROM Parent p WHERE p.username = :username";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("username", username);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}

	public Parent findByUsernameAndPassword(String username, String password) {
		String sql = "SELECT p FROM Parent p WHERE p.username = :username AND p.password = :password";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("username", username);
		typedQuery.setParameter("password", PasswordEncryptor.encrypt(password));

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public Parent findByIdAndToken(long id, String token) {
		String sql = "SELECT p FROM Parent p WHERE p.id = :id AND p.token = :token";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("id", id);
		typedQuery.setParameter("token", token);

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public List<Parent> findByName(String name) {
		String sql = "SELECT p FROM Parent p WHERE p.name LIKE :name";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("name", "%" + name + "%");
		
		return typedQuery.getResultList();
	}
	
	public List<Parent> findByFamilyId(long familyId) {
		String sql = "SELECT p FROM Parent p WHERE p.family.id = :familyId";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("familyId", familyId);
		
		return typedQuery.getResultList();
	}
	
	public Parent findByWechatOpenId(String wechatOpenId) {
		String sql = "SELECT p FROM Parent p WHERE p.wechatOpenId = :wechatOpenId";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("wechatOpenId", wechatOpenId);

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}

	public Parent findRegisteredByMobile(String mobile) {
		String sql = "SELECT p FROM Parent p WHERE p.mobile = :mobile";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("mobile", mobile);

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public Parent findByMobileAndPassword(String username, String password) {
		String sql = "SELECT p FROM Parent p WHERE p.mobile = :mobile AND p.password = :password";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("mobile", username);
		typedQuery.setParameter("password", PasswordEncryptor.encrypt(password));

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	

	public Parent findReferredBy(long studentId){
		String sql = "SELECT p from Student s, Parent p where s.family.id = p.family.id and s.id = :studentId";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("studentId", studentId);
		List<Parent> parents = typedQuery.getResultList();
		if(parents.isEmpty()) {
			return null;
		}else {
			return parents.get(0);
		}
	}
	
	public List<Parent> findByrecommendCode(String recommendCode){
		String sql = "SELECT p FROM Parent p WHERE p.recommendCode = :recommendCode";
		TypedQuery<Parent> typedQuery = entityManager.createQuery(sql, Parent.class);
		typedQuery.setParameter("recommendCode", recommendCode);
		return typedQuery.getResultList();
	}
}
