package com.vipkid.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.OnlineClassAdapter;
import com.vipkid.model.json.moxy.StudentAdapter;
import com.vipkid.model.json.moxy.TeacherAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 闈㈣瘯鎶ュ憡
 */
@Entity
@Table(name = "demo_report", schema = DBInfo.SCHEMA)
public class DemoReport extends Base {
	private static final long serialVersionUID = 1L;
	
	public enum Answer{
		YES,
		WITH_DIFFICULT,
		NO,
	}
	
	public enum Level{
		L1U0,
		L1U1,
		L1U4,
		L1U9,
		L2U1,
		L2U4,
		L2U7,
		L2U10,
		L3U1,
		L3U4,
		L3U7,
		L3U10,
		L4U1,
		L4U4,
		L4U7,
		L4U10,
		
	}
	
	public enum LifeCycle{
		UNFINISHED,  //
		SUBMITTED,  // 闈㈣瘯鑰佸笀纭畾鎻愪氦
		CONFIRMED,  // Lane宸茬粡纭畾鍙彂閫佺粰瀹堕暱
		PARENTREADED,  // 瀹堕暱宸茶
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	// 鍦ㄧ嚎鏁欏
	@XmlJavaTypeAdapter(OnlineClassAdapter.class)
	@OneToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "online_class_id", referencedColumnName = "id")
	private OnlineClass onlineClass;
	
	// 鐢熷懡鍛ㄦ湡
	@Enumerated(EnumType.STRING)
	@Column(name = "life_cycle")
	private LifeCycle lifeCycle;	

	// 鍒涘缓鏃堕棿
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date_time")
	private Date createDateTime;
	
	// submitted time
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "submit_date_time")
	private Date submitDateTime;
	
	// Confirmed time
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "confirm_date_time")
	private Date confirmDateTime;
	
	
	// 瀛︾敓
	@XmlJavaTypeAdapter(StudentAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "student_id", referencedColumnName = "id")
	private Student student;
	
	// 鑰佸笀
	@XmlJavaTypeAdapter(TeacherAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "teacher_id", referencedColumnName = "id")
	private Teacher teacher;
	
	// l1
    @Column(name = "l1")
    private Answer l1;
    
    // l2
    @Column(name = "l2")
    private Answer l2;
    
    // l3
    @Column(name = "l3")
    private Answer l3;
    
    // l4
    @Column(name = "l4")
    private Answer l4;
    
    // l5
    @Column(name = "l5")
    private Answer l5;
 
    // l6
    @Column(name = "l6")
    private Answer l6;
 
    // l7
    @Column(name = "l7")
    private Answer l7;
    
    // s1
    @Column(name = "s1")
    private Answer s1;
    
    // s2
    @Column(name = "s2")
    private Answer s2;
    
    // s3
    @Column(name = "s3")
    private Answer s3;
    
    // s4
    @Column(name = "s4")
    private Answer s4;
    
    // s5
    @Column(name = "s5")
    private Answer s5;
    
    // s6
    @Column(name = "s6")
    private Answer s6;
    
    // s7
    @Column(name = "s7")
    private Answer s7;
    
    // s8
    @Column(name = "s8")
    private Answer s8;
    
    // s9
    @Column(name = "s9")
    private Answer s9;
    
    // s10
    @Column(name = "s10")
    private Answer s10;
    
    // s11
    @Column(name = "s11")
    private Answer s11;
    
    // s12
    @Column(name = "s12")
    private Answer s12;
    
    // s13
    @Column(name = "s13")
    private Answer s13;
    
    // s14
    @Column(name = "s14")
    private Answer s14;
    
    // s15
    @Column(name = "s15")
    private Answer s15;
    
    // r1
    @Column(name = "r1")
    private Answer r1;
    
    // r2
    @Column(name = "r2")
    private Answer r2;
    
    // r3
    @Column(name = "r3")
    private Answer r3;
    
    // r4
    @Column(name = "r4")
    private Answer r4;
    
    // r5
    @Column(name = "r5")
    private Answer r5;
    
    // r6
    @Column(name = "r6")
    private Answer r6;
    
    // r7
    @Column(name = "r7")
    private Answer r7;
    
    // r8
    @Column(name = "r8")
    private Answer r8;
   
    // r9
    @Column(name = "r9")
    private Answer r9;
    
    // r10
    @Column(name = "r10")
    private Answer r10;
    
    // r11
    @Column(name = "r11")
    private Answer r11;
    
    // r12
    @Column(name = "r12")
    private Answer r12;
    
    // r13
    @Column(name = "r13")
    private Answer r13;
   
    // r14
    @Column(name = "r14")
    private Answer r14;
    
    // r15
    @Column(name = "r15")
    private Answer r15;
    
    // r16
    @Column(name = "r16")
    private Answer r16;
    
    // r17
    @Column(name = "r17")
    private Answer r17;
    
    
    // m1
    @Column(name = "m1")
    private Answer m1;
    
    // m2
    @Column(name = "m2")
    private Answer m2;
    
    // m3
    @Column(name = "m3")
    private Answer m3;
    
    // m4
    @Column(name = "m4")
    private Answer m4;
    
    // m5
    @Column(name = "m5")
    private Answer m5;
    
    // m6
    @Column(name = "m6")
    private Answer m6;
    
    // m7
    @Column(name = "m7")
    private Answer m7;
    
    // m8
    @Column(name = "m8")
    private Answer m8;
    
    // m9
    @Column(name = "m9")
    private Answer m9;
    
    // m10
    @Column(name = "m10")
    private Answer m10;
    
    // level
    @Column(name = "level")
    private Level level;
    
    // 璇勮
    @Lob
    @Column(name = "comment")
    private String comment;
    
    // 娉ㄦ剰鍔�
    @Column(name = "attention")
    private int attention;
    
    // 浜掑姩鑳藉姏
    @Column(name = "interaction")
    private int interaction;
    
    // 鑷俊
    @Column(name = "confidence")
    private int confidence;
    
    // 鐙珛鍥炵瓟闂鑳藉姏
    @Column(name = "independent")
    private int independent;
    
    // 浣跨敤榧犳爣鑳藉姏
    @Column(name = "mouse")
    private int mouse;
    
    
    @PrePersist
	public void prePersist() {
		this.createDateTime = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public OnlineClass getOnlineClass() {
		return onlineClass;
	}

	public void setOnlineClass(OnlineClass onlineClass) {
		this.onlineClass = onlineClass;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LifeCycle getLifeCycle() {
		return lifeCycle;
	}

	public void setLifeCycle(LifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Answer getL1() {
		return l1;
	}

	public void setL1(Answer l1) {
		this.l1 = l1;
	}

	public Answer getL2() {
		return l2;
	}

	public void setL2(Answer l2) {
		this.l2 = l2;
	}

	public Answer getL3() {
		return l3;
	}

	public void setL3(Answer l3) {
		this.l3 = l3;
	}

	public Answer getL4() {
		return l4;
	}

	public void setL4(Answer l4) {
		this.l4 = l4;
	}

	public Answer getL5() {
		return l5;
	}

	public void setL5(Answer l5) {
		this.l5 = l5;
	}

	public Answer getL6() {
		return l6;
	}

	public void setL6(Answer l6) {
		this.l6 = l6;
	}

	public Answer getL7() {
		return l7;
	}

	public void setL7(Answer l7) {
		this.l7 = l7;
	}

	public Answer getS1() {
		return s1;
	}

	public void setS1(Answer s1) {
		this.s1 = s1;
	}

	public Answer getS2() {
		return s2;
	}

	public void setS2(Answer s2) {
		this.s2 = s2;
	}

	public Answer getS3() {
		return s3;
	}

	public void setS3(Answer s3) {
		this.s3 = s3;
	}

	public Answer getS4() {
		return s4;
	}

	public void setS4(Answer s4) {
		this.s4 = s4;
	}

	public Answer getS5() {
		return s5;
	}

	public void setS5(Answer s5) {
		this.s5 = s5;
	}

	public Answer getS6() {
		return s6;
	}

	public void setS6(Answer s6) {
		this.s6 = s6;
	}

	public Answer getS7() {
		return s7;
	}

	public void setS7(Answer s7) {
		this.s7 = s7;
	}

	public Answer getS8() {
		return s8;
	}

	public void setS8(Answer s8) {
		this.s8 = s8;
	}

	public Answer getS9() {
		return s9;
	}

	public void setS9(Answer s9) {
		this.s9 = s9;
	}

	public Answer getS10() {
		return s10;
	}

	public void setS10(Answer s10) {
		this.s10 = s10;
	}

	public Answer getS11() {
		return s11;
	}

	public void setS11(Answer s11) {
		this.s11 = s11;
	}

	public Answer getS12() {
		return s12;
	}

	public void setS12(Answer s12) {
		this.s12 = s12;
	}

	public Answer getS13() {
		return s13;
	}

	public void setS13(Answer s13) {
		this.s13 = s13;
	}

	public Answer getS14() {
		return s14;
	}

	public void setS14(Answer s14) {
		this.s14 = s14;
	}

	public Answer getS15() {
		return s15;
	}

	public void setS15(Answer s15) {
		this.s15 = s15;
	}

	public Answer getR1() {
		return r1;
	}

	public void setR1(Answer r1) {
		this.r1 = r1;
	}

	public Answer getR2() {
		return r2;
	}

	public void setR2(Answer r2) {
		this.r2 = r2;
	}

	public Answer getR3() {
		return r3;
	}

	public void setR3(Answer r3) {
		this.r3 = r3;
	}

	public Answer getR4() {
		return r4;
	}

	public void setR4(Answer r4) {
		this.r4 = r4;
	}

	public Answer getR5() {
		return r5;
	}

	public void setR5(Answer r5) {
		this.r5 = r5;
	}

	public Answer getR6() {
		return r6;
	}

	public void setR6(Answer r6) {
		this.r6 = r6;
	}

	public Answer getR7() {
		return r7;
	}

	public void setR7(Answer r7) {
		this.r7 = r7;
	}

	public Answer getR8() {
		return r8;
	}

	public void setR8(Answer r8) {
		this.r8 = r8;
	}

	public Answer getR9() {
		return r9;
	}

	public void setR9(Answer r9) {
		this.r9 = r9;
	}

	public Answer getR10() {
		return r10;
	}

	public void setR10(Answer r10) {
		this.r10 = r10;
	}

	public Answer getR11() {
		return r11;
	}

	public void setR11(Answer r11) {
		this.r11 = r11;
	}

	public Answer getR12() {
		return r12;
	}

	public void setR12(Answer r12) {
		this.r12 = r12;
	}

	public Answer getR13() {
		return r13;
	}

	public void setR13(Answer r13) {
		this.r13 = r13;
	}

	public Answer getR14() {
		return r14;
	}

	public void setR14(Answer r14) {
		this.r14 = r14;
	}

	public Answer getR15() {
		return r15;
	}

	public void setR15(Answer r15) {
		this.r15 = r15;
	}

	public Answer getR16() {
		return r16;
	}

	public void setR16(Answer r16) {
		this.r16 = r16;
	}

	public Answer getR17() {
		return r17;
	}

	public void setR17(Answer r17) {
		this.r17 = r17;
	}

	public Answer getM1() {
		return m1;
	}

	public void setM1(Answer m1) {
		this.m1 = m1;
	}

	public Answer getM2() {
		return m2;
	}

	public void setM2(Answer m2) {
		this.m2 = m2;
	}

	public Answer getM3() {
		return m3;
	}

	public void setM3(Answer m3) {
		this.m3 = m3;
	}

	public Answer getM4() {
		return m4;
	}

	public void setM4(Answer m4) {
		this.m4 = m4;
	}

	public Answer getM5() {
		return m5;
	}

	public void setM5(Answer m5) {
		this.m5 = m5;
	}

	public Answer getM6() {
		return m6;
	}

	public void setM6(Answer m6) {
		this.m6 = m6;
	}

	public Answer getM7() {
		return m7;
	}

	public void setM7(Answer m7) {
		this.m7 = m7;
	}

	public Answer getM8() {
		return m8;
	}

	public void setM8(Answer m8) {
		this.m8 = m8;
	}

	public Answer getM9() {
		return m9;
	}

	public void setM9(Answer m9) {
		this.m9 = m9;
	}

	public Answer getM10() {
		return m10;
	}

	public void setM10(Answer m10) {
		this.m10 = m10;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Date getSubmitDateTime() {
		return submitDateTime;
	}

	public void setSubmitDateTime(Date submitDateTime) {
		this.submitDateTime = submitDateTime;
	}

	public Date getConfirmDateTime() {
		return confirmDateTime;
	}

	public void setConfirmDateTime(Date confirmDateTime) {
		this.confirmDateTime = confirmDateTime;
	}

	public int getAttention() {
		return attention;
	}

	public void setAttention(int attention) {
		this.attention = attention;
	}

	public int getInteraction() {
		return interaction;
	}

	public void setInteraction(int interaction) {
		this.interaction = interaction;
	}

	public int getConfidence() {
		return confidence;
	}

	public void setConfidence(int confidence) {
		this.confidence = confidence;
	}

	public int getIndependent() {
		return independent;
	}

	public void setIndependent(int independent) {
		this.independent = independent;
	}

	public int getMouse() {
		return mouse;
	}

	public void setMouse(int mouse) {
		this.mouse = mouse;
	}

	
}
	
	
	
	
	