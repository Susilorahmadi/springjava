package id.co.bni.cardbinding.services;
import id.co.bni.cardbinding.configs.MicroserviceEnum;
import id.co.bni.cardbinding.exception.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {
    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    private final RedisTemplate<String, Object> redis;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redis = redisTemplate;
    }

    private void startRequest(String host, Integer port, Object value) {
        log.info("========  REDIS   REQUEST   ==========");
        log.info("Host:Port   : {}", host + ":" + port);
        log.info("Value       : {}", value);
    }
    private void endRequest(long end, long start) {
        long elapsedTimeInMillis = (end - start) / 1_000_000;
        String elapsedTime;
        if (elapsedTimeInMillis > 1000) {
            elapsedTime = (elapsedTimeInMillis / 1000) + " seconds";
        } else {
            elapsedTime = elapsedTimeInMillis + " ms";
        }
        log.info("========  REDIS   RESPONSE  ========== " + elapsedTime);
    }

    public String set(String key, Object value, long ttlInSeconds) throws RedisException{
        startRequest(redisHost, redisPort, value);
        long start = System.nanoTime();
        try {
            redis.opsForValue().set(key, value);
            redis.expire(key, ttlInSeconds, TimeUnit.SECONDS);
            long end = System.nanoTime();
            endRequest(end, start);
            log.info("Set : {}, value : {}, ttl : {}s", key, value, ttlInSeconds);
            return "ok";
        } catch (Exception e) {
            if (e.getMessage().contains(MicroserviceEnum.RDS_MESS_UNABLE_TO_CONN.getMessage())) {
                log.warn(MicroserviceEnum.RDS_MESS_FAIL.getMessage(), redisHost, redisPort);
            }
            log.warn(MicroserviceEnum.RDS_MESS_ERR.getMessage(), e.getMessage());
            throw new RedisException(e.getMessage());
        }
    }

    public String get(String key) throws RedisException {
        startRequest(redisHost, redisPort, key);
        long start = System.nanoTime();
        try {
            String result = (String) redis.opsForValue().get(key);
            long end = System.nanoTime();
            endRequest(end, start);
            log.info("Get {} : {}", key, result);
            return result;
        } catch (Exception e) {
            if (e.getMessage().contains(MicroserviceEnum.RDS_MESS_UNABLE_TO_CONN.getMessage())) {
                log.warn(MicroserviceEnum.RDS_MESS_FAIL.getMessage(), redisHost, redisPort);
            }
            log.warn(MicroserviceEnum.RDS_MESS_ERR.getMessage(), e.getMessage());
            throw new RedisException(e.getMessage());
        }
    }

    public void delete(String key) {
        startRequest(redisHost, redisPort, key);
        try {
            long start = System.nanoTime();
            redis.delete(key);
            long end = System.nanoTime();
            endRequest(end, start);
            log.info("Delete {} : ok", key);
        } catch (RedisConnectionFailureException e) {
            if (e.getMessage().contains(MicroserviceEnum.RDS_MESS_UNABLE_TO_CONN.getMessage())) {
                log.warn(MicroserviceEnum.RDS_MESS_FAIL.getMessage(), redisHost, redisPort);
            }
            log.warn(MicroserviceEnum.RDS_MESS_ERR.getMessage(), e.getMessage());
        }
    }

    public boolean checkConn() {
        try {
            RedisConnection connection = Objects.requireNonNull(redis.getConnectionFactory()).getConnection();
            if (Objects.requireNonNull(connection.ping()).equalsIgnoreCase("PONG")) {
                return true;
            }
        } catch (Exception e) {
            log.warn("Error Check Connection Redis : {}", e.getMessage());
        }
        return false;
    }

}
