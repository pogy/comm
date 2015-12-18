package com.test;

import com.lmax.disruptor.EventHandler;

public class LongEventHandler implements EventHandler<LongEventFactory>{

	public void onEvent(LongEventFactory EventObj, long sequence, boolean endOfBatch)
			throws Exception {
			Thread.sleep(1000);
			String message = EventObj.getDeliverObject().getMessage();
			System.out.println("消费信息：" + message);
	}
}
