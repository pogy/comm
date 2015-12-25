package com.vipkid.repository;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Course;
import com.vipkid.model.Course.Mode;
import com.vipkid.model.Course.Type;
import com.vipkid.model.Course_;
import com.vipkid.model.Product;
import com.vipkid.model.Product_;

@Repository
public class ProductRepository extends BaseRepository<Product> {

	private Logger logger = LoggerFactory.getLogger(ProductRepository.class);
	
	public ProductRepository() {
		super(Product.class);
	}
	
	public List<Product> list(String search, String mode, Integer start, Integer length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
		Root<Product> product = criteriaQuery.from(Product.class);
		Join<Product, Course> course = product.join(Product_.course, JoinType.LEFT);

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(product.get(Product_.name), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (mode != null && !mode.equals("")) {
			andPredicates.add(criteriaBuilder.equal(course.get(Course_.mode), Mode.valueOf(mode)));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		if(!orPredicates.isEmpty() && !andPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate, andPredicate);
		}else if(!orPredicates.isEmpty()){
			criteriaQuery.where(orPredicate);
		}else if(!andPredicates.isEmpty()){
			criteriaQuery.where(andPredicate);
		}
		
		criteriaQuery.orderBy(criteriaBuilder.desc(product.get(Product_.createDateTime)));
		TypedQuery<Product> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start==null?0:start);
		typedQuery.setMaxResults(length==null?0:length);
		return typedQuery.getResultList();
	}
	
	public long count(String search, String mode) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Product> product = criteriaQuery.from(Product.class);
		Join<Product, Course> course = product.join(Product_.course, JoinType.LEFT);
		criteriaQuery.select(criteriaBuilder.count(product));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(product.get(Product_.name), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		if (mode != null && !mode.equals("")) {
			andPredicates.add(criteriaBuilder.equal(course.get(Course_.mode), Mode.valueOf(mode)));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		if(!orPredicates.isEmpty() && !andPredicates.isEmpty()) {
			criteriaQuery.where(orPredicate, andPredicate);
		}else if(!orPredicates.isEmpty()){
			criteriaQuery.where(orPredicate);
		}else if(!andPredicates.isEmpty()){
			criteriaQuery.where(andPredicate);
		}

		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}

	public List<Product> findByCourseType(Type type) {
		String sql = "SELECT p FROM Product p WHERE p.course.type = :type";
		logger.debug("The sql = {}", sql);
		TypedQuery<Product> typedQuery = entityManager.createQuery(sql, Product.class);
		typedQuery.setParameter("type", type);
	    return typedQuery.getResultList();
	}
}
