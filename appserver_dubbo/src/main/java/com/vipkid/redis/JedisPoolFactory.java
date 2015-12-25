package com.vipkid.redis;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.google.common.collect.Maps;
import com.vipkid.util.Configurations;
import com.vipkid.util.TextUtils;

public class JedisPoolFactory {
	private static final Logger logger = LoggerFactory.getLogger(JedisPoolFactory.class);
    private static Map<String,JedisPool> POOL_MAP = Maps.newHashMap();

    public static JedisPool getPool() {
    	
    	String host = Configurations.Redis.HOST;
    	String password = Configurations.Redis.PASSWORD;
        int port = Configurations.Redis.PORT;
        int timeout = Configurations.Redis.TIMEOUT;
        int minSize = Configurations.Redis.POOL_MIN_IDLE;
        int maxSize = Configurations.Redis.POOL_MAX_TOTAL;
        
        String key = host + TextUtils.COLON + String.valueOf(port) + TextUtils.COLON + String.valueOf(timeout); 
        
        synchronized (JedisPoolFactory.class) {
            JedisPool jedisPool = POOL_MAP.get(key);
            if (null == jedisPool) {
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMinIdle(minSize);
                config.setMaxTotal(maxSize);
                if (TextUtils.isEmpty(password)) {
                    jedisPool = new JedisPool(config, host, port, timeout);
                } else {
                    jedisPool = new JedisPool(config, host, port, timeout, password);
                }
                POOL_MAP.put(key, jedisPool);
            }
            logger.debug("Pool initialized");
            return jedisPool;
        }
    }
  //TODO, As we don't need to change the redis properties frequently, so don't need config file  
//    private static void init(){
//    	Properties prop = new Properties();
//		InputStream input = null;
//	 
//		try {
//			input = JedisPoolFactory.class.getClassLoader().getResourceAsStream(Configurations.Redis.CONFIG_FILE);
//			prop.load(input);
//			
//			host = prop.getProperty("redis.host");
//			port = Integer.parseInt(prop.getProperty("redis.port"));
//			timeout = Integer.parseInt(prop.getProperty("redis.timeout"));
//			password = prop.getProperty("redis.password");
//			minSize = Integer.parseInt(prop.getProperty("redis.pool.minIdle"));
//			maxSize = Integer.parseInt(prop.getProperty("redis.pool.maxTotal"));
//	 
//		} catch (Exception e) {
//			logger.error("Failed to load redis config file");
//			throw new RuntimeException("Failed to load redis config file", e);
//		} finally {
//			if (input != null) {
//				try {
//					input.close();
//				} catch (IOException e) {
//					logger.error("IO problems");
//				}
//			}
//		}
//    }
}
