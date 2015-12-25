package com.vipkid.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vipkid.ext.email.EMail;
import com.vipkid.ext.sms.yunpian.SMS;
import com.vipkid.ext.sms.yunpian.SendSMSResponse;
import com.vipkid.model.Audit.Category;
import com.vipkid.model.Audit.Level;
import com.vipkid.model.Course;
import com.vipkid.model.Leads;
import com.vipkid.model.LearningProgress;
import com.vipkid.model.Lesson;
import com.vipkid.model.OnlineClass;
import com.vipkid.model.Order;
import com.vipkid.model.Order.PayBy;
import com.vipkid.model.Order.Status;
import com.vipkid.model.OrderItem;
import com.vipkid.model.Parent;
import com.vipkid.model.Product;
import com.vipkid.model.Staff;
import com.vipkid.model.Student;
import com.vipkid.model.User;
import com.vipkid.repository.LeadsRepository;
import com.vipkid.repository.LearningProgressRepository;
import com.vipkid.repository.LessonRepository;
import com.vipkid.repository.OnlineClassRepository;
import com.vipkid.repository.OrderItemRepository;
import com.vipkid.repository.OrderRepository;
import com.vipkid.repository.StaffRepository;
import com.vipkid.repository.StudentRepository;
import com.vipkid.security.SecurityService;
import com.vipkid.service.exception.BadRequestServiceException;
import com.vipkid.service.param.DateTimeParam;
import com.vipkid.service.pojo.Count;
import com.vipkid.service.pojo.DoubleVo;
import com.vipkid.util.Configurations;

@Service("orderService")
public class OrderService {
    private Logger logger = LoggerFactory.getLogger(OrderService.class.getSimpleName());

    @Resource
    private OrderRepository orderRepository;

    @Resource
    private OrderItemRepository orderItemRepository;

    @Resource
    private LearningProgressRepository learningProgressRepository;

    @Resource
    private StudentRepository studentRepository;

    @Resource
    private LessonRepository lessonRepository;

    @Resource
    private SecurityService securityService;
    
    @Resource
    private LeadsRepository leadsRepository;
    
    @Resource
    private StudentLifeCycleLogService studentLifeCycleLogService;

    @Resource
    private OnlineClassRepository onlineClassRepository;
	@Resource
	private LeadsManageService leadsManageService;
	@Resource
	private StaffRepository staffRepository;
	@Resource
	BillNoService billNoService;
	
    public Order find(long id) {
        logger.info("find product for id = {}", id);
        return orderRepository.find(id);
    }

    public List<Order> list(String serialNumber, String search, Status status, List<PayBy> payBy, List<Long> salesIds, DateTimeParam fromDate, DateTimeParam toDate, int start, int length) {
        logger.info("list order with params: serialNumber = {}, search = {}, status = {}, payBy = {}, salesIds = {}, start = {}, length = {}.", serialNumber, search, status, payBy, salesIds, fromDate, toDate, start, length);
        return orderRepository.list(serialNumber, search, status, payBy, salesIds, fromDate, toDate, start, length);
    }

    public Count count(String serialNumber, String search, Status status, List<PayBy> payBy, List<Long> salesIds, DateTimeParam fromDate, DateTimeParam toDate) {
        logger.info("count order with params: serialNumber = {}, search = {}, status = {}, payBy = {}, salesIds = {}, fromDate = {}, toDate = {}.", serialNumber, search, status, payBy, salesIds, fromDate, toDate);
        return new Count(orderRepository.count(serialNumber, search, status, payBy, salesIds, fromDate, toDate));
    }
    public DoubleVo countTotalDealPrice(String serialNumber, String search, Status status, List<PayBy> payBy, List<Long> salesIds, DateTimeParam fromDate, DateTimeParam toDate) {
    	logger.info("countTotalDealPrice order with params: serialNumber = {}, search = {}, status = {}, payBy = {}, salesIds = {}, fromDate = {}, toDate = {}.", serialNumber, search, status, payBy, salesIds, fromDate, toDate);
    	return new DoubleVo(orderRepository.countTotalDealPrice(serialNumber, search, status, payBy, salesIds, fromDate, toDate));
    }

    public List<Order> findByFamilyId(long familyId) {
        logger.info("findByFamilyid order with params: familyId = {}", familyId);
        return orderRepository.findByFamilyId(familyId);
    }
    public List<Order> listOrderByFamilyId(long familyId,Integer rowNum,Integer currNum) {
        logger.info("listOrderByFamilyId order with params: familyId = {}", familyId);
        return orderRepository.listOrderByFamilyId(familyId, rowNum, currNum);
    }
    public long countOrderByFamilyId(long familyId) {
        logger.info("countOrderByFamilyId order with params: familyId = {}", familyId);
        return orderRepository.countOrderByFamilyId(familyId);
    }

	public Order create(Order order) {
		Student student = null;
		if (order != null && order.getStudent() != null) {
			student = studentRepository.find(order.getStudent().getId());
		} else {
			throw new BadRequestServiceException("order or student is null");
		}
		
		if (student == null) {
			throw new BadRequestServiceException("can not find student");
		}
		
		order.setFamily(student.getFamily());
		
		
		try {
			logger.info("create order: {}", order);
			order.setCreater(securityService.getCurrentUser());

			float totalDealPrice = 0;
			for (OrderItem orderItem : order.getOrderItems()) {
				totalDealPrice += orderItem.getDealPrice();
			}
			order.setTotalDealPrice(totalDealPrice);

			order.setStatus(Status.TO_PAY);
			order.setCreateDateTime(new Date());
			//order.setSerialNumber(UUID.randomUUID().toString());
			order.setSerialNumber(billNoService.doGetNextOrderNo());
			
			order = orderRepository.create(order);

			for (OrderItem orderItem : order.getOrderItems()) {
				orderItem.setOrder(order);
				orderItemRepository.create(orderItem);
			}

			order = orderRepository.update(order);

			// 发送订单支付短信提醒, 如果订单金额大于0，就发短信，否则就不发
			if (totalDealPrice > 0) {
				User currentUser = securityService.getCurrentUser();
				String staffName = currentUser.getName();
				if (currentUser instanceof Staff) {
					Staff currentStaff = (Staff) currentUser;
					staffName = currentStaff.getEnglishName();
				}
				String studentName = order.getStudent().getSafeName();

				List<Parent> parents = order.getFamily().getParents();
				for (Parent parent : parents) {
					String mobile = parent.getMobile();
					logger.info("parent mobile is " + mobile);
					SendSMSResponse sendSMSResponse = SMS.sendOrderPaymentSMS(mobile, staffName, studentName);
					if (sendSMSResponse.isSuccess()) {
						logger.info("send order payment remind succeed !");
					} else {
						logger.info("send order payment remind failed !");
					}
				}
			} else {
				//如果0元订单，直接confirm
				doConfirm(order);
			}

			securityService.logAudit(Level.INFO, Category.ORDER_CREATE, "Create order: " + order.getSerialNumber());

			return order;
		} catch (Exception e) {
			securityService.logAudit(Level.ERROR, Category.ORDER_UPDATE, "Update order: " + e.getMessage());
			logger.error("###################### Got order errors :" + e.getMessage());
		}
		return null;
	}

    public Order update(Order order) {
        logger.info("update order: {}", order);

        orderRepository.update(order);

        securityService.logAudit(Level.INFO, Category.ORDER_UPDATE, "Update order: " + order.getSerialNumber());

        return order;
    }

    /**
     * 家长转账确认
     * @param order
     * @return
     */
    public Order transferConfirm(Order order) {
        if (null == order) {
            return order;
        }
        logger.info("Parent confirm order by transfer,order id is : {}", order.getId());
        order.setPaidDateTime(new Date());
        order.setStatus(Status.PAID);
        order.setPayBy(PayBy.TRANSFER);
        order.setOnlinePayFailed(false);
        order = orderRepository.update(order);
        securityService.logAudit(Level.INFO, Category.ORDER_UPDATE, "Parent confirm order by transfer,order's serialNumber is : " + order.getSerialNumber());
        return order;
    }

    public Order doConfirm(Order order) {
        if (null == order) {
            return order;
        }
        logger.info("order confirm,orderID={},orderStatus={}",order.getId(),order.getStatus());
        if (order.getStatus() == Status.PAY_CONFIRMED) {
            return order;
        }
        order.setStatus(Status.PAY_CONFIRMED);
        order.setPaidDateTime(new Date());
        logger.info("order confirm,payBy={}",order.getPayBy());
        if (order.getPayBy() != PayBy.ALIPAY || order.getPayBy() != PayBy.WECHATPAY || order.getPayBy() != PayBy.UNIONPAY) {
            order.setConfirmer(securityService.getCurrentUser());
        }
        int countClassHour = 0;

        boolean isMajor = false;
        for (OrderItem orderItem : order.getOrderItems()) {
        	Lesson nextShouldTakeLesson = null;
        	if (orderItem.getStartUnit() != null) {
        		nextShouldTakeLesson = lessonRepository.findFirstByUnitId(orderItem.getStartUnit().getId());
        	}
            LearningProgress tempLearningProgress = learningProgressRepository.findByStudentIdAndCourseId(order.getStudent().getId(), orderItem.getProduct().getCourse().getId());
            if (tempLearningProgress == null) {
                logger.info("Confirm order,create new LearningProgress,orderID={}",order.getId());
                // 新建新生指导课学习进度
//				Course guideCourse = courseAccessor.findByCourseType(Type.GUIDE);
//				LearningProgress guideLearningProgress = new LearningProgress();
//				guideLearningProgress.setStudent(order.getStudent());
//				guideLearningProgress.setStatus(com.vipkid.pojo.LearningProgress.Status.STARTED);
//				guideLearningProgress.setCourse(guideCourse);
//				guideLearningProgress.setLeftClassHour(1);
//				guideLearningProgress.setTotalClassHour(1);
//				learningProgressAccessor.create(guideLearningProgress);

                LearningProgress learningProgress = new LearningProgress();
                learningProgress.setStudent(order.getStudent());
                learningProgress.setCourse(orderItem.getProduct().getCourse());
                learningProgress.setLeftClassHour(orderItem.getClassHour());
                learningProgress.setTotalClassHour(orderItem.getClassHour());
                learningProgress.setProductId(orderItem.getProduct().getId());
                if (orderItem.getProduct().getCourse().getType().equals(Course.Type.MAJOR)) {
                    countClassHour += orderItem.getClassHour();
                }

                learningProgress.setStatus(LearningProgress.Status.STARTED);
                if (learningProgress.getCourse().isSequential()) {
                    learningProgress.setStartUnit(orderItem.getStartUnit());
                    learningProgress.setNextShouldTakeLesson(nextShouldTakeLesson);
                }
                learningProgressRepository.create(learningProgress);
            } else {
                logger.info("Confirm order,update LearningProgress,orderID={},LearningProgressID={}",order.getId(),tempLearningProgress.getId());
                int leftClassHour = tempLearningProgress.getLeftClassHour() + orderItem.getClassHour();
                int totalClassHour = tempLearningProgress.getTotalClassHour() + orderItem.getClassHour();
                tempLearningProgress.setLeftClassHour(leftClassHour);
                tempLearningProgress.setTotalClassHour(totalClassHour);
                tempLearningProgress.setProductId(orderItem.getProduct().getId());
                tempLearningProgress.setStatus(LearningProgress.Status.STARTED);
                learningProgressRepository.update(tempLearningProgress);
                logger.info("Confirm order,update LearningProgress success,orderID={},LearningProgressID={}",order.getId(),tempLearningProgress.getId());
                if (orderItem.getProduct().getCourse().getType().equals(Course.Type.MAJOR)) {
                    countClassHour += orderItem.getClassHour();
                }
            }
            
            if (orderItem.getProduct().getCourse().getType().equals(Course.Type.MAJOR)) {
            	isMajor = true;
        }
        }

        Student orderedStudent = studentRepository.find(order.getStudent().getId());
        boolean isOrderNotFree = false;
        for (OrderItem orderItem : order.getOrderItems()) {
        	if (orderItem.getProduct().getType() == Product.Type.PAID) {
        		isOrderNotFree = true;
        	}
        }
        if (isOrderNotFree) {
        	studentLifeCycleLogService.doChangeLifeCycle(orderedStudent, orderedStudent.getLifeCycle(), Student.LifeCycle.LEARNING);
        }
        
        //12次60 天
        //36次 120天
        //72次  240
        //144   420
        //主修课   才算课时
        //支付金额  >0
        if (order.getTotalDealPrice() > 0 && countClassHour >= 12) {
            long count = 0;
            if (countClassHour > 12 && countClassHour < 36) {
                count = 60;
            } else if (countClassHour >= 36 && countClassHour < 72) {
                count = 120;
            } else if (countClassHour >= 72 && countClassHour < 44) {
                count = 240;
            } else if (countClassHour >= 144) {
                count = 420;
            }
            
            Long Interval=24L*60L*60L*1000L*count;
            if (orderedStudent.getPreContractEndTime() != null) {
                Date endDateTime = new Date(orderedStudent.getPreContractEndTime().getTime() + Interval);
                order.setContractEndTime(endDateTime);
                orderedStudent.setPreContractEndTime(endDateTime);
            } else {
                Date endDateTime = new Date(new Date().getTime() + Interval);
                order.setContractEndTime(endDateTime);
                orderedStudent.setPreContractEndTime(endDateTime);
            }
        }

        orderRepository.update(order);
        logger.info("Confirm order,update order success,orderID={}",order.getId());
        studentRepository.update(orderedStudent);
        logger.info("studentRepository success,studentID={}",orderedStudent.getId());
       
		if (isOrderNotFree && isMajor && order.getTotalDealPrice() > Configurations.ALIPAY.MIN_AMOUNT) {
			logger.info("onlineClassRepository findLastedFinishedTailClassByStudentId,studentID={}",orderedStudent.getId());
			OnlineClass findTrailOnlineClass = onlineClassRepository.findLastedFinishedTailClassByStudentId(orderedStudent.getId());
			if (findTrailOnlineClass != null) {
				findTrailOnlineClass.setPaidTrail(true);
				logger.info("fount last trail lesson  ,update order success,orderID={}", order.getId());
				onlineClassRepository.update(findTrailOnlineClass);
			}else{
				findTrailOnlineClass = onlineClassRepository.findLastedTailClassByStudentId(orderedStudent.getId());
				if (findTrailOnlineClass != null) {
					findTrailOnlineClass.setPaidTrail(true);
					logger.info("fount last trail lesson  ,update order success,orderID={}", order.getId());
					onlineClassRepository.update(findTrailOnlineClass);
				}
			}
			logger.info("onlineClassRepository findLastedFinishedTailClassByStudentId,studentID={} ok",orderedStudent.getId());
		}
		
        

        //向家长发送收款成功确认短信，如果是零元就不用发短信了
        if (order.getTotalDealPrice() > 0) {
            String studentName = order.getStudent().getSafeName();
            List<Parent> parents = order.getFamily().getParents();
            for (Parent parent : parents) {
                String mobile = parent.getMobile();
                logger.info("send payment success sms for " + mobile);
                SendSMSResponse sendSMSResponse = SMS.sendPaymentSuccessAckedSMS(mobile, studentName);
                if (sendSMSResponse.isSuccess()) {
                    logger.info("send order payment remind succeed !");
                } else {
                    logger.info("send order payment remind failed !");
                }
            }
            
            //send sms to creater
            User u = order.getCreater();
            if (u != null) {
            	Staff creater = staffRepository.find(u.getId());
            	if (creater != null) {
            		sendPaymentSuccessSMSToOrderCreater(creater, studentName);
            	}
            }
            
        }
        
		try {//更新leads状态
			if (isMajor) {
				leadsManageService.updateLeadsStatus(order.getStudent().getId(), order.getCreater().getId(), Leads.Status.PAYED.getCode());
			}
		} catch(Exception e) {
			logger.error("error when update leads status,orderId = {}",order.getId());
		}
		
        securityService.logAudit(Level.INFO, Category.ORDER_CONFIRM, "Confirm order: " + order.getSerialNumber());
        return order;
    }

    private void sendPaymentSuccessSMSToOrderCreater(Staff creater ,String studentName) {
    	if (creater != null) {
    		String mobile = creater.getMobile();
    		if (StringUtils.isNotBlank(mobile)) {
                logger.info("send payment success sms for order creater,createrId = {}, mobile = {}, studentName = {}",
                		creater.getId(), mobile, studentName);
                SendSMSResponse sendSMSResponse = SMS.sendPaymentSuccessAckedSMS(mobile, studentName);
                if (sendSMSResponse.isSuccess()) {
                    logger.info("send order payment remind succeed !,mobile = {}", mobile);
                } else {
                    logger.info("send order payment remind failed !,mobile = {}", mobile);
                }
    		} else {
				logger.warn(" mobile  is not exists ,unable to send order payment remind sms,createrId = {},studentName = {} ",
						creater.getId(), studentName);
    		}
    	}
    
    }
    
    public Order cancel(Order order) {
        logger.info("cancel order: {}", order);
        checkParameters(order);
        Order dbOrder = orderRepository.find(order.getId());
		dbOrder.setCanceledDateTime(order.getCanceledDateTime());
		dbOrder.setStatus(order.getStatus());
		orderRepository.update(dbOrder);

        //发送取消订单邮件
        User currentUser = securityService.getCurrentUser();
        if (currentUser instanceof Parent) {
            Parent parent = (Parent) currentUser;
            EMail.sendCancelOrderEmail(parent.getName(), dbOrder.getSerialNumber(), parent.getMobile());
        }

        securityService.logAudit(Level.INFO, Category.ORDER_CANCEL, "Cancel order: " + dbOrder.getSerialNumber());

        return dbOrder;
    }


    // 2015-01-30 获取学生的order list
    public List<Order> findByStudentId(long studentId) {
        logger.info("findByStudent order with params: studentId = {}", studentId);
        return orderRepository.findByStudentId(studentId);
    }

    public Order findBySerialNumber(String serialNumber) {
        logger.info("find By SerialNumber,serialNumber = {}.", serialNumber);
        return orderRepository.findBySerialNumber(serialNumber);
    }
    
    private void checkParameters(final Order order){
		if (order == null || order.getId() == 0){
			throw new IllegalStateException("Order is invalidated!");
		}
	}

}
