package com.vipkid.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.vipkid.model.Order.Status;
import com.vipkid.model.OrderItem;

@Repository
public class OrderItemRepository extends BaseRepository<OrderItem> {
	//private Logger logger = LoggerFactory.getLogger(OrderItemRepository.class);
	
	public OrderItemRepository() {
		super(OrderItem.class);
	}
	
	public List<OrderItem> findByOrderId(long orderId){
		String sql = "SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId";
		TypedQuery<OrderItem> typedQuery = entityManager.createQuery(sql, OrderItem.class);
		typedQuery.setParameter("orderId", orderId);
		
		return typedQuery.getResultList();
	}
	
	public List<OrderItem> findByStudentAndCourse(long studentId,long courseId){
		String sql = "SELECT oi FROM OrderItem oi where oi.order.student.id=:studentId AND oi.order.status=:status AND oi.product.course.id=:courseId ORDER BY oi.order.createDateTime ASC";
		TypedQuery<OrderItem> typedQuery = entityManager.createQuery(sql, OrderItem.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("status", Status.PAY_CONFIRMED);
		typedQuery.setParameter("courseId", courseId);
		
		return typedQuery.getResultList();
	}
}
