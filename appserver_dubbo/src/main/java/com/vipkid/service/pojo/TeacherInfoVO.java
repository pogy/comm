package com.vipkid.service.pojo;

import com.vipkid.model.*;
import com.vipkid.rest.vo.query.CourseView;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by zfl on 2015/6/17.
 */
public class TeacherInfoVO implements Serializable {
	private static final long serialVersionUID = -180338287777467510L;
    private Long id;
    private String avatar;
    private String email;
    private String realName;
    private String name;
    private Gender gender;
    private String skype;
    private String linkedIn;
    private String qq;
    private String mobile;
    private String resume;
    private String serialNumber;
    private Teacher.RecruitmentChannel recruitmentChannel;
    // 学历证书
    private String bachelorDiploma;
    // 学历证书
    private String additionalDiplomas;

    // 教学经验（年）
    private int teachingExperience;

    // 教学证书
    private String certificates;

    // 教学证书
    private String certificateFiles;

    //合同
    private String contract;

    // 阶段
    private Teacher.LifeCycle lifeCycle;

    // 类型
    private Teacher.Type type;

    // 地址
    private String address;

    // 简介
    private String introduction;

    private String shortVideo;

    // 时区
    private String timezone = Timezone.ASIA_SHANGHAI;

    // 生日
    private Date birthday;

    // 国籍
    private Country country;

    // 工作
    private String job;

    // 护照
    private String passport;

    // 生活照1
    private String lifePicture1;

    // 生活照2
    private String lifePicture2;


    // 每课时额外工资
    private float extraClassSalary;

    // 每课时加班工资
    private float overTimeClassSalary;

    // 累计逃课次数
    private long noShowTime;

    // 工资结算货币
    private Teacher.Currency currency = Teacher.Currency.US_DOLLAR;

    // 合同起始日期
    private Date contractStartDate;

    // 合同结束日期
    private Date contractEndDate;

    // 备注
    private String notes;

    // 银行卡号
    private String bankCardNumber;

    // 银行户主姓名
    private String bankAccountName;

    // 银行名称
    private String bankName;

    // 银行地址
    private String bankAddress;

    // 银行国籍代码
    private String bankSWIFTCode;

    // PayPal账号
    private String payPalAccount;

    // 简介
    private String summary;

   // 推荐人，这里存老师id和名字用逗号分隔。
    private String referee;

    // 用于招聘自动登录的ID
    private String recruitmentId;
    
    private String graduatedFrom;

	// 标记是否进行IT测试
    private boolean hasTested = false;

    private User.Status status;

    private List<CourseView> certificatedCourses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getGraduatedFrom() {
		return graduatedFrom;
	}

	public void setGraduatedFrom(String graduatedFrom) {
		this.graduatedFrom = graduatedFrom;
	}

    public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getBachelorDiploma() {
        return bachelorDiploma;
    }

    public void setBachelorDiploma(String bachelorDiploma) {
        this.bachelorDiploma = bachelorDiploma;
    }

    public String getAdditionalDiplomas() {
        return additionalDiplomas;
    }

    public void setAdditionalDiplomas(String additionalDiplomas) {
        this.additionalDiplomas = additionalDiplomas;
    }

    public int getTeachingExperience() {
        return teachingExperience;
    }

    public void setTeachingExperience(int teachingExperience) {
        this.teachingExperience = teachingExperience;
    }

    public String getCertificates() {
        return certificates;
    }

    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }

    public String getCertificateFiles() {
        return certificateFiles;
    }

    public void setCertificateFiles(String certificateFiles) {
        this.certificateFiles = certificateFiles;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public Teacher.LifeCycle getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(Teacher.LifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public Teacher.Type getType() {
        return type;
    }

    public void setType(Teacher.Type type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getShortVideo() {
        return shortVideo;
    }

    public void setShortVideo(String shortVideo) {
        this.shortVideo = shortVideo;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getLifePicture1() {
        return lifePicture1;
    }

    public void setLifePicture1(String lifePicture1) {
        this.lifePicture1 = lifePicture1;
    }

    public String getLifePicture2() {
        return lifePicture2;
    }

    public void setLifePicture2(String lifePicture2) {
        this.lifePicture2 = lifePicture2;
    }

    public float getExtraClassSalary() {
        return extraClassSalary;
    }

    public void setExtraClassSalary(float extraClassSalary) {
        this.extraClassSalary = extraClassSalary;
    }

    public float getOverTimeClassSalary() {
        return overTimeClassSalary;
    }

    public void setOverTimeClassSalary(float overTimeClassSalary) {
        this.overTimeClassSalary = overTimeClassSalary;
    }

    public long getNoShowTime() {
        return noShowTime;
    }

    public void setNoShowTime(long noShowTime) {
        this.noShowTime = noShowTime;
    }

    public Teacher.Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Teacher.Currency currency) {
        this.currency = currency;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getBankCardNumber() {
        return bankCardNumber;
    }

    public void setBankCardNumber(String bankCardNumber) {
        this.bankCardNumber = bankCardNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getBankSWIFTCode() {
        return bankSWIFTCode;
    }

    public void setBankSWIFTCode(String bankSWIFTCode) {
        this.bankSWIFTCode = bankSWIFTCode;
    }

    public String getPayPalAccount() {
        return payPalAccount;
    }

    public void setPayPalAccount(String payPalAccount) {
        this.payPalAccount = payPalAccount;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReferee() {
        return referee;
    }

    public void setReferee(String referee) {
        this.referee = referee;
    }

    public String getRecruitmentId() {
        return recruitmentId;
    }

    public void setRecruitmentId(String recruitmentId) {
        this.recruitmentId = recruitmentId;
    }

    public boolean isHasTested() {
        return hasTested;
    }

    public void setHasTested(boolean hasTested) {
        this.hasTested = hasTested;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public User.Status getStatus() {
        return status;
    }

    public void setStatus(User.Status status) {
        this.status = status;
    }

    public List<CourseView> getCertificatedCourses() {
        return certificatedCourses;
    }

    public void setCertificatedCourses(List<CourseView> certificatedCourses) {
        this.certificatedCourses = certificatedCourses;
    }

    public Teacher.RecruitmentChannel getRecruitmentChannel() {
        return recruitmentChannel;
    }

    public void setRecruitmentChannel(Teacher.RecruitmentChannel recruitmentChannel) {
        this.recruitmentChannel = recruitmentChannel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TeacherInfoVO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", realName='" + realName + '\'' +
                ", gender=" + gender +
                ", skype='" + skype + '\'' +
                ", linkedIn='" + linkedIn + '\'' +
                ", qq='" + qq + '\'' +
                ", mobile='" + mobile + '\'' +
                ", resume='" + resume + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", bachelorDiploma='" + bachelorDiploma + '\'' +
                ", additionalDiplomas='" + additionalDiplomas + '\'' +
                ", teachingExperience=" + teachingExperience +
                ", certificates='" + certificates + '\'' +
                ", certificateFiles='" + certificateFiles + '\'' +
                ", contract='" + contract + '\'' +
                ", lifeCycle=" + lifeCycle +
                ", type=" + type +
                ", address='" + address + '\'' +
                ", introduction='" + introduction + '\'' +
                ", shortVideo='" + shortVideo + '\'' +
                ", timezone='" + timezone + '\'' +
                ", birthday=" + birthday +
                ", country=" + country +
                ", job='" + job + '\'' +
                ", passport='" + passport + '\'' +
                ", lifePicture1='" + lifePicture1 + '\'' +
                ", lifePicture2='" + lifePicture2 + '\'' +
                ", extraClassSalary=" + extraClassSalary +
                ", overTimeClassSalary=" + overTimeClassSalary +
                ", noShowTime=" + noShowTime +
                ", currency=" + currency +
                ", contractStartDate=" + contractStartDate +
                ", contractEndDate=" + contractEndDate +
                ", notes='" + notes + '\'' +
                ", bankCardNumber='" + bankCardNumber + '\'' +
                ", bankAccountName='" + bankAccountName + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankAddress='" + bankAddress + '\'' +
                ", bankSWIFTCode='" + bankSWIFTCode + '\'' +
                ", payPalAccount='" + payPalAccount + '\'' +
                ", summary='" + summary + '\'' +
                ", referee='" + referee + '\'' +
                ", recruitmentId='" + recruitmentId + '\'' +
                ", hasTested=" + hasTested +
                '}';
    }
}
