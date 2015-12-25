package com.vipkid.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.validator.constraints.NotEmpty;

import com.vipkid.model.json.moxy.FamilyAdapter;
import com.vipkid.model.util.DBInfo;
import com.vipkid.model.validation.ValidateMessages;

/**
 * 家长
 * 用户名为家长手机
 */
@Entity
@Table(name = "parent", schema = DBInfo.SCHEMA)
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
public class Parent extends User {
	private static final long serialVersionUID = 1L;

	@Transient
	private transient Set<Role> roleSet = new HashSet<Role>();
	
	public enum Relation {
		FATHER, // 父亲
		MOTHER, // 母亲
		GRANDFATHER, // 爷爷、姥爷
		GRANDMOTHER, // 奶奶，姥姥
		OTHER_FAMILIES // 其他家人
	}
	
	public enum RegisterFrom {
		WEBSITE, // 网站
		PARENT_PORTAL_WEIXIN,
		PARENT_PORTAL_MOBILE_BROWSER,//在家长端使用微信以外的浏览器注册
		OTHERS, // 其他
		WEIXIN,
		WEB_AIBAIMAMA,
		WEB_JIAZHANGBANG,
		WEB_XIAOXINMAMA,
		M_IBM,
		M_BAIDU,
		M_DELL,
		M_CHUANGXIN,
		M_YOUNG_MBA,
		M_RUIMA,
		M_VIPKID,
		M_VIPKID2,
		M_VIPKID3,
		M_VIPKID4,
		M_VIPKID5,
		M_HALLOWEEN,
		M_CHANGJIANG,
		M_JIAZHANGBANG,
		RECOMMENDER,
		MOBILE_BROWSER,
		WEIBO,
		M_ARTICLE,
		M_HEZI,
		M_JIAZHANGTUIJIAN,
		M_XIAOXINMAMA,
		M_BEVA,
		M_BBYYS,
		M_TNZZ_ENGLAND,
		M_TNZZ_SOUTHAFRICA,
		M_TNZZ_VIDEO,
		M_XIMENGZI,
		M_CHENGSICHENG,
		M_ANQIER,
		M_MAHAOXUAN,
		M_LIHAOYU,
		M_WEIBO2015,
		M_XIAOXINMAMA2,
		M_WANGZIXUAN
	}
	
	// 注册来源
	@Enumerated(EnumType.STRING)
	@Column(name = "register_from")
	private RegisterFrom registerFrom;
	
	// 关系
	@Enumerated(EnumType.STRING)
	@Column(name = "relation")
	private Relation relation;
	
	// 家庭
	@XmlJavaTypeAdapter(FamilyAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "family_id", referencedColumnName = "id", nullable = false)
	private Family family;

	// 头像图片链接
	@Column(name = "avatar")
	private String avatar;

	// 手机
	@NotEmpty(message = ValidateMessages.NOT_EMPTY)
	@Column(name = "mobile", nullable = false)
	private String mobile;

	// 电子邮箱
	@Column(name = "email")
	private String email;
	
	// 单位
	@Column(name = "orgnization")
	private String orgnization;
	
	// 工作
	@Column(name = "job")
	private String job;
	
	// 关注点
	@Lob
	@Column(name = "concern")
	private String concern;
	
	// 微信 Open Id
	@Column(name = "wechat_open_id")
	private String wechatOpenId;
	
	// 决策人
	@Column(name = "decision_maker")
	private boolean decisionMaker;
	
	// 密码验证码
	@Column(name = "verify_code")
	private String verifyCode;
	
//	@ManyToOne(cascade = CascadeType.REFRESH)
//	@JoinColumn(name = "channel_id", referencedColumnName = "id")
//	private Channel channel;

	//渠道id
	@Column(name = "channel_id")
	private long channel_id;
	
	@Column(name="channel_keyword")
	private String channelKeyword="";
	
	

	//推荐码
	@Column(name = "recommend_code")
	private String recommendCode = "";
	
	public String getRecommendCode() {
		return recommendCode;
	}

	public void setRecommendCode(String recommendCode) {
		this.recommendCode = recommendCode;
	}

	@PrePersist
	public void prePersist() {
		super.prePersist();
		roles = Role.PARENT.name();
	}
	
	@PreUpdate
	public void preUpdate() {
		super.preUpdate();
	}
	
	public Parent() {
		roleSet.add(Role.PARENT);
	}
    
	@Override
	public Set<Role> getRoleSet() {
		return roleSet;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWechatOpenId() {
		return wechatOpenId;
	}

	public void setWechatOpenId(String wechatOpenId) {
		this.wechatOpenId = wechatOpenId;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
		// 可能会引起JPA对象池死锁
//		List<Parent> parents = this.family.getParents();
//		if (parents == null) {
//			parents = new ArrayList<Parent>();
//		}
//		
//		if(!parents.contains(this)){
//			parents.add(this);
//			this.family.setParents(parents);
//		}
	}

	public String getOrgnization() {
		return orgnization;
	}

	public void setOrgnization(String orgnization) {
		this.orgnization = orgnization;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getConcern() {
		return concern;
	}

	public void setConcern(String concern) {
		this.concern = concern;
	}

	public void setRoleSet(Set<Role> roleSet) {
		this.roleSet = roleSet;
	}

	public boolean isDecisionMaker() {
		return decisionMaker;
	}

	public void setDecisionMaker(boolean decisionMaker) {
		this.decisionMaker = decisionMaker;
	}
	
	public RegisterFrom getRegisterFrom() {
		return registerFrom;
	}

	public void setRegisterFrom(RegisterFrom registerFrom) {
		this.registerFrom = registerFrom;
	}

	public long getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(long channel_id) {
		this.channel_id = channel_id;
	}
	
	public String getChannelKeyword() {
		return channelKeyword;
	}

	public void setChannelKeyword(String channelKeyword) {
		this.channelKeyword = channelKeyword;
	}
	
//	public Channel getChannel() {
//		return channel;
//	}
//
//	public void setChannel(Channel channel) {
//		this.channel = channel;
//	}
}
