package com.vipkid.repository;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.vipkid.model.Course;
import com.vipkid.model.Course.Type;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.Product;
import com.vipkid.model.Student;

@Repository
public class LearningProgressRepository extends BaseRepository<LearningProgress> {

	private Logger logger = LoggerFactory.getLogger(LearningProgressRepository.class);
	
	public LearningProgressRepository() {
		super(LearningProgress.class);
	}
	
	public List<LearningProgress> findByOnlineClassId(long onlineClassId){
		String sql = "SELECT l FROM LearningProgress l JOIN l.completedOnlineClasses lcocs WHERE lcocs.id = :onlineClassId";
		TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		
		return typedQuery.getResultList();
	}
	
	public LearningProgress findByStudentIdAndCourseId(long studentId, long courseId) {
		String sql = "SELECT l FROM LearningProgress l WHERE l.student.id = :studentId AND l.course.id = :courseId";
		logger.info("Query parameters studentId={}, courseId={}", studentId, courseId);
		TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setMaxResults(1);
		List <LearningProgress> learningProgresses = typedQuery.getResultList();
		if (!learningProgresses.isEmpty()){
			return learningProgresses.get(0);
		}
		return null;
			
	}
	
	public LearningProgress findStartedLearningProgressByStudentIdAndCourseId(long studentId, long courseId) {
		String sql = "SELECT l FROM LearningProgress l WHERE l.status = :status AND l.student.id = :studentId AND l.course.id = :courseId";
		logger.info("Query parameters studentId={}, courseId={}", studentId, courseId);
		TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
		typedQuery.setParameter("status", LearningProgress.Status.STARTED);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setMaxResults(1);
		List <LearningProgress> learningProgresses = typedQuery.getResultList();
		if (!learningProgresses.isEmpty()){
			return learningProgresses.get(0);
		}
		return null;
	}
	
	public LearningProgress findByStudentIdAndCourseIdAndOnlineClassId(long studentId, long courseId, long onlineClassId) {
		String sql = "SELECT l FROM LearningProgress l JOIN l.completedOnlineClasses lcocs WHERE l.student.id = :studentId AND l.course.id = :courseId AND lcocs.id = :onlineClassId";
		logger.info("Query parameters studentId={}, courseId={}", studentId, courseId);
		TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("courseId", courseId);
		typedQuery.setParameter("onlineClassId", onlineClassId);
		return typedQuery.getSingleResult();
	}
	
	public List<LearningProgress> findByStudentId(long studentId) {
        String sql = "SELECT l FROM LearningProgress l WHERE l.student.id = :studentId";
        logger.info("Query parameters studentId={}", studentId);
        TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
        typedQuery.setParameter("studentId", studentId);

        return typedQuery.getResultList();
    }

    public List<LearningProgress> findByStudentIdAndStatus(long studentId,LearningProgress.Status status) {
        String sql = "SELECT l FROM LearningProgress l WHERE l.student.id = :studentId and l.status = :status";
        logger.info("Query parameters studentId={},status={}", studentId,status.name());
        TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
        typedQuery.setParameter("studentId", studentId);
        typedQuery.setParameter("status", status);

        return typedQuery.getResultList();
    }
	
	public List<LearningProgress> findLeftClassHourByStudentIdWithoutTestClass(long studentId) {
		String sql = "SELECT l FROM LearningProgress l WHERE l.student.id = :studentId AND l.course.type != :itTest AND l.course.type != :demo AND l.course.type != :trial";
		logger.info("Query parameters studentId={}", studentId);
		TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("itTest", Type.IT_TEST);
		typedQuery.setParameter("demo", Type.DEMO);
		typedQuery.setParameter("trial", Type.TRIAL); // trial课从Type demo 中取出，所以新增此项

		return typedQuery.getResultList();
	}

	public LearningProgress findByFamilyId(long familyId) {
		String sql = "SELECT l FROM LearningProgress l WHERE l.student.family.id = :familyId AND l.course.serialNumber = :serialNumber";
		logger.info("Query parameters studentId={}", familyId);
		TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
		typedQuery.setParameter("familyId", familyId);
		typedQuery.setParameter("serialNumber", "C1");

		try {
			return typedQuery.getSingleResult();
		} catch (NoResultException exception) {
			return null;
		} catch (NonUniqueResultException exception) {
			return typedQuery.getResultList().get(0);
		}
	}
	
	public List<LearningProgress> findNotMajorByStudentId(long studentId) {
		String sql = "SELECT l FROM LearningProgress l WHERE l.student.id = :studentId AND l.course.type != :type";
		logger.info("Query parameters studentId={}", studentId);
		TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("type", Type.MAJOR);
		return typedQuery.getResultList();
	}
	
	public List<LearningProgress> findMajorByStudentId(long studentId) {
		String sql = "SELECT l FROM LearningProgress l WHERE l.student.id = :studentId AND l.course.type = :type";
		logger.info("Query parameters studentId={}", studentId);
		TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("type", Type.MAJOR);

		return typedQuery.getResultList();
	}
    public LearningProgress findPracticumByStudentId(long studentId) {
        String sql = "SELECT l FROM LearningProgress l WHERE l.student.id = :studentId AND l.course.type = :type";
        logger.info("Query parameters studentId={}", studentId);
        TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
        typedQuery.setParameter("studentId", studentId);
        typedQuery.setParameter("type", Type.PRACTICUM);

        return typedQuery.getSingleResult();
    }
    
    public LearningProgress findByStudentIdAndCourseType(long studentId) {
		String sql = "SELECT l FROM LearningProgress l WHERE l.status = :status AND l.student.id = :studentId AND l.course.type = :MAJOR";
		logger.info("Query parameters studentId={}, courseId={}", studentId);
		try{
			TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
			typedQuery.setParameter("status", LearningProgress.Status.STARTED);
			typedQuery.setParameter("studentId", studentId);
			typedQuery.setParameter("MAJOR", Type.MAJOR);
			return typedQuery.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<LearningProgress> findPaidByStudentId(long studentId) {
		logger.info("Query parameters studentId={}", studentId);
		
		String sql = "SELECT l FROM LearningProgress l, Product p WHERE l.productId = p.id AND l.student.id = :studentId AND p.type = :type";
		TypedQuery<LearningProgress> typedQuery = entityManager.createQuery(sql, LearningProgress.class);
		typedQuery.setParameter("studentId", studentId);
		typedQuery.setParameter("type", Product.Type.PAID);
		return typedQuery.getResultList();
	}
	/**
	 * 通过studentid onlineclassid 查询出唯一一条learningprogress
	 * @param studentId
	 * @param onlineClassId
	 * @return
	 */
	public LearningProgress findByStudentIdAndOnlineClassId(long studentId, long onlineClassId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select lp.id,lp.left_class_hour,lp.left_avaiable_class_hour,lp.student_id,lp.course_id from learning_progress lp,")
		    .append("unit u,learning_cycle lc,lesson l,online_class o ")
		    .append("where student_id= ? ")
		    .append("and lp.status = 'STARTED' ")
		    .append("and o.id = ? ")
		    .append("and o.lesson_id = l.id ")
		    .append("and l.learning_cycle_id = lc.id ")
		    .append("and lc.unit_id = u.id ")
		    .append("and u.course_id = lp.course_id ");
		logger.info("Query parameters studentId={}, onlineClassId={}", studentId, onlineClassId);
		Query query = entityManager.createNativeQuery(sql.toString());
		query.setParameter(1, studentId);
		query.setParameter(2, onlineClassId);
		query.setMaxResults(1);
		List<Object> rows = query.getResultList();
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }else{
        	Object[] row =  (Object[]) rows.get(0);
        	LearningProgress lp = new LearningProgress();
        	lp.setId((Long)row[0]);
        	lp.setLeftClassHour((Integer)row[1]);
        	lp.setLeftAvaiableClassHour((Integer)row[2]);
        	Student st = new Student();
        	st.setId((Long)row[3]);
        	lp.setStudent(st);
        	Course co = new Course();
        	co.setId((Long)row[4]);
        	lp.setCourse(co);
        	return lp;
        }
	}
	
	/**
	 * 收费公开课扣课时
	 * @param id
	 */
	public void subtractOpenClassHour(long id){
		String sql = "update learning_progress set left_class_hour = left_class_hour -1 where id = ?";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1, id);
		query.executeUpdate();
	}

}
