package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.config.RedissonConfig;
import com.yupi.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * 工具类，提供redis的通用服务
 * @author dio哒
 * @version 1.0
 * @date 2024/7/2 18:35
 */
@Service
public class RedisLimiterManager {
    @Resource
    private RedissonClient redissonClient;
    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如不同的用户id
     */
    public void doRateLimit(String key){
        //创建用户的限流器,使用redis进行限流
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        //一秒内允许访问5次
        rateLimiter.trySetRate(RateType.OVERALL,5,1, RateIntervalUnit.SECONDS);

        //每当一个操作来了以后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
