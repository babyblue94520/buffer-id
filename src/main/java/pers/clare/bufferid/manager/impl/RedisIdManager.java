package pers.clare.bufferid.manager.impl;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import pers.clare.bufferid.manager.IdManager;
import pers.clare.bufferid.util.IdUtil;
import pers.clare.util.Asserts;

import java.util.concurrent.TimeUnit;

public class RedisIdManager implements IdManager {
    public static final String key = "serial:";
    private LongRedisTemplate redisTemplate;


    public RedisIdManager(RedisConnectionFactory connectionFactory) {
        this.redisTemplate = new LongRedisTemplate(connectionFactory);
    }

    @Override
    public long next(String id, String prefix) {
        return increment(id, prefix, 1);
    }

    @Override
    public String next(String id, String prefix, int length) {
        return IdUtil.addZero(prefix, String.valueOf(increment(id, prefix, 1)), length);
    }

    @Override
    public long increment(String id, String prefix, int incr) {
        Asserts.notNull(id, "id" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        return redisTemplate.opsForValue().increment( toKey(id, prefix),incr);
    }


    @Override
    public boolean exist(String id, String prefix) {
        Asserts.notNull(id, "id" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);

        Long incr = redisTemplate.opsForValue().get(toKey(id, prefix));
        return incr == null ? false : true;
    }

    @Override
    public int save(String id, String prefix) {
        Asserts.notNull(id, "id" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        if (exist(id, prefix)) {
            return 0;
        }
        redisTemplate.opsForValue().set(toKey(id, prefix), 0L);
        return 1;
    }

    @Override
    public int remove(String id, String prefix) {
        Asserts.notNull(id, "id" + Asserts.NotNullMessage);
        Asserts.notNull(prefix, "prefix" + Asserts.NotNullMessage);
        redisTemplate.expire(toKey(id, prefix), 0, TimeUnit.MILLISECONDS);
        return 1;
    }

    private static String toKey(String id, String prefix) {
        return key + id + ":" + prefix;
    }
}

class LongRedisTemplate extends RedisTemplate<String, Long> {
    public LongRedisTemplate(RedisConnectionFactory connectionFactory) {
        GenericToStringSerializer serializer = new GenericToStringSerializer<Long>(Long.class);
        setKeySerializer(RedisSerializer.string());
        setValueSerializer(serializer);
        setHashKeySerializer(serializer);
        setHashValueSerializer(serializer);
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }
}
