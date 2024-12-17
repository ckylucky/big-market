package com.cky.infrastructure.persistent.repository;

import com.cky.domain.strategy.model.entity.StrategyAwardEntity;
import com.cky.domain.strategy.model.entity.StrategyEntity;
import com.cky.domain.strategy.model.entity.StrategyRuleEntity;
import com.cky.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.cky.domain.strategy.repository.IStrategyRepository;
import com.cky.infrastructure.persistent.dao.IstrategyAwardDao;
import com.cky.infrastructure.persistent.dao.IstrategyDao;
import com.cky.infrastructure.persistent.dao.IstrategyRuleDao;
import com.cky.infrastructure.persistent.po.strategy;
import com.cky.infrastructure.persistent.po.strategyAward;
import com.cky.infrastructure.persistent.po.strategyRule;
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
    @Resource
    private IstrategyDao strategyDao;
    @Resource
    private IstrategyRuleDao strategyRuleDao;
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
        if (strategyAwards == null || strategyAwards.isEmpty()) {
            // 可以记录日志或者抛出异常，表示没有找到数据
            return Collections.emptyList(); // 如果数据库也没有，返回空列表
        }
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

        /** stream流方式
         * strategyAwardEntities = strategyAwards.stream()
         *         .map(strategyAward -> StrategyAwardEntity.builder()
         *             .strategyId(strategyAward.getStrategyId())
         *             .awardId(strategyAward.getAwardId())
         *             .awardCount(strategyAward.getAwardCount())
         *             .awardCountSurplus(strategyAward.getAwardCountSurplus())
         *             .awardRate(strategyAward.getAwardRate())
         *             .build())
         *         .collect(Collectors.toList());
         * */
        redisService.setValue(CacheKey,strategyAwardEntities);
        //保存到redis


        return strategyAwardEntities;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(String key, int rateRange, HashMap<Integer, Integer> shuffleStartegyAwardSearchTables) {
        // 1. 存储抽奖策略范围值，如10000，用于生成1000以内的随机数
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, rateRange);
        // 2. 存储概率查找表
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffleStartegyAwardSearchTables);
    }

    @Override
    public int getRateRange(String key) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public int getRateRange(Long StrategyId) {
        return getRateRange(String.valueOf(StrategyId));
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, int rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key, rateKey);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        //先从缓存中取
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(cacheKey);
        if (null != strategyEntity) return strategyEntity;
        strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();
        redisService.setValue(cacheKey, strategyEntity);
        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        strategyRule strategyRuleReq = new strategyRule();
        strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(ruleModel);
        strategyRule strategyRuleRes = strategyRuleDao.queryStrategyRule(strategyRuleReq);
        return StrategyRuleEntity.builder()
                .strategyId(strategyRuleRes.getStrategyId())
                .awardId(strategyRuleRes.getAwardId())
                .ruleType(strategyRuleRes.getRuleType())
                .ruleModel(strategyRuleRes.getRuleModel())
                .ruleValue(strategyRuleRes.getRuleValue())
                .ruleDesc(strategyRuleRes.getRuleDesc())
                .build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        strategyRule strategyRule = new strategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setAwardId(awardId);
        strategyRule.setRuleModel(ruleModel);
        return strategyRuleDao.queryStrategyRuleValue(strategyRule);
    }

    @Override
    public StrategyAwardRuleModelVO queryStartegyAwardRuleModels(StrategyAwardEntity strategyAwardEntity) {
        strategyAward strategyAward=new strategyAward();
        strategyAward.setAwardId(strategyAwardEntity.getAwardId());
        strategyAward.setStrategyId(strategyAwardEntity.getStrategyId());
        String ruleModels =strategyAwardDao.queryStartegyAwardRuleModels(strategyAward);
        return StrategyAwardRuleModelVO.builder().ruleModels(ruleModels).build();
    }
}
