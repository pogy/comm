package com.test;

import com.lmax.disruptor.EventFactory;


/**
 * Ѫѹ�¼�
 * @author Administrator
 *
 */
public class LongEventFactory {
	
	private LongEvent deliverObj;

	public LongEvent getDeliverObject() {
		return deliverObj;
	}

	public void setDeliverObject(LongEvent _deliverObj) {
		this.deliverObj = _deliverObj;
	}
	
	
	public final static EventFactory<LongEventFactory> EVENT_FACTORY = 
			new EventFactory<LongEventFactory>() {
		         public LongEventFactory newInstance() {
		             return new LongEventFactory();
		         }
		     };

}
