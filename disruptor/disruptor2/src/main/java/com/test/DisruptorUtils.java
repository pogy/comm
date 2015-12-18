package com.test;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

public class DisruptorUtils {

	/**
	 * ringbuffer�����������2��N�η�
	 */
	private static final int BUFFER_SIZE = 1024 * 8;
	private RingBuffer<LongEventFactory> ringBuffer;
	private SequenceBarrier sequenceBarrier;
	private LongEventHandler handler;
	private BatchEventProcessor<LongEventFactory> batchEventProcessor;
	private static DisruptorUtils instance;
	private static boolean inited = false;

	private DisruptorUtils() {
		ringBuffer = new RingBuffer<LongEventFactory>(LongEventFactory.EVENT_FACTORY,
				new SingleThreadedClaimStrategy(BUFFER_SIZE),
				new YieldingWaitStrategy());
		sequenceBarrier = ringBuffer.newBarrier();
		handler = new LongEventHandler();
		batchEventProcessor = new BatchEventProcessor<LongEventFactory>(
				ringBuffer, sequenceBarrier, handler);
		ringBuffer.setGatingSequences(batchEventProcessor.getSequence());
	}

	public static void initAndStart() {
		instance = new DisruptorUtils();
		new Thread(instance.batchEventProcessor).start();
		inited = true;
	}

	public static void shutdown() {
		if (!inited) {
			throw new RuntimeException("Disruptor��û�г�ʼ����");
		}
		instance.shutdown0();
	}

	private void shutdown0() {
		batchEventProcessor.halt();
	}

	private void produce0(LongEvent deliveryReport) {
		// ��ȡ��һ�����к�
		long sequence = ringBuffer.next();
		// ��״̬�������ringBuffer�ĸ����к���
		ringBuffer.get(sequence).setDeliverObject(deliveryReport);
		// ֪ͨ����߸���Դ�������
		ringBuffer.publish(sequence);
	}

	/**
	 * ��״̬���������Դ���У��ȴ���
	 * 
	 * @param deliveryReport
	 */
	public static void produce(LongEvent deliveryReport) {
		if (!inited) {
			throw new RuntimeException("Disruptor��û�г�ʼ����");
		}
		instance.produce0(deliveryReport);
	}

}
