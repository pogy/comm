package com.vipkid.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.exceptions.JedisDataException;

import com.vipkid.util.Configurations;
import com.vipkid.util.TextUtils;

public class DistributedLock {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLock.class);

    /**
     * 
     * @param key
     * @param expireTime (seconds)
     * @return
     */
    public static boolean lock(String key, Integer expireTime) {
    	if (!Configurations.Redis.ENABLE_LOCK){
    		return true;
    	}
        if (TextUtils.isEmpty(key) || expireTime != null && expireTime <= 0) {
        	logger.error("key:{}, expireTime:{} is invalidate.", key, expireTime);
            return false;
        }
        try {
            Long setOK = RedisClient.getInstance().setnx(key, Configurations.Redis.LOCK_VAULE);
            logger.info("Lock key={}, setOk={}", key, setOK);
            if (setOK > 0) {
            	if (expireTime != null){
            		RedisClient.getInstance().expire(key, expireTime);
            	}
                return true;
            }
        } catch (Exception e) {
        	logger.error("Failed to lock the key={}", key, e);
            checkMasterDown(e);
        }
        return false;
    }
    
    public static boolean lock(String key) {
        return lock(key, 300);
    }

    public static boolean unlock(String key) {
    	if (!Configurations.Redis.ENABLE_LOCK){
    		return true;
    	}
        if (TextUtils.isEmpty(key)) {
        	logger.error("key:{} can not be null", key);
            return false;
        }
        try {
        	RedisClient.getInstance().del(key);
            return true;
        } catch (Exception e) {
        	logger.error("Failed to unlock the key={}", key, e);
            checkMasterDown(e);
        }
        return false;
    }

    private static void checkMasterDown(Exception e) {
        if (e.getMessage() != null && e.getMessage().contains("READONLY")) {
            if (e instanceof JedisDataException) {
            	logger.error("master down now");
            } else {
            	logger.error("unknow readonly error now");
            }
        }
    }

//    public static void main(String[] args) {
//        String key = "ddddd1";
//        boolean isLocked = GlobalLock.lock(key);
//        System.out.println(isLocked);
//    }

}
