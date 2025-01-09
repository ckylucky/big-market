package com.cky.domain.strategy.service.armory.impl;

import com.cky.domain.activity.model.entity.ActivityEntity;
import com.cky.domain.activity.repository.IActivityRepository;
import com.cky.domain.strategy.model.entity.StrategyAwardEntity;
import com.cky.domain.strategy.model.entity.StrategyEntity;
import com.cky.domain.strategy.model.entity.StrategyRuleEntity;
import com.cky.domain.strategy.repository.IStrategyRepository;
import com.cky.domain.strategy.service.armory.IStrategyDispatch;
import com.cky.domain.strategy.service.armory.IStrategyArmory;
import com.cky.types.common.Constants;
import com.cky.types.enums.ResponseCode;
import com.cky.types.exception.AppException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

/**
 * @ClassName StrategyArmory
 * @Description  策略装配实现
 * @Author lukcy
 * @Date 2024/12/14 11:41
 * @Version 1.0
 */
@Service
public class StrategyArmory implements IStrategyArmory, IStrategyDispatch {

    @Resource
    private IStrategyRepository repository;


    @Resource
    private IActivityRepository activityRepository;
    /**
     *   根据抽奖策略id 装配其奖品对应的key 后续我们可以根据该数值来抽取对应的奖品  一般在活动确定好就初始化到redis中了
     * @param strategyId  抽奖策略id
     * @return
     */
    //装配抽奖策略
    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {

        if (strategyId == null) {
            // 如果 strategyId 为 null，直接返回 false 或者抛出异常
            throw new AppException(ResponseCode.STRATEGY_IS_NULL.getCode(), ResponseCode.STRATEGY_IS_NULL.getInfo());
        }
        //1、查询抽奖策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);

        // 2、判断查询结果是否为空
        if (strategyAwardEntities == null || strategyAwardEntities.isEmpty()) {
            // 如果没有找到对应的抽奖策略配置，可以返回 false 或者进行其他处理
            throw new AppException(ResponseCode.STRATEGY_AWARD_IS_NULL.getCode(), ResponseCode.STRATEGY_AWARD_IS_NULL.getInfo());
        }
        // 2 缓存奖品库存【用于decr扣减库存使用】
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            Integer awardCount = strategyAward.getAwardCount();
            cacheStrategyAwardCount(strategyId, awardId, awardCount);
        }
        assembleLotteryStrategy(strategyId.toString(),strategyAwardEntities);
        //2 通过策略id查找策略实体 接着判断它的rule_model是否还有rule_weight
        StrategyEntity strategyEntity =repository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if(ruleWeight==null)
            return true;//当前策略没有配置rule_weight
        //3 查询具体的策略配置规则
        StrategyRuleEntity strategyRuleEntity=repository.queryStrategyRule(strategyId,ruleWeight);
        if (null == strategyRuleEntity) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }//有rule_weight 但是没有具体的配置
        //划分 4000:102,103,104 6000:102,103,104,105,106,107,108,109
        //key为4000:102,103,104 value为102 103 104 当然key也可以为4000
        Map<String, List<Integer>> ruleWeightValueMap = strategyRuleEntity.getRuleWeightValues();
        Set<String> keys = ruleWeightValueMap.keySet();
        for (String key : keys) {
            List<Integer> ruleWeightValues = ruleWeightValueMap.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            //把之前得到的全部奖品克隆,然后移除掉不在当前实体中的,然后进行装配
            strategyAwardEntitiesClone.removeIf(entity -> !ruleWeightValues.contains(entity.getAwardId()));
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(key), strategyAwardEntitiesClone);
        }
        return true;
    }

    /**
     * 通过活动id来装配 对应的策略
     * @param activityId
     */
    @Override
    public boolean assembleLotteryStrategyByActivityId(Long activityId) {
        ActivityEntity activityEntity =activityRepository.queryRaffleActivityByActivityId(activityId);
        Long strategyId = activityEntity.getStrategyId();
        return assembleLotteryStrategy(strategyId);
    }

    private void cacheStrategyAwardCount(Long strategyId, Integer awardId, Integer awardCount) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        repository.cacheStrategyAwardCount(cacheKey, awardCount);
    }


    /**
     *
     * @param key
     * @param strategyAwardEntities  装配的策略奖品实体
     * @return
     */
    /**
     * 转换计算，只根据小数位来计算。如【0.01返回100】、【0.009返回1000】、【0.0018返回10000】
     */
    private double convert(double min) {
        double current = min;
        double max = 1;
        while (current < 1) {
            current = current * 10;
            max = max * 10;
        }
        return max;
    }
    public boolean assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntities) {


        // 1. 获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 2. 循环计算找到概率范围值
        BigDecimal rateRange = BigDecimal.valueOf(convert(minAwardRate.doubleValue()));

        //4、生成策略奖品概率查找表，其实也就是分配占位，比如百分之80的可能，概率范围为百分位为100，那给他分配80个占位即可。
        List<Integer> strategyAwardSearchTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
            Integer awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();
            for (int i = 0; i < rateRange.multiply(awardRate).intValue(); i++) {
                strategyAwardSearchTables.add(awardId);
            }
        }
        //5、打乱 比如刚开始我们是 101 101 101.。。102 102 .。。 顺序打乱但其实占位还是不变的
        Collections.shuffle(strategyAwardSearchTables);

        //6、生成Map集合，value就是奖品，后续我们可以通过随机值 即key 来找到value
        HashMap<Integer, Integer> shuffleStartegyAwardSearchTables = new HashMap<>();
        for (int i = 0; i < strategyAwardSearchTables.size(); i++) {
            shuffleStartegyAwardSearchTables.put(i, strategyAwardSearchTables.get(i));
            //比如 1-->102  2->104 等
        }
        //这个方法 不仅存放大小 还存放map表的具体信息
        //7、 存放到 Redis  注意这里不能是 rateRange ,如果概率不为1 ，rateRange可能为100，但是实际存放的值可能只有70个，就会有 null的存在。
        repository.storeStrategyAwardSearchRateTable(key, shuffleStartegyAwardSearchTables.size(), shuffleStartegyAwardSearchTables);

        return true;
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rateRange = repository.getRateRange(strategyId);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(String.valueOf(strategyId), new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat("_").concat(ruleWeightValue);
        return getRandomAwardId(key);
    }

    @Override
    public Integer getRandomAwardId(String key) {
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rateRange = repository.getRateRange(key);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(key, new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Boolean subtractionAwardStock(Long strategyId, Integer awardId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        return repository.subtractionAwardStock(cacheKey);

    }
}
