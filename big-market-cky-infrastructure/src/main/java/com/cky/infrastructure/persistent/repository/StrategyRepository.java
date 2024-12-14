package com.cky.infrastructure.persistent.repository;

import com.cky.domain.strategy.model.entity.StrategyAwardEntity;
import com.cky.domain.strategy.repository.IStrategyRepository;
import com.cky.infrastructure.persistent.dao.IstrategyAwardDao;
import com.cky.infrastructure.persistent.dao.IstrategyDao;
import com.cky.infrastructure.persistent.po.strategyAward;
import com.cky.infrastructure.persistent.redis.IRedisService;
import com.cky.types.common.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName StrategyRepository
 * @Description
 * @Author lukcy
 * @Date 2024/12/14 11:46
 * @Version 1.0
 */
@Repository
public class StrategyRepository implements IStrategyRepository {
    @Resource
    private IRedisService redisService;

    @Resource
    private IstrategyAwardDao strategyAwardDao;
    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        String CacheKey= Constants.RedisKey.STRATEGY_AWARD_KEY+strategyId;
        //1、首先从redis中查询
        List<StrategyAwardEntity>  strategyAwardEntities = redisService.getValue(CacheKey);
        if(strategyAwardEntities!=null&&!strategyAwardEntities.isEmpty()){
            return strategyAwardEntities;
        }
        //没有 查询策略对应的奖品信息
        List<strategyAward> strategyAwards =  strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        //转为实体对象
        strategyAwardEntities=new ArrayList<>(strategyAwards.size());
        for (strategyAward strategyAward:strategyAwards){
             StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                          .strategyId(strategyAward.getStrategyId())
                          .awardId(strategyAward.getAwardId())
                          .awardCount(strategyAward.getAwardCount())
                          .awardCountSurplus(strategyAward.getAwardCountSurplus())
                          .awardRate(strategyAward.getAwardRate())
                          .build();

            strategyAwardEntities.add(strategyAwardEntity);
        }
        redisService.setValue(CacheKey,strategyAwardEntities);
        //保存到redis


        return strategyAwardEntities;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(Long strategyId, int rateRange, HashMap<Integer, Integer> shuffleStartegyAwardSearchTables) {
        // 1. 存储抽奖策略范围值，如10000，用于生成1000以内的随机数
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId, rateRange);
        // 2. 存储概率查找表
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(shuffleStartegyAwardSearchTables);
    }

    @Override
    public int getRateRange(long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }

    @Override
    public Integer getStrategyAwardAssemble(long strategyId, int rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId, rateKey);
    }
}
