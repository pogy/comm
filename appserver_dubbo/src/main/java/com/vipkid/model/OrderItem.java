package com.vipkid.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vipkid.model.json.moxy.OrderAdapter;
import com.vipkid.model.json.moxy.ProductAdapter;
import com.vipkid.model.json.moxy.UnitAdapter;
import com.vipkid.model.util.DBInfo;

/**
 * 订单项
 */
@Entity
@Table(name = "order_item", schema = DBInfo.SCHEMA)
public class OrderItem extends Base {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;
	
	// 订单
	@XmlJavaTypeAdapter(OrderAdapter.class)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
	private Order order;
	
	// 每课时价格
	@Column(name = "class_hour_price", nullable = false)
	private float classHourPrice;
	
	// 课时数
	@Column(name = "class_hour", nullable = false)
	private int classHour;
	
	// 商品
	@XmlJavaTypeAdapter(ProductAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
	private Product product;
	
	// 单元
	@XmlJavaTypeAdapter(UnitAdapter.class)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(name = "order_item_unit", inverseJoinColumns = @JoinColumn(name = "unit_id"), joinColumns = @JoinColumn(name = "order_item_id"))
	private List<Unit> units;
	
	// 商品
	@XmlJavaTypeAdapter(UnitAdapter.class)
	@ManyToOne()
	@JoinColumn(name = "start_unit_id", referencedColumnName = "id")
	private Unit startUnit;
	
	// 原价
	@Column(name = "price", nullable = false)
	private float price;
	
	// 成交价
	@Column(name = "deal_price", nullable = false)
	private float dealPrice;
	
	// 备注
	@Column(name = "comment")
	private String comment;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public float getClassHourPrice() {
		return classHourPrice;
	}

	public void setClassHourPrice(float classHourPrice) {
		this.classHourPrice = classHourPrice;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getDealPrice() {
		return dealPrice;
	}

	public void setDealPrice(float dealPrice) {
		this.dealPrice = dealPrice;
	}

	public int getClassHour() {
		return classHour;
	}

	public void setClassHour(int classHour) {
		this.classHour = classHour;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}

	public Unit getStartUnit() {
		return startUnit;
	}

	public void setStartUnit(Unit startUnit) {
		this.startUnit = startUnit;
	}
}
