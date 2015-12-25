package com.vipkid.redis;

import com.google.common.base.Preconditions;
import com.vipkid.redis.serializer.SerializerFactory;
import com.vipkid.redis.serializer.SerializerType;
import com.vipkid.service.exception.RedisException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RedisClient implements JedisCommands {
    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private JedisPool jedisPool;

    private static RedisClient redisClient = new RedisClient();

    private RedisClient() {
        jedisPool = JedisPoolFactory.getPool();
        if (jedisPool == null) {
            String errorMessage = "JedisPool not available";
            logger.error(errorMessage);
            throw new RedisException(errorMessage);
        }
    }

    public static RedisClient getInstance() {
        return redisClient;
    }


    @Override
    public String set(String key, String value) {
        Jedis jedis = null;
        String setOK = "";
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                setOK = jedis.set(key, value);
            }
            jedisPool.returnResource(jedis);
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new RedisException(e);
        }
        return setOK;
    }

    @Override
    public String get(String key) {
        Jedis jedis = null;
        String getOK = "";
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                getOK = jedis.get(key);
            }
            jedisPool.returnResource(jedis);
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new RedisException(e);
        }
        return getOK;
    }
    public byte[] get(byte [] key) {
        Jedis jedis = null;
        byte[] value = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                value = jedis.get(key);
            }
            jedisPool.returnResource(jedis);
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new RedisException(e);
        }
        return value;
    }
    public String set(byte[] key, byte[] value) {
        Jedis jedis = null;
        String setOK = "";
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                setOK = jedis.set(key, value);
            }
            jedisPool.returnResource(jedis);
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new RedisException(e);
        }
        return setOK;
    }

    public <T extends Serializable> boolean setObject(String key, T value) {
        Preconditions.checkArgument(StringUtils.isNotBlank(key));
        try {
            final byte[] keyBytes = SerializerFactory.getStringBytes(key);
            final byte[] valueBytes = SerializerFactory.getSerializer(SerializerType.hessian).encode(value);
            String ret = this.set(keyBytes, valueBytes);
            if (StringUtils.containsIgnoreCase(ret, "ok")) {
                return true;
            } else {
                logger.info("ret = {}", ret);
                return false;
            }
        } catch (Exception e) {
            logger.error("Redis Exception:{}",e);
            return false;
        }
    }

    public Object getObject(String key) {
        Preconditions.checkArgument(StringUtils.isNotBlank(key));
        try {
            final byte[] keyBytes = SerializerFactory.getStringBytes(key);
            byte[] value = this.get(keyBytes);
            if (ArrayUtils.isNotEmpty(value)) {
                return SerializerFactory.getSerializer(SerializerType.hessian).decode(value);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Redis Exception:{}",e);
        }
        return null;
    }

    @Override
    public Boolean exists(String key) {
        return null;
    }

    @Override
    public Long persist(String key) {
        return null;
    }

    @Override
    public String type(String key) {
        return null;
    }

    @Override
    public Long expire(String key, int seconds) {
        Jedis jedis = null;
        Long expireOK = 0L;
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                expireOK = jedis.expire(key, seconds);
            }
            jedisPool.returnResource(jedis);
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new RedisException(e);
        }
        return expireOK;
    }

    @Override
    public Long expireAt(String key, long unixTime) {
        return null;
    }

    @Override
    public Long ttl(String key) {
        return null;
    }

    @Override
    public Boolean setbit(String key, long offset, boolean value) {
        return null;
    }

    @Override
    public Boolean setbit(String key, long offset, String value) {
        return null;
    }

    @Override
    public Boolean getbit(String key, long offset) {
        return null;
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        return null;
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        return null;
    }

    @Override
    public String getSet(String key, String value) {
        return null;
    }

    @Override
    public Long setnx(String key, String value) {
        Jedis jedis = null;
        Long ok = 0L;
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                ok = jedis.setnx(key, value);
            }
            jedisPool.returnResource(jedis);
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new RedisException(e);
        }
        return ok;
    }

    @Override
    public String setex(String key, int seconds, String value) {
        return null;
    }

    @Override
    public Long decrBy(String key, long integer) {
        return null;
    }

    @Override
    public Long decr(String key) {
        return null;
    }

    @Override
    public Long incrBy(String key, long integer) {
        return null;
    }

    @Override
    public Long incr(String key) {
        return null;
    }

    @Override
    public Long append(String key, String value) {
        return null;
    }

    @Override
    public String substr(String key, int start, int end) {
        return null;
    }

    @Override
    public Long hset(String key, String field, String value) {
        return null;
    }

    @Override
    public String hget(String key, String field) {
        return null;
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        return null;
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        return null;
    }

    @Override
    public List<String> hmget(String key, String... fields) {
        return null;
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
        return null;
    }

    @Override
    public Boolean hexists(String key, String field) {
        return null;
    }

    @Override
    public Long hdel(String key, String... field) {
        return null;
    }

    @Override
    public Long hlen(String key) {
        return null;
    }

    @Override
    public Set<String> hkeys(String key) {
        return null;
    }

    @Override
    public List<String> hvals(String key) {
        return null;
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return null;
    }

    @Override
    public Long rpush(String key, String... string) {
        return null;
    }

    @Override
    public Long lpush(String key, String... string) {
        return null;
    }

    @Override
    public Long llen(String key) {
        return null;
    }

    @Override
    public List<String> lrange(String key, long start, long end) {
        return null;
    }

    @Override
    public String ltrim(String key, long start, long end) {
        return null;
    }

    @Override
    public String lindex(String key, long index) {
        return null;
    }

    @Override
    public String lset(String key, long index, String value) {
        return null;
    }

    @Override
    public Long lrem(String key, long count, String value) {
        return null;
    }

    @Override
    public String lpop(String key) {
        return null;
    }

    @Override
    public String rpop(String key) {
        return null;
    }

    @Override
    public Long sadd(String key, String... member) {
        return null;
    }

    @Override
    public Set<String> smembers(String key) {
        return null;
    }

    @Override
    public Long srem(String key, String... member) {
        return null;
    }

    @Override
    public String spop(String key) {
        return null;
    }

    @Override
    public Long scard(String key) {
        return null;
    }

    @Override
    public Boolean sismember(String key, String member) {
        return null;
    }

    @Override
    public String srandmember(String key) {
        return null;
    }

    @Override
    public Long strlen(String key) {
        return null;
    }

    @Override
    public Long zadd(String key, double score, String member) {
        return null;
    }


    @Override
    public Set<String> zrange(String key, long start, long end) {
        return null;
    }

    @Override
    public Long zrem(String key, String... member) {
        return null;
    }

    @Override
    public Double zincrby(String key, double score, String member) {
        return null;
    }

    @Override
    public Long zrank(String key, String member) {
        return null;
    }

    @Override
    public Long zrevrank(String key, String member) {
        return null;
    }

    @Override
    public Set<String> zrevrange(String key, long start, long end) {
        return null;
    }

    @Override
    public Long zcard(String key) {
        return null;
    }

    @Override
    public Double zscore(String key, String member) {
        return null;
    }

    @Override
    public List<String> sort(String key) {
        return null;
    }

    @Override
    public List<String> sort(String key, SortingParams sortingParameters) {
        return null;
    }

    @Override
    public Long zcount(String key, double min, double max) {
        return null;
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return null;
    }

    @Override
    public Long zremrangeByRank(String key, long start, long end) {
        return null;
    }

    @Override
    public Long zremrangeByScore(String key, double start, double end) {
        return null;
    }

    @Override
    public Long zremrangeByScore(String key, String start, String end) {
        return null;
    }

    @Override
    public Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
        return null;
    }

    @Override
    public Long lpushx(String key, String... string) {
        return null;
    }

    @Override
    public Long rpushx(String key, String... string) {
        return null;
    }

    @Override
    public List<String> blpop(String arg) {
        return null;
    }

    @Override
    public List<String> brpop(String arg) {
        return null;
    }

    @Override
    public Long del(String key) {
        Jedis jedis = null;
        Long ok = 0L;
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                ok = jedis.del(key);
            }
            jedisPool.returnResource(jedis);
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new RedisException(e);
        }
        return ok;
    }

    @Override
    public String echo(String string) {
        return null;
    }

    @Override
    public Long move(String key, int dbIndex) {
        return null;
    }

    @Override
    public Long bitcount(String key) {
        return null;
    }

    @Override
    public Long bitcount(String key, long start, long end) {
        return null;
    }

    @Override
    public ScanResult<Entry<String, String>> hscan(String arg0, int arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<Entry<String, String>> hscan(String arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<String> sscan(String arg0, int arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<String> sscan(String arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long zadd(String arg0, Map<String, Double> arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<redis.clients.jedis.Tuple> zrangeByScoreWithScores(String arg0,
                                                                  double arg1, double arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<redis.clients.jedis.Tuple> zrangeByScoreWithScores(String arg0,
                                                                  String arg1, String arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<redis.clients.jedis.Tuple> zrangeByScoreWithScores(String arg0,
                                                                  double arg1, double arg2, int arg3, int arg4) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<redis.clients.jedis.Tuple> zrangeByScoreWithScores(String arg0,
                                                                  String arg1, String arg2, int arg3, int arg4) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<redis.clients.jedis.Tuple> zrangeWithScores(String arg0,
                                                           long arg1, long arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String arg0, String arg1, String arg2,
                                        int arg3, int arg4) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<redis.clients.jedis.Tuple> zrevrangeByScoreWithScores(
            String arg0, double arg1, double arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<redis.clients.jedis.Tuple> zrevrangeByScoreWithScores(
            String arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<redis.clients.jedis.Tuple> zrevrangeByScoreWithScores(
            String arg0, double arg1, double arg2, int arg3, int arg4) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<redis.clients.jedis.Tuple> zrevrangeByScoreWithScores(
            String arg0, String arg1, String arg2, int arg3, int arg4) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<redis.clients.jedis.Tuple> zrevrangeWithScores(String arg0,
                                                              long arg1, long arg2) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<redis.clients.jedis.Tuple> zscan(String arg0, int arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ScanResult<redis.clients.jedis.Tuple> zscan(String arg0, String arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<String> getKeys(String key) {
        Jedis jedis = null;
        Set<String> results = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                results = jedis.keys(key);
            }
            jedisPool.returnResource(jedis);
        } catch (Exception e) {
            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
            }
            throw new RedisException(e);
        }
        return results;
    }
}
