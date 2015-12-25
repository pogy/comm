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
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.json.moxy.FamilyAdapter;
import com.vipkid.model.json.moxy.TeacherAdapter;
import com.vipkid.model.util.DBInfo;

@Entity
@Table(name = "it_test", schema = DBInfo.SCHEMA)
public class ItTest extends Base {
	
	private static final long serialVersionUID = 1L;
	
	public enum FinalResult {
		NORMAL,
		ABNORMAL,
		NONE
	}
	
	public enum Result {
		NORMAL,
		ABNORMAL
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 测试老师
	@XmlJavaTypeAdapter(TeacherAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "teacher_id", referencedColumnName = "id")
	private Teacher teacher;
	
	// 测试学生
	@XmlJavaTypeAdapter(FamilyAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "family_id", referencedColumnName = "id")
	private Family family;
	
	// 测试时间
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "test_date_time")
	private Date testDateTime;
	
	// 是否为当前测试
	@Column(name = "current")
	private boolean current;	
	
	// 综合测试结果
	@Enumerated(EnumType.STRING)
	@Column(name = "result")
	private FinalResult finalResult; 
	
	// 操作系统信息
	@Column(name = "os")
	private String system;
	
	// 操作系统信息result
	@Column(name = "os_result")
	private Result systemResult;
	
	// 浏览器信息
	@Column(name = "browser")
	private String browser;
	
	// 浏览器信息result
	@Column(name = "browser_result")
	private Result browserResult;
	
	// flash版本
	@Column(name = "flash")
	private String flash;
	
	// flash版本result
	@Column(name = "flash_result")
	private Result flashResult;
	
	// 网络连接信息
	@Column(name = "connect")
	private String connect;
	
	// 网络连接信息result
	@Column(name = "connect_result")
	private Result connectResult;
	
	// 网络延迟信息
	@Column(name = "delay")
	private String delay;
	
	// 网络延迟信息result
	@Column(name = "delay_result")
	private Result delayResult;
	
	// 带宽延迟
	@Column(name = "band_width")
	private String bandWidth;
	
	// 带宽延迟result
	@Column(name = "band_width_result")
	private Result bandWidthResult;
	
	// 音频播放信息
	@Column(name = "sound")
	private String sound;
	
	// 音频播放信息result
	@Column(name = "sound_result")
	private Result soundResult;
	
	// 麦克风信息
	@Column(name = "mic")
	private String mic;
	
	// 麦克风信息
	@Column(name = "mic_result")
	private Result micResult;
	
	// 摄像头信息
	@Column(name = "camera")
	private String camera;
	
	// 摄像头信息
	@Column(name = "camera_result")
	private Result cameraResult;
	
	@PrePersist
	public void PrePersist(){
		this.testDateTime = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
	}

	public Date getTestDateTime() {
		return testDateTime;
	}

	public void setTestDateTime(Date testDateTime) {
		this.testDateTime = testDateTime;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public FinalResult getFinalResult() {
		return finalResult;
	}

	public void setFinalResult(FinalResult finalResult) {
		this.finalResult = finalResult;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getFlash() {
		return flash;
	}

	public void setFlash(String flash) {
		this.flash = flash;
	}

	public String getConnect() {
		return connect;
	}

	public void setConnect(String connect) {
		this.connect = connect;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public String getBandWidth() {
		return bandWidth;
	}

	public void setBandWidth(String bandWidth) {
		this.bandWidth = bandWidth;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public String getMic() {
		return mic;
	}

	public void setMic(String mic) {
		this.mic = mic;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}

	public Result getSystemResult() {
		return systemResult;
	}

	public void setSystemResult(Result systemResult) {
		this.systemResult = systemResult;
	}

	public Result getBrowserResult() {
		return browserResult;
	}

	public void setBrowserResult(Result browserResult) {
		this.browserResult = browserResult;
	}

	public Result getFlashResult() {
		return flashResult;
	}

	public void setFlashResult(Result flashResult) {
		this.flashResult = flashResult;
	}

	public Result getConnectResult() {
		return connectResult;
	}

	public void setConnectResult(Result connectResult) {
		this.connectResult = connectResult;
	}

	public Result getDelayResult() {
		return delayResult;
	}

	public void setDelayResult(Result delayResult) {
		this.delayResult = delayResult;
	}

	public Result getBandWidthResult() {
		return bandWidthResult;
	}

	public void setBandWidthResult(Result bandWidthResult) {
		this.bandWidthResult = bandWidthResult;
	}

	public Result getSoundResult() {
		return soundResult;
	}

	public void setSoundResult(Result soundResult) {
		this.soundResult = soundResult;
	}

	public Result getMicResult() {
		return micResult;
	}

	public void setMicResult(Result micResult) {
		this.micResult = micResult;
	}

	public Result getCameraResult() {
		return cameraResult;
	}

	public void setCameraResult(Result cameraResult) {
		this.cameraResult = cameraResult;
	}

}
