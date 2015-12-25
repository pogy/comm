/**
 * 
 */
package com.vipkid.mq.producer.queue;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Stream;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.vipkid.ext.email.EMail;
import com.vipkid.util.JMS;

/**
 * @描述 发送消息到队列
 */
@Component
public class LeadsQueueSender {
	private Logger logger = LoggerFactory.getLogger(LeadsQueueSender.class.getSimpleName());
	
	@Autowired //@Resource
	@Qualifier("jmsQueueTemplate")
	private JmsTemplate jmsTemplate;//通过@Qualifier修饰符来注入对应的bean
	
	@Autowired //@Resource
	@Qualifier("queueDestination")
	private Destination destination;
	
	/**
	 * 向指定队列发送消息
	 */
	public void sendText(final String message) {
		checkParamIsNull(message);
		logger.info("send text message with param: destination = {}, message = {}", destination.toString(), message);
		for(int i=1; i<= JMS.Retry.count; i++) { // 如果成功了则停止，如果失败则尝试再次发送，直到第3次仍失败的话，记error log并发送邮件通知
			try {
				jmsTemplate.send(destination, new MessageCreator() {
			    	@Override
				    public Message createMessage(Session session) throws JMSException {
			    		Message sendMessage = session.createTextMessage(message);
						sendMessage.setJMSType(JMS.Type.LEADS);
						return sendMessage;
				    }
			    });
				return;
			} catch (JmsException e) {
				if(i == JMS.Retry.count) {
					logger.error("fail to send text message with param: destination = {}, message = {}", destination.toString(), message);
					EMail.sendJMSExceptionToRDTeamEmail(destination.toString(), message, e.toString());
					return;
				}
				continue;
			}
		}
		
	}
	
	/**
	 * 发送一条消息到指定的队列（目标）
	 * @param topicName 队列名称
	 * @param message 消息内容
	 */
	public void sendText(String queueName,final String message) {
		checkParamIsNull(queueName, message);
		logger.info("send text message with param: topicName = {}, message = {}", queueName, message);
		for(int i=1; i<= JMS.Retry.count; i++) { // 如果成功了则停止，如果失败则尝试再次发送，直到第3次仍失败的话，记error log并发送邮件通知
			try {
				jmsTemplate.send(queueName, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						Message sendMessage = session.createTextMessage(message);
						sendMessage.setJMSType(JMS.Type.LEADS);
						return sendMessage;
					}
				});
				return;
			} catch (JmsException e) {
				if(i == JMS.Retry.count) {
					logger.error("fail to send text message with param: topicName = {}, message = {}", queueName, message);
					EMail.sendJMSExceptionToRDTeamEmail(queueName, message, e.toString());
					return;
				}
				continue;
			}
		}
	}
	
	public void sendObject(String topicName,final Object object) {
		checkParamIsNull(topicName, object);
		logger.info("send object message with param: topicName = {}, message = {}", topicName, object);
		for(int i=1; i<= JMS.Retry.count; i++) {
			try {
				jmsTemplate.send(topicName, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						ObjectMessage sendMessage = session.createObjectMessage((Serializable) object); // 必需实现序列化
						sendMessage.setJMSType("LeadsBean");
						return sendMessage;
					}
				});
			} catch (JmsException e) {
				if(i == JMS.Retry.count) {
					logger.error("fail to send object message with param: topicName = {}, message = {}", topicName, object);
					EMail.sendJMSExceptionToRDTeamEmail(topicName, object.toString(), e.toString());
					return;
				}
				continue;
			}
		}
	}
	
	
	// map消息
	public void sendMap(String topicName, final Map<String, Object> map) {
		checkParamIsNull(topicName, map);
		logger.info("send map message with param: topicName = {}, message = {}", topicName, map);
		for(int i=1; i<= JMS.Retry.count; i++) {
			try {
				jmsTemplate.send(topicName, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						MapMessage message = session.createMapMessage();
						message.setObject("key", map.get("key"));
						// message.setString("key", (String)map.get("key"));
				        return message;
					}
				});
			} catch (JmsException e) {
				if(i == JMS.Retry.count) {
					logger.error("fail to send map message with param: topicName = {}, message = {}", topicName, map);
					EMail.sendJMSExceptionToRDTeamEmail(topicName, map.toString(), e.toString());
					return;
				}
				continue;
			}
		}
	}
	
	// Bytes消息
	public void sendBytes(String topicName, final byte[] bytes) {
		checkParamIsNull(topicName, bytes);
		logger.info("send bytes message with param: topicName = {}, message = {}", topicName, bytes);
		for(int i=1; i<= JMS.Retry.count; i++) {
			try {
				jmsTemplate.send(topicName, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
				        BytesMessage message = session.createBytesMessage();
				        message.writeBytes(bytes);
				        return message;
					}
				});
			} catch (JmsException e) {
				if(i == JMS.Retry.count) {
					logger.error("fail to send bytes message with param: topicName = {}, message = {}", topicName, bytes);
					EMail.sendJMSExceptionToRDTeamEmail(topicName, bytes.toString(), e.toString());
					return;
				}
				continue;
			}
		}
	}
	
	// Stream消息
	public void sendStream(String topicName, final Stream<?> stream) {
		checkParamIsNull(topicName, stream);
		logger.info("send stream message with param: topicName = {}, message = {}", topicName, stream);
		for(int i=1; i<= JMS.Retry.count; i++) {
			try {
				jmsTemplate.send(topicName, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
				        StreamMessage message = session.createStreamMessage();
				        message.writeString(stream.toString()); // 待测试修改
				        return message;
					}
				});
			} catch (JmsException e) {
				if(i == JMS.Retry.count) {
					logger.error("fail to send stream message with param: topicName = {}, message = {}", topicName, stream);
					EMail.sendJMSExceptionToRDTeamEmail(topicName, stream.toString(), e.toString());
					return;
				}
				continue;
			}
		}
	}
	
	private static void checkParamIsNull(String queueName, Object message) {
		if(queueName == null || message == null) {
			throw new IllegalStateException("param is null."); 
		}
	}
	 
	private static void checkParamIsNull(Object message) {
		if(message == null) {
			throw new IllegalStateException("param is null."); 
		}
	}
}

