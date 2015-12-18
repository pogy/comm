package com.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class DisruptorUtils {
	
	private static Disruptor<LongEvent> disruptor = null;
	
	private static ExecutorService executor = null;
	
	private static int ringBufferSize = 1024 * 1024; // RingBuffer 大小，必须是 2 的 N 次方；
	
	/**
	 * 7.启动
	 */
	@SuppressWarnings("unchecked")
	public static void start(){
		//单个线程
		executor = Executors.newSingleThreadExecutor();
		disruptor = new Disruptor<LongEvent>(
						new LongEventFactory(),		//1
		                ringBufferSize, 			//2
		                executor,					//3
		                ProducerType.SINGLE,		//4
		                new YieldingWaitStrategy() 	//5
						);
		EventHandler<LongEvent> eventHandler = new LongEventHandler();
		disruptor.handleEventsWith(eventHandler);
		disruptor.start();
	}
	
	/**
	 * 8. 生产发布
	 */
	public static void produce(String message){
		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
		long sequence = ringBuffer.next();//请求下一个事件序号；
		try {
		    LongEvent event = ringBuffer.get(sequence);//获取该序号对应的事件对象；
		    event.setMessage(message);
		} finally{
		    ringBuffer.publish(sequence);//发布事件；
		}
	}
	
	/**
	 * 8. 发布2
	 */
	private static class Translator implements EventTranslatorOneArg<LongEvent, String>{
	    public void translateTo(LongEvent event, long sequence, String message) {
	        event.setMessage(message);
	    }    
	}
	private static Translator TRANSLATOR = new Translator();
	public static void produce2(String message) {
	    // 发布事件；
	    RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
	    ringBuffer.publishEvent(TRANSLATOR, message);
	}

	
	/**
	 * 9.关闭
	 */
	public static void shutdown(){
		
		disruptor.shutdown();//关闭 disruptor，方法会堵塞，直至所有的事件都得到处理；
		
		executor.shutdown();//关闭 disruptor 使用的线程池；如果需要的话，必须手动关闭， disruptor 在 shutdown 时不会自动关闭；
	}
}
