package com.vipkid.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vipkid.model.Order;
import com.vipkid.model.Order.PayBy;
import com.vipkid.model.Order.Status;
import com.vipkid.model.OrderItem;
import com.vipkid.rest.vo.query.OrderQueryOrderItemView;
import com.vipkid.rest.vo.query.OrderQueryOrderView;
import com.vipkid.rest.vo.query.OrderQueryProductView;
import com.vipkid.rest.vo.query.OrderQueryStudentView;
import com.vipkid.rest.vo.query.OrderQueryUnitView;
import com.vipkid.service.OrderService;
import com.vipkid.service.exception.BadRequestServiceException;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.DoubleVo;

@RestController
@RequestMapping("/api/service/private/orders")
public class OrderController {
	private Logger logger = LoggerFactory.getLogger(OrderController.class.getSimpleName());

	@Resource
	private OrderService orderService;
	
	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public Order find(@RequestParam("id") long id) {
		logger.info("find product for id = {}", id);
		return orderService.find(id);
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Order> list(
			@RequestParam(value = "serialNumber", required = false) String serialNumber, 
			@RequestParam(value = "search", required = false) String search, 
			@RequestParam(value = "status", required = false) Status status, 
			@RequestParam(value = "payBy", required = false) PayBy payBy,
			@RequestParam(value = "salesIds", required = false) Long[] salesIds,
			@RequestParam(value = "fromDate", required = false) DateTimeParam fromDate, 
			@RequestParam(value = "toDate", required = false) DateTimeParam toDate, 
			@RequestParam(value = "start", required = false) int start, 
			@RequestParam(value = "length", required = false) int length) {
		logger.info("list order with params: serialNumber = {}, search = {}, status = {}, payBy = {}, salesIds = {}, fromDate = {}, toDate = {}, start = {}, length = {}.", serialNumber, search, status, payBy, salesIds, fromDate, toDate, start, length);
		return orderService.list(serialNumber, search, status,  payBy != null ? Arrays.asList(payBy) : null, salesIds != null ? Arrays.asList(salesIds) : null, fromDate, toDate, start, length);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	public Count count(@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "serialNumber", required = false) String serialNumber,
			@RequestParam(value = "status", required = false) Status status, 
			@RequestParam(value = "payBy", required = false) PayBy[] payBy,
			@RequestParam(value = "salesIds", required = false) Long[] salesIds,
			@RequestParam(value = "fromDate", required = false) DateTimeParam fromDate, 
			@RequestParam(value = "toDate", required = false) DateTimeParam toDate) {
		logger.info("count order with params: serialNumber = {}, search = {}, status = {}, payBy = {}, salesIds = {}, fromDate = {}, toDate = {}.",serialNumber, search, status, payBy, salesIds, fromDate, toDate);
		return orderService.count(serialNumber,search,status,  payBy != null ? Arrays.asList(payBy) : null, salesIds != null ? Arrays.asList(salesIds) : null, fromDate, toDate);
	}
	
	@RequestMapping(value = "/countTotalDealPrice", method = RequestMethod.GET)
	public DoubleVo countTotalDealPrice(@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "serialNumber", required = false) String serialNumber,
			@RequestParam(value = "status", required = false) Status status, 
			@RequestParam(value = "payBy", required = false) PayBy[] payBy,
			@RequestParam(value = "salesIds", required = false) Long[] salesIds,
			@RequestParam(value = "fromDate", required = false) DateTimeParam fromDate, 
			@RequestParam(value = "toDate", required = false) DateTimeParam toDate) {
		logger.info("countTotalDealPrice order with params: serialNumber = {}, search = {}, status = {}, payBy = {}, salesIds = {}, fromDate = {}, toDate = {}.",serialNumber, search, status, payBy, salesIds, fromDate, toDate);
		return orderService.countTotalDealPrice(serialNumber,search,status,  payBy != null ? Arrays.asList(payBy) : null, salesIds != null ? Arrays.asList(salesIds) : null, fromDate, toDate);
	}

	@RequestMapping(value = "/findByFamilyId", method = RequestMethod.GET)
	public List<Order> findByFamilyId(@RequestParam("familyId") long familyId){
		logger.info("findByFamilyid order with params: familyId = {}", familyId);
		return orderService.findByFamilyId(familyId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public Order create(@RequestBody Order order) {
		logger.info("create order: {}", order);
		return orderService.create(order);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public Order update(@RequestBody Order order) {
		logger.info("update order: {}", order);
		return orderService.update(order);
	}

    /**
     * 转账确认订单
     * @param order
     * @return
     */
    @RequestMapping(value = "/transferConfirm", method = RequestMethod.POST)
    public Order transferConfirm(@RequestBody Order order) {
        logger.info("transfer confirm order is: {}", order);
        return orderService.transferConfirm(order);
    }
	
	@RequestMapping(value = "/confirm", method = RequestMethod.PUT)
	public Order confirm(@RequestBody Order order){
		logger.info("update order: {}", order);
		return orderService.doConfirm(order);
	}
	
	
	@RequestMapping(value = "/cancel", method = RequestMethod.PUT)
	public Order cancel(@RequestBody Order order) {
		logger.info("cancel order: {}", order);
		return orderService.cancel(order);
	}
	
	
	// 2015-01-30 获取学生的order list
	@RequestMapping(value = "/findByStudentId", method = RequestMethod.GET)
	public List<Order> findByStudentId(@RequestParam("studentId") long studentId){
		logger.info("findByStudent order with params: studentId = {}", studentId);
		return orderService.findByStudentId(studentId);
	}
	
	
	@RequestMapping(value = "/confirmFromQueryPage", method = RequestMethod.PUT)
	public Order confirmFromQueryPage(@RequestBody Order order){
		logger.info("confirm order from QueryPage orderId: {}", order == null ? "" : order.getId());
		
		Order orderPo = null;
		if (order != null) {
			orderPo = orderService.find(order.getId());
		} else {
			throw new BadRequestServiceException("order can not be null.");
		}
		
		if (orderPo == null) { 
			throw new BadRequestServiceException("order does not exists,orderId = " + order.getId());
		}
		
		 orderPo.setPayBy(order.getPayBy());
		 orderPo.setPayer(order.getPayer());

		 orderService.doConfirm(orderPo);
		 return null;
	}
	
	@RequestMapping(value = "/filter", method = RequestMethod.GET)
	public List<OrderQueryOrderView> filter(
			@RequestParam(value = "serialNumber", required = false) String serialNumber, 
			@RequestParam(value = "search", required = false) String search, 
			@RequestParam(value = "status", required = false) Status status, 
			@RequestParam(value = "payBy", required = false) PayBy[] payBy, 
			@RequestParam(value = "salesIds", required = false) Long[] salesIds, 
			@RequestParam(value = "fromDate", required = false) DateTimeParam fromDate, 
			@RequestParam(value = "toDate", required = false) DateTimeParam toDate, 
			@RequestParam(value = "start", required = false) int start, 
			@RequestParam(value = "length", required = false) int length) {
		logger.info("list order with params: serialNumber = {}, search = {}, status = {}, payBy = {}, salesIds = {}, fromDate = {}, toDate = {}, start = {}, length = {}.", serialNumber, search, status, payBy, salesIds, fromDate, toDate, start, length);
		List<Order> orderList =  orderService.list(serialNumber, search, status, payBy != null ? Arrays.asList(payBy) : null, salesIds != null ? Arrays.asList(salesIds) : null, fromDate, toDate, start, length);
		return this.getOrderQueryResultView(orderList);
	}
	
	private List<OrderQueryOrderView> getOrderQueryResultView(List<Order> orderList) {
		
		List<OrderQueryOrderView> orderviewList = new ArrayList<OrderQueryOrderView>();
		if (orderList != null && orderList.size() > 0) {
			Iterator<Order> iterator = orderList.iterator();
			while (iterator.hasNext()) {
				Order order = iterator.next();
				OrderQueryOrderView orderView = new OrderQueryOrderView();
				
				//student
				OrderQueryStudentView studentView = null;
				if (order.getStudent() != null) {
					studentView = new OrderQueryStudentView();
					studentView.setId(order.getStudent().getId());
					studentView.setName(order.getStudent().getName());
					studentView.setEnglishName(order.getStudent().getEnglishName());
				}
				
				//orderItems
				List<OrderQueryOrderItemView> orderItemViews = new ArrayList<OrderQueryOrderItemView>();
				if (order.getOrderItems() != null  && order.getOrderItems().size() > 0) {
					for (OrderItem orderItem : order.getOrderItems()) {
						OrderQueryOrderItemView orderItemView = new OrderQueryOrderItemView();
						
						//startUnit
						OrderQueryUnitView startUnitView = null;
						if (orderItem.getStartUnit() != null) {
							startUnitView = new OrderQueryUnitView();
							startUnitView.setId(orderItem.getStartUnit().getId());
							startUnitView.setSerialNumber(orderItem.getStartUnit().getSerialNumber());
						}
						
						//product
						OrderQueryProductView productView = null;
						if (orderItem.getProduct() != null) {
							productView = new OrderQueryProductView();
							productView.setId(orderItem.getProduct().getId());
							productView.setName(orderItem.getProduct().getName());
						}
						
						orderItemView.setId(orderItem.getId());
						orderItemView.setClassHour(orderItem.getClassHour());
						orderItemView.setDealPrice(orderItem.getDealPrice());
						orderItemView.setPrice(orderItem.getPrice());
						orderItemView.setStartUnit(startUnitView);
						orderItemView.setProduct(productView);
						
						orderItemViews.add(orderItemView);
						
					}
				}
				
				orderView.setId(order.getId());
				orderView.setSerialNumber(order.getSerialNumber());
				orderView.setStatus(order.getStatus());
                orderView.setCreateDateTime(order.getCreateDateTime());
                orderView.setPaidDateTime(order.getPaidDateTime());
				orderView.setPayBy(order.getPayBy());
				orderView.setStudent(studentView);
				orderView.setOrderItems(orderItemViews);
				
				orderviewList.add(orderView);
				
			}
		}

		return orderviewList;
	}
	
}
