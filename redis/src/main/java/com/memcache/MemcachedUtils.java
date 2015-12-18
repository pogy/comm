package com.memcache;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class MemcachedUtils {
	
	public static void main(String[] args) throws IOException{
		MemcachedUtils mu = new MemcachedUtils();
		mu.MemTest();
	}
	
	public void MemTest() throws IOException {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses("192.168.244.128:11211"));
		MemcachedClient memcachedClient = builder.build();
		try {
			memcachedClient.set("hello", 0, "Hello,xmemcached");
			String value = memcachedClient.get("hello");
			System.out.println("hello=" + value);
			memcachedClient.delete("hello");
			value = memcachedClient.get("hello");
			System.out.println("hello=" + value);
			memcachedClient.add("hello", 0, "Hello,xmemcached");
			value = memcachedClient.get("hello");
			System.out.println("hello=" + value);
		} catch (MemcachedException e) {
			System.err.println("MemcachedClient operation fail");
			e.printStackTrace();
		} catch (TimeoutException e) {
			System.err.println("MemcachedClient operation timeout");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// ignore
			e.printStackTrace();
		}
		try {
			memcachedClient.shutdown();
		} catch (IOException e) {
			System.err.println("Shutdown MemcachedClient fail");
			e.printStackTrace();
		}
	}
}
