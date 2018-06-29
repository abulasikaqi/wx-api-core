package wx.common.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RedisCacheHelper {
	private RedisTemplate redisTemplate;
	

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/* ----------- common --------- */
	public Collection<String> keys(String pattern) {
		return redisTemplate.keys(pattern);
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}

	public void delete(Collection<String> key) {
		redisTemplate.delete(key);
	}

	/* ----------- string --------- */
	public <T> T get(String key, Class<T> clazz) {
		String value = (String) redisTemplate.opsForValue().get(key);
		return parseJson(value, clazz);
	}

	public <T> List<T> mget(Collection<String> keys, Class<T> clazz) {
		List<String> values = redisTemplate.opsForValue().multiGet(keys);
		return parseJsonList(values, clazz);
	}

	public <T> void set(String key, T obj, Long timeout, TimeUnit unit) {
		if (obj == null) {
			return;
		}

		String value = toJson(obj);
		if (timeout != null) {
			redisTemplate.opsForValue().set(key, value, timeout, unit);
		} else {
			redisTemplate.opsForValue().set(key, value);
		}
	}

	public <T> void set(String key, T obj, int timeout) {
		if (obj == null) {
			return;
		}

		String value = toJson(obj);
		if (timeout != 0) {
			redisTemplate.opsForValue().set(key, value, Long.valueOf(timeout), TimeUnit.SECONDS);
		} else {
			redisTemplate.opsForValue().set(key, value);
		}
	}

	public <T> void set(String key, T obj) {
		if (obj == null) {
			return;
		}
		String value = toJson(obj);
		redisTemplate.opsForValue().set(key, value);
	}

	public <T> T getAndSet(String key, T obj, Class<T> clazz) {
		if (obj == null) {
			return get(key, clazz);
		}

		String value = (String) redisTemplate.opsForValue().getAndSet(key, toJson(obj));
		return parseJson(value, clazz);
	}

	public int decrement(String key, int delta) {
		Long value = redisTemplate.opsForValue().increment(key, -delta);
		return value.intValue();
	}

	public int increment(String key, int delta) {
		Long value = redisTemplate.opsForValue().increment(key, delta);
		return value.intValue();
	}

	public boolean exists(final String key) {
		return (boolean) redisTemplate.execute(new RedisCallback() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.exists(key.getBytes());
			}
		});
	}

	/* ----------- list --------- */
	public int size(String key) {
		return redisTemplate.opsForList().size(key).intValue();
	}

	public <T> List<T> range(String key, long start, long end, Class<T> clazz) {
		List<String> list = redisTemplate.opsForList().range(key, start, end);
		return parseJsonList(list, clazz);
	}

	public void rightPushAll(String key, Collection<?> values, Long timeout, TimeUnit unit) {
		if (values == null || values.isEmpty()) {
			return;
		}

		redisTemplate.opsForList().rightPushAll(key, toJsonList(values));
		if (timeout != null) {
			redisTemplate.expire(key, timeout, unit);
		}
	}

	public <T> void leftPush(String key, T obj) {
		if (obj == null) {
			return;
		}

		redisTemplate.opsForList().leftPush(key, toJson(obj));
	}

	public <T> T leftPop(String key, Class<T> clazz) {
		String value = (String) redisTemplate.opsForList().leftPop(key);
		return parseJson(value, clazz);
	}

	public void remove(String key, int count, Object obj) {
		if (obj == null) {
			return;
		}

		redisTemplate.opsForList().remove(key, count, toJson(obj));
	}

	/* ----------- zset --------- */
	public int zcard(String key) {
		return redisTemplate.opsForZSet().zCard(key).intValue();
	}

	public <T> List<T> zrange(String key, long start, long end, Class<T> clazz) {
		Set<String> set = redisTemplate.opsForZSet().range(key, start, end);
		return parseJsonList(setToList(set), clazz);
	}

	private List<String> setToList(Set<String> set) {
		if (set == null) {
			return null;
		}
		return new ArrayList<String>(set);
	}

	public void zadd(String key, Object obj, double score) {
		if (obj == null) {
			return;
		}
		redisTemplate.opsForZSet().add(key, toJson(obj), score);
	}

	public void zaddAll(String key, List<TypedTuple<?>> tupleList, Long timeout, TimeUnit unit) {
		if (tupleList == null || tupleList.isEmpty()) {
			return;
		}

		Set<TypedTuple<String>> tupleSet = toTupleSet(tupleList);
		redisTemplate.opsForZSet().add(key, tupleSet);
		if (timeout != null) {
			redisTemplate.expire(key, timeout, unit);
		}
	}

	private Set<TypedTuple<String>> toTupleSet(List<TypedTuple<?>> tupleList) {
		Set<TypedTuple<String>> tupleSet = new LinkedHashSet<TypedTuple<String>>();
		for (TypedTuple<?> t : tupleList) {
			tupleSet.add(new DefaultTypedTuple<String>(toJson(t.getValue()), t.getScore()));
		}
		return tupleSet;
	}

	public void zrem(String key, Object obj) {
		if (obj == null) {
			return;
		}
		redisTemplate.opsForZSet().remove(key, toJson(obj));
	}

	public void unionStore(String destKey, Collection<String> keys, Long timeout, TimeUnit unit) {
		if (keys == null || keys.isEmpty()) {
			return;
		}

		Object[] keyArr = keys.toArray();
		String key = (String) keyArr[0];

		Collection<String> otherKeys = new ArrayList<String>(keys.size() - 1);
		for (int i = 1; i < keyArr.length; i++) {
			otherKeys.add((String) keyArr[i]);
		}

		redisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey);
		if (timeout != null) {
			redisTemplate.expire(destKey, timeout, unit);
		}
	}

	/* ----------- tool methods --------- */
	public String toJson(Object obj) {
		return JSON.toJSONString(obj, SerializerFeature.SortField);
	}

	public <T> T parseJson(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}

	public List<String> toJsonList(Collection<?> values) {
		if (values == null) {
			return null;
		}

		List<String> result = new ArrayList<String>();
		for (Object obj : values) {
			result.add(toJson(obj));
		}
		return result;
	}

	public <T> List<T> parseJsonList(List<String> list, Class<T> clazz) {
		if (list == null) {
			return null;
		}

		List<T> result = new ArrayList<T>();
		for (String s : list) {
			result.add(parseJson(s, clazz));
		}
		return result;
	}
}
