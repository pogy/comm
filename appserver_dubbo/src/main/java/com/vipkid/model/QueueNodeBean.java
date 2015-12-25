package com.vipkid.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.DateTimeAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 队列信息
 */
@Entity
@Table(name = "dispatch_queue_node", schema = DBInfo.SCHEMA)
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
public class QueueNodeBean  implements Serializable {
	private static final long serialVersionUID = 9026734792596296173L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "node_name")
	private String nodeName;
	
	@Column(name = "node_type")
	private String nodeType;
	
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "queue_index")
	private int queueIndex;
	
	@Column(name = "auto_assign")
	private Boolean autoAssign;
	
	@Column(name = "counter")
	private Long counter;
	
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_time")
	private Date createTime;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public int getQueueIndex() {
		return queueIndex;
	}

	public void setQueueIndex(int queueIndex) {
		this.queueIndex = queueIndex;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long getUserId) {
		this.userId = getUserId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean isAutoAssign() {
		return autoAssign;
	}

	public void setAutoAssign(Boolean autoAssign) {
		this.autoAssign = autoAssign;
	}

	public Long getCounter() {
		return counter;
	}

	public void setCounter(Long counter) {
		this.counter = counter;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public enum NodeType {
		SALES("SALES"), TMK("TMK");
		
        private String name;
        NodeType(String name) {
        	this.name = name;
        }
        
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
    @Override
    public String toString() {
        return "LeadsQueueNodeBean{" +
                "id=" + id +
                ", nodeName=" + nodeName +
                ", nodeType=" + nodeType +
                ", queueIndex=" + queueIndex +
                ", userId=" + userId +
                ", userName=" + userName +
                ", autoAssign=" + autoAssign +
                ", counter=" + counter +
                ", createTime=" + createTime +
                '}';
    }
}
