package com.vipkid.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.PayrollItem;

@Repository
public class PayrollItemRepository extends BaseRepository<PayrollItem> {
	private Logger logger = LoggerFactory.getLogger(PayrollItemRepository.class.getSimpleName());
	
//	@EJB
//	private StudentAccessor studentAccessor;
//	@EJB
//	private ContractAccessor contractAccessor;
//	@EJB
//	private LearningProgressAccessor learningProgressAccessor;
//	@EJB
//	private LessonAccessor lessonAccessor;
	
    
	public PayrollItemRepository() {
		super(PayrollItem.class);
	}


	public long countByPayrollId(long payrollId) {
		logger.debug("counting payroll items under {}", payrollId);

		String sql = "SELECT COUNT(p) FROM PayrollItem p WHERE p.payroll.id = :payrollId";
		Query query = entityManager.createQuery(sql);
		query.setParameter("payrollId", payrollId);

		return (Long)query.getSingleResult();
	}
	
	public List<PayrollItem> findByPayrollId(long payrollId) {
		logger.debug("querying payroll items according to payroll id");
		
		String sql = "SELECT p FROM PayrollItem p WHERE p.payroll.id = :payrollId ORDER BY p.onlineClass.scheduledDateTime DESC";
		TypedQuery<PayrollItem> query = entityManager.createQuery(sql, PayrollItem.class);
		query.setParameter("payrollId", payrollId);

		List<PayrollItem> payrollItemList = query.getResultList();
		if (payrollItemList.isEmpty()) {
			return null;
		} else {
			return payrollItemList;
		}
	}
	
	public PayrollItem findByOnlineClassId(long onlineClassId){
		String sql = "SELECT p FROM PayrollItem p WHERE p.onlineClass.id = :onlineClassId";
		TypedQuery<PayrollItem> query = entityManager.createQuery(sql, PayrollItem.class);
		query.setParameter("onlineClassId", onlineClassId);

		List<PayrollItem> payrollItemList = query.getResultList();
		if (payrollItemList.isEmpty()) {
			return null;
		} else {
			return payrollItemList.get(0);
		}
	}


	public List<PayrollItem> findByOnlineClassScheduledDateTimeAndTeacherId(
			Date scheduledDateTime, long teacherId) {
		String sql = "SELECT p FROM PayrollItem p WHERE p.onlineClass.scheduledDateTime = :datetime AND p.onlineClass.teacher.id = :teacherId";
		TypedQuery<PayrollItem> query = entityManager.createQuery(sql, PayrollItem.class);
		query.setParameter("datetime", scheduledDateTime);
		query.setParameter("teacherId", teacherId);
		
		return query.getResultList();
	}

}
