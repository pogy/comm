package com.vipkid.repository;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.vipkid.model.Role;
import com.vipkid.model.Staff;
import com.vipkid.model.Staff_;
import com.vipkid.model.User.Status;
import com.vipkid.model.User_;
import com.vipkid.security.PasswordEncryptor;

@Repository
public class StaffRepository extends BaseRepository<Staff> {

	public StaffRepository() {
		super(Staff.class);
		
	}
	
	public List<Staff> findByRole(Role role) {
		String sql = "SELECT s FROM Staff s WHERE s.roles LIKE :roles AND s.status = :status";
		TypedQuery<Staff> typedQuery = entityManager.createQuery(sql, Staff.class);
		typedQuery.setParameter("roles", "%" + role.name() + "%");
		typedQuery.setParameter("status", Status.NORMAL);
		return typedQuery.getResultList();
	}
	
	public List<Staff> list(String search, String role, Status status, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Staff> criteriaQuery = criteriaBuilder.createQuery(Staff.class);
		Root<Staff> staff = criteriaQuery.from(Staff.class);

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(staff.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(staff.get(Staff_.englishName), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (role != null) {
			andPredicates.add(criteriaBuilder.like(staff.get(User_.roles), "%" + role + "%"));
		}
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(staff.get(User_.status), status));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

		// compose final predicate
		if(!orPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate);
		}
        if (!andPredicates.isEmpty()) {
            criteriaQuery.where(andPredicate);
        }
		
		criteriaQuery.orderBy(criteriaBuilder.desc(staff.get(User_.registerDateTime)));
		TypedQuery<Staff> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();
	}
	
	public long count(String search, String role, Status status) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Staff> staff = criteriaQuery.from(Staff.class);
		criteriaQuery.select(criteriaBuilder.count(staff));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(staff.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(staff.get(Staff_.englishName), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.and(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (role != null) {
			andPredicates.add(criteriaBuilder.like(staff.get(User_.roles), "%" + role + "%"));
		}
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(staff.get(User_.status), status));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
        if(!orPredicates.isEmpty()) {
            criteriaQuery.where(orPredicate);
        }
        if (!andPredicates.isEmpty()) {
            criteriaQuery.where(andPredicate);
        }

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}
	
	public Staff findByEmail(String email) {
		String sql = "SELECT s FROM Staff s WHERE s.email = :email";
		TypedQuery<Staff> typedQuery = entityManager.createQuery(sql, Staff.class);
		typedQuery.setParameter("email", email);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public Staff findByUsername(String username) {
		String sql = "SELECT s FROM Staff s WHERE s.username = :username";
		TypedQuery<Staff> typedQuery = entityManager.createQuery(sql, Staff.class);
		typedQuery.setParameter("username", username);
		
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}

	public Staff findByUsernameAndPassword(String username, String password) {
		String sql = "SELECT s FROM Staff s WHERE s.username = :username AND s.password = :password";
		TypedQuery<Staff> typedQuery = entityManager.createQuery(sql, Staff.class);
		typedQuery.setParameter("username", username);
		typedQuery.setParameter("password", PasswordEncryptor.encrypt(password));

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public Staff findByIdAndToken(long id, String token) {
		String sql = "SELECT s FROM Staff s WHERE s.id = :id AND s.token = :token";
		TypedQuery<Staff> typedQuery = entityManager.createQuery(sql, Staff.class);
		typedQuery.setParameter("id", id);
		typedQuery.setParameter("token", token);

		if (typedQuery.getResultList().isEmpty()) {
			return null;
		} else {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public List<Staff> findByName(String name) {
		String sql = "SELECT s FROM Staff s WHERE s.name LIKE :name";
		TypedQuery<Staff> typedQuery = entityManager.createQuery(sql, Staff.class);
		typedQuery.setParameter("name", "%" + name + "%");
		
		return typedQuery.getResultList();
	}
	
	public Staff findManagerBySalesTeamId(long salesTeamId) {
		String sql = "SELECT s FROM Staff s, SalesTeam st WHERE st.id =:salesTeamId AND st.managerId = s.id";
		TypedQuery<Staff> typedQuery = entityManager.createQuery(sql, Staff.class);
		typedQuery.setParameter("salesTeamId", salesTeamId);
		List<Staff> staffs = typedQuery.getResultList();
		if(staffs.isEmpty()) {
			return null;
		}else {
			return staffs.get(0);
		}
	}
	
	public List<Staff> findByTeamId(long salesTeamId) {
		String sql = "SELECT s FROM Staff s WHERE s.salesTeamId = :salesTeamId";
		TypedQuery<Staff> typedQuery = entityManager.createQuery(sql, Staff.class);
		typedQuery.setParameter("salesTeamId", salesTeamId);
		return typedQuery.getResultList();
	}
	
	public List<Staff> findSalesTeamMateByManagerId(long managerId) {
		String sql = "SELECT s FROM Staff s, SalesTeam st WHERE s.salesTeamId = st.id AND st.managerId = :managerId";
		TypedQuery<Staff> typedQuery = entityManager.createQuery(sql, Staff.class);
		typedQuery.setParameter("managerId", managerId);
		return typedQuery.getResultList();
	}
	
}
