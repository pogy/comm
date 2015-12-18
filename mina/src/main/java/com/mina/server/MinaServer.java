package com.mina.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaServer {
	
    /** 
     * 缺省连接超时时间 
     */  
    public static final int DEFAULT_CONNECT_TIMEOUT = 10;  
    

	public void init(int port) throws Exception {
		//虚拟机处理器数量
		int processCount = Runtime.getRuntime().availableProcessors();
		//服务器端
		SocketAcceptor acceptor = new NioSocketAcceptor(processCount + 1);
		//服务端设置解析器
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
		//设置消息处理类（创建、关闭Session，可读可写等等，继承自接口IoHandler）
		acceptor.setHandler(new ServerHandler());
        //设置接收缓存区大小  
        acceptor.getSessionConfig().setReadBufferSize(2);  
        //设置10秒内未有连接自动断开(服务器设置即可)
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, DEFAULT_CONNECT_TIMEOUT);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.READER_IDLE, DEFAULT_CONNECT_TIMEOUT);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.WRITER_IDLE, DEFAULT_CONNECT_TIMEOUT);
		//监听端口
		acceptor.bind(new InetSocketAddress(port));
	}
}
