package com.test1;

import com.lmax.disruptor.EventHandler;

public class LongEventHandler implements EventHandler<LongEvent> {
	@Override
	public void onEvent(LongEvent longEvent, long l, boolean b)
			throws Exception {
		Thread.sleep(2000);
		System.out.println("消费完毕："+longEvent.getMessage());
	}
}