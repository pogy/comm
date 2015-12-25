package com.vipkid.repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Payroll;
import com.vipkid.model.Teacher;
import com.vipkid.util.Configurations;

@Repository
public class PayrollRepository extends BaseRepository<Payroll> {
    private Logger logger = LoggerFactory.getLogger(PayrollRepository.class.getSimpleName());

    public PayrollRepository() {
        super(Payroll.class);
    }


    public Payroll findCurrentByTeacherId(long teacherId) {
        logger.debug("querying according to teacher id");

        Calendar payDateTimeCalendar = Calendar.getInstance();
        payDateTimeCalendar.set(Calendar.DATE, Configurations.Payroll.PAY_DATE_TIME);
        payDateTimeCalendar.set(Calendar.HOUR_OF_DAY, 0);
        payDateTimeCalendar.set(Calendar.MINUTE, 0);
        payDateTimeCalendar.set(Calendar.SECOND, 0);
        payDateTimeCalendar.set(Calendar.MILLISECOND, 0);

        String sql = "SELECT p FROM Payroll p WHERE p.teacher.id = :teacherId AND p.paidDateTime = :dateTime";
        TypedQuery<Payroll> query = entityManager.createQuery(sql, Payroll.class);
        query.setParameter("teacherId", teacherId);
        query.setParameter("dateTime", payDateTimeCalendar.getTime());
        query.setFirstResult(0);
        query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Payroll findCurrentByTeacherUserId(long userId) {
        logger.debug("querying according to teacher id");

        String sql = "SELECT p FROM Payroll p WHERE p.teacher.id = :userId ORDER BY p.paidDateTime DESC";
        TypedQuery<Payroll> query = entityManager.createQuery(sql, Payroll.class);
        query.setParameter("userId", userId);
        query.setFirstResult(0);
        query.setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Payroll findByScheduledDateAndTeacher(Date scheduledDate, Teacher teacher) {
        logger.debug("querying according to scheduledDate");

        String sql = "SELECT p FROM Payroll p WHERE p.paidDateTime = :payDate AND p.teacher.id = :teacherId";
        TypedQuery<Payroll> query = entityManager.createQuery(sql, Payroll.class);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(scheduledDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, Configurations.Payroll.PAY_DATE_TIME);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date payDate = calendar.getTime();
        query.setParameter("payDate", payDate);
        query.setParameter("teacherId", teacher.getId());
        query.setFirstResult(0);
        query.setMaxResults(1);

        if (query.getResultList().size() > 0) {
            return query.getSingleResult();
        } else {
            return null;
        }
    }

    public Payroll findNextOfPayrollId(Long payrollId) {
        logger.debug("querying next according to payroll id");

        String sql = "SELECT p FROM Payroll p WHERE p.teacher.id = :userId and p.paidDateTime > :currentPaidDateTime ORDER BY p.paidDateTime ASC";
        TypedQuery<Payroll> query = entityManager.createQuery(sql, Payroll.class);

        Payroll currentPayroll = this.find(payrollId);
        long userId = currentPayroll.getTeacher().getId();
        Date currentPaidDateTime = currentPayroll.getPaidDateTime();

        query.setParameter("userId", userId);
        query.setParameter("currentPaidDateTime", currentPaidDateTime);
        query.setFirstResult(0);
        query.setMaxResults(1);

        try {
            Payroll nextPayroll = query.getSingleResult();
            return nextPayroll;
        } catch (NoResultException e) {
            return currentPayroll;
        }

    }

    public Payroll findPrevOfPayrollId(Long payrollId) {
        logger.debug("querying next according to payroll id");

        String sql = "SELECT p FROM Payroll p WHERE p.teacher.id = :userId and p.paidDateTime < :currentPaidDateTime ORDER BY p.paidDateTime DESC";
        TypedQuery<Payroll> query = entityManager.createQuery(sql, Payroll.class);

        Payroll currentPayroll = this.find(payrollId);
        long userId = currentPayroll.getTeacher().getId();
        Date currentPaidDateTime = currentPayroll.getPaidDateTime();

        query.setParameter("userId", userId);
        query.setParameter("currentPaidDateTime", currentPaidDateTime);
        query.setFirstResult(0);
        query.setMaxResults(1);

        try {
            Payroll nextPayroll = query.getSingleResult();
            return nextPayroll;
        } catch (NoResultException e) {
            return currentPayroll;
        }

    }

    public List<Payroll> findMonthListWithPayroll(long teacherId) {
        String sql = "SELECT p FROM Payroll p WHERE p.teacher.id = :teacherId ORDER BY p.paidDateTime";
        TypedQuery<Payroll> typedQuery = entityManager.createQuery(sql, Payroll.class);
        typedQuery.setParameter("teacherId", teacherId);

        return typedQuery.getResultList();
    }

}
