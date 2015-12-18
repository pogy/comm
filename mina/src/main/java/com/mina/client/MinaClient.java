package com.mina.client;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class MinaClient {
	
    public NioSocketConnector connector;  
  
    public String HOST = "127.0.0.1";  
  
    public int PORT = 11210;  
  
    public MinaClient(){
    	init(); 
    }
    
    public void init() {  
    	connector = new NioSocketConnector();  
    	//创建接受数据的过滤器
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        //设定这个过滤器将一行一行(/r/n)的读取数据 
        chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        //客户端处理器
        connector.setHandler(new ClientHandler());  
    }  
  
    /**
     * 信息发送
     * @param msg
     */
    public void sendMessage(final String message) {  
        ConnectFuture cf = connector.connect(new InetSocketAddress(HOST, PORT));
        try {
        	System.out.println("客户端请求命令：" + message);
        	//Wait for the connection attempt to be finished.
        	cf.awaitUninterruptibly();
            IoSession session = cf.getSession();
            session.write(message);
        } catch (Exception e) { 
            e.printStackTrace();
        } 
    }    
}   