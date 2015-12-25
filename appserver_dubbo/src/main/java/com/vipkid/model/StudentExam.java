/**
 * 
 */
package com.vipkid.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;

import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 学生测试
 * 
 * @author vipkid
 *
 */
@Entity
@Table(	name = "student_exam", schema = DBInfo.SCHEMA
		, indexes = { 
			@Index(name="student_index", columnList = "student_id"), 
			@Index(name="family_index", columnList = "family_id"),
			@Index(name="uuid_index", columnList = "record_uuid"),
			@Index(name="status_index", columnList = "status")
		}
	)
public class StudentExam extends Base {

	private static final long serialVersionUID = 1L;

	public enum Category {
		TestSelf, // 自测
		TestCourse, // 课程测试
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

////	// 测试的学生
//	@Index(name = "student_index")
//	@Column(name = "student_id")
//	private long studentId;
	
	// 学生
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	private Student student;
	
	
	// 测试级别(类别-category)
//	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "exam_level", nullable = true)
	protected String examLevel;

	// 测试score
	@Column(name = "exam_score")
	protected int examScore;

	// exam_comment
	@Column(name = "exam_comment")
	protected String examComment;

	// exam_comment
	@Column(name = "record_uuid")
	protected String recordUuid;

	
	@Column(name="create_datetime")
	@Temporal(TemporalType.TIMESTAMP)	// web 端anjularJS无法filter处理
	protected Date createDatetime;
	
	@Column(name = "family_id")
	protected long familyId;
	
	@Column(name="end_datetime", insertable=false)
	@Temporal(TemporalType.TIMESTAMP)	
	protected Date endDatetime;
	
	@Column(name="status", insertable=false)
	protected int status;
	
	
	// setter & getter.
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRecordUuid() {
		return recordUuid;
	}

	public void setRecordUuid(String recordUuid) {
		this.recordUuid = recordUuid;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

//	public long getStudentId() {
//		return studentId;
//	}
//	
//	public void setStudentId(long student) {
//		this.studentId = student;
//	}
	
	public long getFamilyId() {
		return familyId;
	}

	public void setFamilyId(long familyId) {
		this.familyId = familyId;
	}

	public String getExamLevel() {
		return examLevel;
	}

	public void setExamLevel(String examLevel) {
		this.examLevel = examLevel;
	}

	public int getExamScore() {
		return examScore;
	}

	public void setExamScore(int examScore) {
		this.examScore = examScore;
	}

	public String getExamComment() {
		return examComment;
	}

	public void setExamComment(String examComment) {
		this.examComment = examComment;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public Date getEndDatetime() {
		return endDatetime;
	}

	public void setEndDatetime(Date endDatetime) {
		this.endDatetime = endDatetime;
	}


	/**
	 * 2015-06-24: level 0级的2种值
	 */ 
	public final static String KStrLevel0Value = "l1u0"; // l0u0
	public final static String kStrLevel0ValueNew = "l0u0";// 2015-09-30 新的level0
	public final static String KStrFoundationLevelValue = "foundation";
	
	/**
	 * 2015-06-24:
	 * 返回是否 Level 0 的 foundation
	 * @return
	 */
	public boolean _isFoundation() {
		
		return (examLevel.toLowerCase().equalsIgnoreCase(KStrFoundationLevelValue));
	}
	
	/**
	 * 2015-06-24:
	 * 返回是否 Level 0 的 foundation
	 * @return
	 */
	public boolean _isLevel0() {
		
		return (examLevel.toLowerCase().equalsIgnoreCase(KStrLevel0Value) || examLevel.toLowerCase().equalsIgnoreCase(kStrLevel0ValueNew) );
	}
	
	public boolean _isNotLevel0() {
		return	!(_isLevel0()  || _isFoundation() );
	}
	
	/**
	 * 格式化level字符串输出，显示
	 * @return
	 */
	public String formatLevelContent() {
		String strLevel = new String(examLevel);
		if (strLevel.equalsIgnoreCase(kStrLevel0ValueNew) || strLevel.equalsIgnoreCase(KStrLevel0Value) || strLevel.equalsIgnoreCase(KStrFoundationLevelValue))  {
			//
			return "VIPKID美小课程 Level1 Foundation";
		}
		
		final String kStrPrefix = "VIPKID美小课程 ";
		String strNewContent = strLevel.replace("L", "Level ");
		strNewContent = strNewContent.replace("U", " Unit ");
		return kStrPrefix+strNewContent;
	}
	

	/**
	 * 2015-08-03 
	 * 2015-08-28 version 6 description
	 */
	static private String kExamDescFoundation = "你已经做好准备开始一次英语学习之旅了， 在VIPKID 课程学习的第一年里， 你将会学到基础阅读，听力，和口语技巧。";
	static private String kExamDescGradeK = "你已经了解一些美国K年级学生掌握的学习技巧了，在VIPKID 的课程学习中，你将会完成K年级学生的学习目标，在美国，K年级是小学课程的第一年，这个年级的学生通常是5-6岁大的孩子。";
	static private String kExamDescGrade1 = "你已经全部掌握了美国K年级学生掌握的学习技巧并且了解部分1年级学生掌握的学习技巧了，在VIPKID 的课程学习中，你将会完成1年级学生的学习目标。在美国，1年级是小学课程的第二年，这个年级的学生通常是6-7岁大的孩子。";
	static private String kExamDescGrade2 = "你已经全部掌握了美国1年级学生掌握的学习技巧并且了解部分2年级学生掌握的学习技巧了，在VIPKID 的课程学习中，你将会完成2年级学生的学习目标，在美国，2年级是小学课程的第三年，这个年级的学生通常是7-8岁大的孩子。";
	
	private static HashMap<String, String>  kExamCommentDescs = new HashMap<String,String>(){ 
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			// 2015-08-03
			put("foundation",kExamDescFoundation); 
			
			put("l1u0",kExamDescFoundation); 
			put("l1u1",kExamDescGradeK);
			put("l1u4",kExamDescGradeK);
			put("l1u9",kExamDescGradeK);
			
			put("l2u1",kExamDescGradeK);
			put("l2u4",kExamDescGradeK);
			put("l2u7",kExamDescGrade1);
			put("l2u10",kExamDescGrade1);
			
			put("l3u1",kExamDescGrade1);
			put("l3u4",kExamDescGrade1);
			put("l3u7",kExamDescGrade2);
			put("l3u10",kExamDescGrade2);
			
			put("l4u1",kExamDescGrade2);
			put("l4u4",kExamDescGrade2);
			put("l4u7",kExamDescGrade2);
			put("l4u10",kExamDescGrade2);	
			
		} 
	};
	
	/**
	 * 2015-08-03 新的格式化comment字符串输出，显示
	 * @return
	 */
	public String formatLevelCommentDesc() {
		if ( StringUtils.isEmpty(examLevel) ) {
			return "";
		}
		
		String key = examLevel.toLowerCase();
		String strDesc = "";
		
		Set<String> keys = kExamCommentDescs.keySet();
		if (keys.contains(key)) {
			strDesc = kExamCommentDescs.getOrDefault(key, "");
		}
		return strDesc;
	}
	
	/**
	 * 2015-08-04 格式化输出--中文对标--内容
	 * @return
	 */
	public String formatChineseLevel_Chinese() {
		String strKey = "(";
		if ( StringUtils.isEmpty(examComment) ) {
			return "";
		}
		
		String strChinese = "";
		int nIndex = examComment.indexOf(strKey);
		if (nIndex<0) {
			return examComment;
		}
		
		strChinese = examComment.substring(0, nIndex);
		return strChinese;
	}
	
	/**
	 * 2015-08-04 格式化输出--英文对标--内容
	 * @return
	 */
	public String formatChineseLevel_English() {
		String strKey = "(";
		if ( StringUtils.isEmpty(examComment) ) {
			return "";
		}
		
		String strEnglish = "";
		int nLen = examComment.length();
		int nIndex = examComment.indexOf(strKey);
		if (nIndex<0) {
			return "";
		}
		
		int nStartIndex = nIndex+1;
		int nSize = nLen-1;
		if ( nStartIndex > nSize ) {
			//
			nSize = 1;
		}
		strEnglish = examComment.substring(nStartIndex, nSize);
		return strEnglish;
	}
}
