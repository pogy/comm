package com.mina;

import com.mina.server.MinaServer;

/**
 * 测试
 * @author totyuZWL
 *
 */
public class ServerTest {

	public static void main(String[] args) throws Exception{
		MinaServer server = new MinaServer();
		server.init(11210);
	}
}
