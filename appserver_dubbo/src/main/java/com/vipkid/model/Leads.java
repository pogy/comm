package com.vipkid.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vipkid.model.util.DBInfo;

/**
 * Created by zfl on 2015/6/11.
 */
@Entity
@Table(name = "leads", schema = DBInfo.SCHEMA)
public class Leads  extends Base {
    private static final long serialVersionUID = 1L;
    
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "create_time")
    private Date createTime;//分配时间
    
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "register_time")
	private Date registerTime;//学生注册时间
	
	@Column(name = "student_id")
    private Long studentId;//学生ID
	
	@Column(name = "family_id")
    private Long familyId;
	
	@Column(name = "sales_id")
    private Long salesId;
	
	@Column(name = "sales_name")
    private String salesName;
    
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sales_assign_time")
    private Date salesAssignTime;
    
    @Column(name = "tmk_id")
    private Long tmkId;
    
    @Column(name = "tmk_name")
    private String tmkName;
    
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "tmk_assign_time")
    private Date tmkAssignTime;
	
	@Column(name = "owner_type")
	private int ownerType;
    
    @Column(name = "channel")
    private String channel;
    
    @Column(name = "lead_type")
    private char leadType;
    
    @Column(name = "locked_time")
    private Date lockedTime;
    
    @Column(name = "locked")
    private boolean locked;
    
    @Column(name = "status")
    private int status;//参考枚举status
    
    @Column(name = "is_library")
    private boolean isLibrary;
    
    @Column(name = "is_contact")
    private boolean isContact;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modify_time")
    private Date lastModifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public Long getSalesId() {
        return salesId;
    }

    public void setSalesId(Long salesId) {
        this.salesId = salesId;
    }

    public Long getTmkId() {
        return tmkId;
    }

    public void setTmkId(Long tmkId) {
        this.tmkId = tmkId;
    }

    public String getSalesName() {
        return salesName;
    }

    public void setSalesName(String salesName) {
        this.salesName = salesName;
    }

    public Date getSalesAssignTime() {
		return salesAssignTime;
	}

	public void setSalesAssignTime(Date salesAssignTime) {
		this.salesAssignTime = salesAssignTime;
	}

    public String getTmkName() {
        return tmkName;
    }

    public void setTmkName(String tmkName) {
        this.tmkName = tmkName;
    }

	public Date getTmkAssignTime() {
		return tmkAssignTime;
	}

	public void setTmkAssignTime(Date tmkAssignTime) {
		this.tmkAssignTime = tmkAssignTime;
	}

    public int getOwnerType() {
		return ownerType;
	}

	public void setOwnerType(int ownerType) {
		this.ownerType = ownerType;
	}

	public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public char getLeadType() {
        return leadType;
    }

    public void setLeadType(char leadType) {
        this.leadType = leadType;
    }

    public Date getLockedTime() {
        return lockedTime;
    }

    public boolean isLibrary() {
		return isLibrary;
	}

	public void setLibrary(boolean isLibrary) {
		this.isLibrary = isLibrary;
	}

	public boolean isContact() {
		return isContact;
	}

	public void setContact(boolean isContact) {
		this.isContact = isContact;
	}

	public void setLockedTime(Date lockedTime) {
        this.lockedTime = lockedTime;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    
    public Date getLastModifyTime() {
		return lastModifyTime;
	}

	public void setLastModifyTime(Date lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}


	public enum Status {
        DEFAULT (-1,""),
        ASSIGNED(0,"已分配"),
        CONTACTED(1,"已联系"),
        BOOKEDTRIAL(2,"约到trail课"),
        PAYED(3,"已支付");

        private int code;
        private String desc;

        Status(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
 
	public enum OwnerType {
        DEFAULT (-1,""),
        STAFF_SALES_DIRECTOR(0,"STAFF_SALES_DIRECTOR"),
        STAFF_SALES(1,"STAFF_SALES"),
        STAFF_TMK(2,"STAFF_TMK");
        
        private int code;
        private String desc;

        OwnerType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }
	
	@Override
    public String toString() {
        return "LeadsBean{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", registerTime=" + registerTime +
                ", studentId=" + studentId +
                ", familyId=" + familyId +
                ", salesId=" + salesId +
                ", tmkId=" + tmkId +
                ", salesName=" + salesName +
                ", salesAssignTime=" + salesAssignTime +
                ", tmkName='" + tmkName + '\'' +
                ", tmkAssignTime='" + tmkAssignTime +
                ", channel='" + channel + '\'' +
                ", leadType=" + leadType +
                ", lockedTime=" + lockedTime +
                ", locked=" + locked +
                ", status=" + status +
                '}';
    }
}
