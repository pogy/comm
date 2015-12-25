package com.vipkid.repository;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vipkid.model.Leads;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Order;
import com.vipkid.model.Order.PayBy;
import com.vipkid.model.Order.Status;
import com.vipkid.model.OrderItem;
import com.vipkid.model.Order_;
import com.vipkid.model.Product;
import com.vipkid.model.Student;
import com.vipkid.model.Student_;
import com.vipkid.model.User_;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.leads.OrderItemVo;
import com.vipkid.service.pojo.leads.OrderVo;
import com.vipkid.util.DaoUtils;
import com.vipkid.util.DateTimeUtils;

@Repository
public class OrderRepository extends BaseRepository<Order> {

	public OrderRepository() {
		super(Order.class);
	}
	
	public List<Order> list(String serialNumber, String search, Status status, List<PayBy> payBy, List<Long> salesIds, DateTimeParam fromDate, DateTimeParam toDate, int start, int length) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
		Root<Order> order = criteriaQuery.from(Order.class);
//		Join<Order, Parent> parent = order.join(Order_.parent, JoinType.LEFT);
		Join<Order, Student> student = order.join(Order_.student, JoinType.LEFT);

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(Student_.englishName), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		//serialNumber
		if (serialNumber != null) {
			andPredicates.add(criteriaBuilder.equal(order.get(Order_.serialNumber), serialNumber));
		}
		//status
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(order.get(Order_.status), status));
		}
		//payBy
		if (payBy != null && payBy.size() > 0) {
			andPredicates.add(order.get(Order_.payBy).in(payBy));
		}
		//salesIds
		if (salesIds != null && salesIds.size() > 0) {
			andPredicates.add(order.get(Order_.creater).get("id").in(salesIds));
		}
		
		if (fromDate != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(order.get(Order_.createDateTime), fromDate.getValue()));
		}
		if (toDate != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(order.get(Order_.createDateTime), DateTimeUtils.getNextDay(toDate.getValue())));
		}
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
        List<Predicate> finalPredicates = new LinkedList<Predicate>();
        if(orPredicates.size() > 0) {
            finalPredicates.add(orPredicate);
        }
        if(andPredicates.size() > 0) {
            finalPredicates.add(andPredicate);
        }
        Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
        criteriaQuery.where(finalPredicate);

		criteriaQuery.orderBy(criteriaBuilder.desc(order.get(Order_.createDateTime)));
		TypedQuery<Order> typedQuery = entityManager.createQuery(criteriaQuery);
		typedQuery.setFirstResult(start);
		typedQuery.setMaxResults(length);
		return typedQuery.getResultList();
	}
	
	public long count(String serialNumber, String search, Status status, List<PayBy> payBy, List<Long> salesIds, DateTimeParam fromDate, DateTimeParam toDate) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
		Root<Order> order = criteriaQuery.from(Order.class);
		Join<Order, Student> student = order.join(Order_.student, JoinType.LEFT);
		criteriaQuery.select(criteriaBuilder.count(order));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(Student_.englishName), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		//serialNumber
		if (serialNumber != null) {
			andPredicates.add(criteriaBuilder.equal(order.get(Order_.serialNumber), serialNumber));
		}
		//status
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(order.get(Order_.status), status));
		}
		//payBy
		if (payBy != null) {
			andPredicates.add(order.get(Order_.payBy).in(payBy));
		}
		//salesIds
		if (salesIds != null && salesIds.size() > 0) {
			andPredicates.add(order.get(Order_.creater).get("id").in(salesIds));
		}
		
		if (fromDate != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(order.get(Order_.createDateTime), fromDate.getValue()));
		}
		if (toDate != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(order.get(Order_.createDateTime), DateTimeUtils.getNextDay(toDate.getValue())));
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
        if(orPredicates.size() > 0) {
            finalPredicates.add(orPredicate);
        }
        if(andPredicates.size() > 0) {
            finalPredicates.add(andPredicate);
        }
        Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
        criteriaQuery.where(finalPredicate);
		TypedQuery<Long> typedQuery = entityManager.createQuery(criteriaQuery);
		return typedQuery.getSingleResult();
	}

	public double countTotalDealPrice(String serialNumber, String search, Status status, List<PayBy> payBy, List<Long> salesIds, DateTimeParam fromDate, DateTimeParam toDate) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Double> criteriaQuery = criteriaBuilder.createQuery(Double.class);
		Root<Order> order = criteriaQuery.from(Order.class);
		Join<Order, Student> student = order.join(Order_.student, JoinType.LEFT);
		criteriaQuery.select(criteriaBuilder.sumAsDouble(order.get(Order_.totalDealPrice)));

		// compose OR predicate
		List<Predicate> orPredicates = new LinkedList<Predicate>();
		if (search != null) {
			orPredicates.add(criteriaBuilder.like(student.get(User_.name), "%" + search + "%"));
			orPredicates.add(criteriaBuilder.like(student.get(Student_.englishName), "%" + search + "%"));
		}
		Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));

		// compose AND predicate
		List<Predicate> andPredicates = new LinkedList<Predicate>();
		//serialNumber
		if (serialNumber != null) {
			andPredicates.add(criteriaBuilder.equal(order.get(Order_.serialNumber), serialNumber));
		}
		//status
		if (status != null) {
			andPredicates.add(criteriaBuilder.equal(order.get(Order_.status), status));
		}
		//payBy
		if (payBy != null) {
			andPredicates.add(order.get(Order_.payBy).in(payBy));
		}
		//salesIds
		if (salesIds != null && salesIds.size() > 0) {
			andPredicates.add(order.get(Order_.creater).get("id").in(salesIds));
		}
		
		if (fromDate != null) {
			andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(order.get(Order_.createDateTime), fromDate.getValue()));
		}
		if (toDate != null) {
			andPredicates.add(criteriaBuilder.lessThanOrEqualTo(order.get(Order_.createDateTime), DateTimeUtils.getNextDay(toDate.getValue())));
		}
		
		Predicate andPredicate = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
		
		// compose final predicate
		List<Predicate> finalPredicates = new LinkedList<Predicate>();
        if(orPredicates.size() > 0) {
            finalPredicates.add(orPredicate);
        }
        if(andPredicates.size() > 0) {
            finalPredicates.add(andPredicate);
        }
        Predicate finalPredicate = criteriaBuilder.and(finalPredicates.toArray(new Predicate[finalPredicates.size()]));
        criteriaQuery.where(finalPredicate);
		TypedQuery<Double> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Double> retList =  typedQuery.getResultList();
		if (CollectionUtils.isNotEmpty(retList) && retList.get(0) != null) {
			return retList.get(0);
		} else {
			return 0;
		}
	}
	
	public Order findLastByCreateDateTime() {
		String sql = "SELECT o FROM Order o ORDER BY o.createDateTime DESC";
		TypedQuery<Order> typedQuery = entityManager.createQuery(sql, Order.class);
		return typedQuery.getResultList().get(0);
	}
	
	public List<Order> findByFamilyId(long familyId){
		String sql = "SELECT o FROM Order o WHERE o.family.id = :familyId ORDER BY o.status DESC, o.createDateTime DESC";
		TypedQuery<Order> typedQuery = entityManager.createQuery(sql, Order.class);
		typedQuery.setParameter("familyId", familyId);
		return typedQuery.getResultList();
	}
	
	// 2015-01-30 获取学生的order list
	public List<Order> findByStudentId(long studentId){
		String sql = "SELECT o FROM Order o WHERE o.student.id = :studentId ORDER BY o.createDateTime DESC";
		TypedQuery<Order> typedQuery = entityManager.createQuery(sql, Order.class);
		typedQuery.setParameter("studentId", studentId);
		return typedQuery.getResultList();
	}
	
	public Long countPayConfirmedByStudentId(long studentId){
		String sql = "SELECT COUNT(o) FROM Order o JOIN o.orderItems oi JOIN oi.product p WHERE o.student.id = :studentId AND o.status = :status AND p.type = :productType ORDER BY o.createDateTime DESC";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql, Long.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("status", Order.Status.PAY_CONFIRMED);
		typedQuery.setParameter("productType", Product.Type.PAID);
		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return (long) 0; // because there is no result.
		}
	}
	
	public List<Order> findLastWeekPayConfirmedOrders(){
		String sql = "SELECT o FROM Order o WHERE o.status = :status AND o.paidDateTime BETWEEN :startDateTime AND :endDateTime";
		TypedQuery<Order> typedQuery = entityManager.createQuery(sql, Order.class);
		typedQuery.setParameter("status", Status.PAY_CONFIRMED);
		Calendar startOfLastWeekCalendar = Calendar.getInstance();
		Calendar endofLastWeekCalendar = Calendar.getInstance();
		startOfLastWeekCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
		startOfLastWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		startOfLastWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfLastWeekCalendar.set(Calendar.MINUTE, 0);
		startOfLastWeekCalendar.set(Calendar.SECOND, 0);
		startOfLastWeekCalendar.set(Calendar.MILLISECOND, 0);
		startOfLastWeekCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
		endofLastWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		endofLastWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endofLastWeekCalendar.set(Calendar.MINUTE, 59);
		endofLastWeekCalendar.set(Calendar.SECOND, 59);
		endofLastWeekCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfLastWeekCalendar.getTime());
		typedQuery.setParameter("endDateTime", endofLastWeekCalendar.getTime());
		return typedQuery.getResultList();
	}
	
	public List<OnlineClass> findCurrentAndNextWeekBookedOnlineClassesByLearningProgressCourseIdAndStudentId(long learningProgressCourseId, long studentId) {
		String sql = "SELECT DISTINCT o FROM OnlineClass o JOIN o.students s WHERE o.lesson.learningCycle.unit.course.id = :courseId AND s.id = :studentId AND o.status = :status AND "
				+ "o.scheduledDateTime BETWEEN :startDateTime AND :endDateTime ORDER BY o.scheduledDateTime";
		TypedQuery<OnlineClass> typedQuery = entityManager.createQuery(sql, OnlineClass.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("courseId", learningProgressCourseId);
		typedQuery.setParameter("status", OnlineClass.Status.BOOKED);
		Calendar startOfThisWeekCalendar = Calendar.getInstance();
		Calendar endofnextWeekCalendar = Calendar.getInstance();
		startOfThisWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		startOfThisWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		startOfThisWeekCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startOfThisWeekCalendar.set(Calendar.MINUTE, 0);
		startOfThisWeekCalendar.set(Calendar.SECOND, 0);
		startOfThisWeekCalendar.set(Calendar.MILLISECOND, 0);
		endofnextWeekCalendar.setFirstDayOfWeek(Calendar.MONDAY);
		endofnextWeekCalendar.add(Calendar.WEEK_OF_YEAR, 1);
		endofnextWeekCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		endofnextWeekCalendar.set(Calendar.HOUR_OF_DAY, 23);
		endofnextWeekCalendar.set(Calendar.MINUTE, 59);
		endofnextWeekCalendar.set(Calendar.SECOND, 59);
		endofnextWeekCalendar.set(Calendar.MILLISECOND, 0);
		typedQuery.setParameter("startDateTime", startOfThisWeekCalendar.getTime());
		typedQuery.setParameter("endDateTime", endofnextWeekCalendar.getTime());
		
		List<OnlineClass> onlineClasses = typedQuery.getResultList();
		return onlineClasses;
	}
	
	public Order findBySerialNumber(String serialNumber) {
		Order order = null;
		String sql = "SELECT o FROM Order o WHERE　o.serialNumber = :serialNumber  ORDER BY o.createDateTime DESC";
		TypedQuery<Order> typedQuery = entityManager.createQuery(sql, Order.class);
		typedQuery.setParameter("serialNumber", serialNumber);
		List<Order> orderList = typedQuery.getResultList();
		if (!orderList.isEmpty()) {
			order = orderList.get(0);
		}
		return order;
	}
	
	public Count countTopayByFamilyId(long familyId) {
		String sql = "SELECT o FROM Order o WHERE o.status = :status AND o.family.id = :familyId";
		TypedQuery<Order> typedQuery = entityManager.createQuery(sql, Order.class);
		typedQuery.setParameter("status", Status.TO_PAY);
		typedQuery.setParameter("familyId", familyId);
		List<Order> orderList = typedQuery.getResultList();
		Count count = null;
		if (!orderList.isEmpty()) {
			count = new Count(orderList.size());
		}else{
			count = new Count(0);
		}
		return count;
	}
	
	public List<Order> listOrderByFamilyId(long familyId,Integer rowNum,Integer currNum){
		String sql = "SELECT o FROM Order o WHERE o.family.id = :familyId ORDER BY o.status DESC, o.createDateTime DESC";
		TypedQuery<Order> typedQuery = entityManager.createQuery(sql, Order.class);
		typedQuery.setParameter("familyId", familyId);
		typedQuery.setFirstResult((currNum-1)*rowNum);
		typedQuery.setMaxResults(rowNum);
		return typedQuery.getResultList();
	}
	
	public long countOrderByFamilyId(long familyId){
		String sql = "SELECT count(distinct o) FROM Order o WHERE o.family.id = :familyId ORDER BY o.status DESC, o.createDateTime DESC";
		TypedQuery<Long> typedQuery = entityManager.createQuery(sql,Long.class);
		typedQuery.setParameter("familyId", familyId);
		long count = typedQuery.getSingleResult();
		return count;
	}
	
	public List<Order> findOrderList(){
		String sql = "select o from Order o where o.createDateTime <= :timeEnd";
		Date timeEnd = DateTimeUtils.parse("2015-03-26 23:59:59", DateTimeUtils.DATETIME_FORMAT);
		TypedQuery<Order> typedQuery = entityManager.createQuery(sql, Order.class);
		typedQuery.setParameter("timeEnd", timeEnd);
		return typedQuery.getResultList();
	}
	
	public void updateOrderItemByItemIdAndOrderId(long orderItemId,long orderId){
		String sql = "update order_item set order_id = ? where id = ?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, orderId);
		query.setParameter(2, orderItemId);
		query.executeUpdate();
	}
	
	public long countForLeads(Leads.OwnerType ownerType, List<Long> staffIds, Date paidDateTimeFrom, Date paidDateTimeTo, Status status) {
		long count = 0;
		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		jpql.append("select count(order) from Order order,Leads leads where order.student.id = leads.studentId");
		
		//staffIds
		if (CollectionUtils.isNotEmpty(staffIds)) {
			if (staffIds.size() > 1) {
				if (ownerType == Leads.OwnerType.STAFF_SALES) {
					jpql.append(" and leads.salesId in :staffIds");
				} else if (ownerType == Leads.OwnerType.STAFF_TMK){
					jpql.append(" and leads.tmkId in :staffIds");
					
				}
				params.put("staffIds", staffIds);
			} else {
				if (ownerType == Leads.OwnerType.STAFF_SALES) {
					jpql.append(" and leads.salesId = :staffId");
				} else if (ownerType == Leads.OwnerType.STAFF_TMK){
					jpql.append(" and leads.tmkId = :staffId");
					
				}
				params.put("staffId", staffIds.get(0));
				
			}
		}
		
		// targetDateTimeFrom/targetDateTimeTo
		if (paidDateTimeFrom != null) {
			jpql.append(" and order.paidDateTime >= :paidDateTimeFrom");
			params.put("paidDateTimeFrom", paidDateTimeFrom);
		}
		if (paidDateTimeTo != null) {
			paidDateTimeTo = DateTimeUtils.getNextDay(paidDateTimeTo);
			jpql.append(" and order.paidDateTime < :paidDateTimeTo");
			params.put("paidDateTimeTo", paidDateTimeTo);
		}
		
		if (status != null) {
			jpql.append(" and order.status = :status");
			params.put("status", status);
		}
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		count = (Long)query.getSingleResult();
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public List<OrderVo> listOrderForLeads(Date createDateTimeFrom, Date createDateTimeTo,Date paidDateTimeFrom, Date paidDateTimeTo,
			Status status, PayBy payBy, Long salesId, Long tmkId, String searchText, Integer start, Integer length) {

		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		
		jpql.append("select order,leads.salesId,leads.salesName from Order order,Leads leads where order.student.id = leads.studentId ");
		
		// createDateTimeFrom/createDateTimeTo
		if (createDateTimeFrom != null) {
			jpql.append(" and order.createDateTime >= :createDateTimeFrom");
			params.put("createDateTimeFrom", createDateTimeFrom);
		}
		if (createDateTimeTo != null) {
			createDateTimeTo = DateTimeUtils.getNextDay(createDateTimeTo);
			jpql.append(" and order.createDateTime < :createDateTimeTo");
			params.put("createDateTimeTo", createDateTimeTo);
		}
		
		// paidDateTimeFrom/paidDateTimeTo
		if (paidDateTimeFrom != null) {
			jpql.append(" and order.paidDateTime >= :paidDateTimeFrom");
			params.put("paidDateTimeFrom", paidDateTimeFrom);
		}
		if (paidDateTimeTo != null) {
			paidDateTimeTo = DateTimeUtils.getNextDay(paidDateTimeTo);
			jpql.append(" and order.paidDateTime < :paidDateTimeTo");
			params.put("paidDateTimeTo", paidDateTimeTo);
		}
		
		
		//status
		if (status != null) {
			jpql.append(" and order.status = :status");
			params.put("status", status);
		}
		//payBy
		if (payBy != null) {
			jpql.append(" and order.payBy = :payBy");
			params.put("payBy", payBy);
		}
		
		//salesId
		if (salesId != null) {
			jpql.append(" and leads.salesId = :salesId");
			params.put("salesId", salesId);
		}
		
		//tmkId
		if (tmkId != null) {
			jpql.append(" and leads.tmkId = :tmkId");
			params.put("tmkId", tmkId);
		}
		
		//searchText
		if (StringUtils.isNotBlank(searchText)) {
			jpql.append(" and exists (")
			.append(" select 1 from Parent parent")
			.append(" where parent.family = order.student.family")
			.append(" and (parent.mobile = :searchText or parent.name like :searchTextLike or order.student.name like :searchTextLike or order.student.englishName like :searchTextLike)")
			.append(")");
			params.put("searchText", searchText);
			params.put("searchTextLike", "%" + searchText + "%");
		}
		jpql.append(" order by leads.id desc");
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		if (start != null) {
			query.setFirstResult(start);
		}
		if (length != null) {
			query.setMaxResults(length);
		}
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		List<OrderVo> orderVoList = Lists.newArrayList();
		OrderVo orderVo = null;
		if (CollectionUtils.isNotEmpty(resultList)) {
			for (Object[] row : resultList) {
				Order order = (Order) row[0];
				salesId = (Long) row[1];
				String salesName = (String) row[2];
				
				
				//orderItem
				List<OrderItemVo> orderItemVos = Lists.newArrayList();
				OrderItemVo orderItemVo = null;
				float totalPrice = 0;
				int totalClassHour = 0;
				if (CollectionUtils.isNotEmpty(order.getOrderItems())) {
					for (OrderItem orderItem : order.getOrderItems()) {
						orderItemVo = new OrderItemVo();
						orderItemVo.setId(orderItem.getId());
						orderItemVo.setProductName(orderItem.getProduct().getName());
						orderItemVo.setClassHour(orderItem.getClassHour());
						orderItemVo.setPrice(orderItem.getPrice());
						orderItemVo.setDealPrice(orderItem.getDealPrice());
						orderItemVos.add(orderItemVo);
						
						totalPrice += orderItemVo.getPrice();
						totalClassHour += orderItemVo.getClassHour();
					}
				}
				
				orderVo = new OrderVo();
				orderVo.setId(order.getId());
				orderVo.setOrderItems(orderItemVos);
				orderVo.setPayBy(order.getPayBy());				
				orderVo.setSalesId(salesId);
				orderVo.setSalesName(salesName);
				orderVo.setSerialNumber(order.getSerialNumber());
				orderVo.setStatus(order.getStatus());
				orderVo.setTotalClassHour(totalClassHour);
				orderVo.setTotalDealPrice(order.getTotalDealPrice());
				orderVo.setTotalPrice(totalPrice);
				
				if (order.getStudent() != null) {
					orderVo.setStuEnglishName(order.getStudent().getEnglishName());
					orderVo.setStuId(order.getStudent().getId());
					orderVo.setStuName(order.getStudent().getName());
				}
				orderVoList.add(orderVo);
			}
		}
			
		return orderVoList;
	
	}

	public long countOrderForLeads(Date createDateTimeFrom,Date createDateTimeTo, Date paidDateTimeFrom, Date paidDateTimeTo,
			Order.Status status, PayBy payBy, Long salesId, Long tmkId, String searchText) {

		StringBuffer jpql = new StringBuffer();
		Map<String,Object> params = Maps.newHashMap();
		
		jpql.append("select count(order) from Order order,Leads leads where order.student.id = leads.studentId ");
		
		// createDateTimeFrom/createDateTimeTo
		if (createDateTimeFrom != null) {
			jpql.append(" and order.createDateTime >= :createDateTimeFrom");
			params.put("createDateTimeFrom", createDateTimeFrom);
		}
		if (createDateTimeTo != null) {
			createDateTimeTo = DateTimeUtils.getNextDay(createDateTimeTo);
			jpql.append(" and order.createDateTime < :createDateTimeTo");
			params.put("createDateTimeTo", createDateTimeTo);
		}
		
		// paidDateTimeFrom/paidDateTimeTo
		if (paidDateTimeFrom != null) {
			jpql.append(" and order.paidDateTime >= :paidDateTimeFrom");
			params.put("paidDateTimeFrom", paidDateTimeFrom);
		}
		if (paidDateTimeTo != null) {
			paidDateTimeTo = DateTimeUtils.getNextDay(paidDateTimeTo);
			jpql.append(" and order.paidDateTime < :paidDateTimeTo");
			params.put("paidDateTimeTo", paidDateTimeTo);
		}
		
		
		//status
		if (status != null) {
			jpql.append(" and order.status = :status");
			params.put("status", status);
		}
		//payBy
		if (payBy != null) {
			jpql.append(" and order.payBy = :payBy");
			params.put("payBy", payBy);
		}
		
		//salesId
		if (salesId != null) {
			jpql.append(" and leads.salesId = :salesId");
			params.put("salesId", salesId);
		}
		
		//tmkId
		if (tmkId != null) {
			jpql.append(" and leads.tmkId = :tmkId");
			params.put("tmkId", tmkId);
		}
		
		//searchText
		if (StringUtils.isNotBlank(searchText)) {
			jpql.append(" and exists (")
			.append(" select 1 from Parent parent")
			.append(" where parent.family = order.student.family")
			.append(" and (parent.mobile = :searchText or parent.name like :searchTextLike or order.student.name like :searchTextLike or order.student.englishName like :searchTextLike)")
			.append(")");
			params.put("searchText", searchText);
			params.put("searchTextLike", "%" + searchText + "%");
		}
		
		Query query = entityManager.createQuery(jpql.toString());
		DaoUtils.setQueryParameters(query, params);
		long count = (long) query.getSingleResult();
			
		return count;
	
	}
}
