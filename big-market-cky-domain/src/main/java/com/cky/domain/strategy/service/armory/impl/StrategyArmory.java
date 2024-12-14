package com.cky.domain.strategy.service.armory.impl;

import com.cky.domain.strategy.model.entity.StrategyAwardEntity;
import com.cky.domain.strategy.repository.IStrategyRepository;
import com.cky.domain.strategy.service.armory.IStrategyArmory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName StrategyArmory
 * @Description  策略装配实现
 * @Author lukcy
 * @Date 2024/12/14 11:41
 * @Version 1.0
 */
@Service
public class StrategyArmory implements IStrategyArmory {

    @Resource
    private IStrategyRepository repository;
    /**
     *   根据抽奖策略id 装配其奖品对应的key 后续我们可以根据该数值来抽取对应的奖品
     * @param strategyId  抽奖策略id
     * @return
     */
    //装配抽奖策略
    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        //1、查询抽奖策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);

        //2、找到最小概率值
        BigDecimal minRate = strategyAwardEntities.stream().map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        //3、获取概率总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //4、得到 概率范围 百分位 千分位等
        BigDecimal rateRange = totalAwardRate.divide(minRate, 0, RoundingMode.CEILING);

        //5、生成策略奖品概率查找表，其实也就是分配占位，比如百分之80的可能，概率范围为百分位为100，那给他分配80个占位即可。
        List<Integer> strategyAwardSearchTables=new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAwardEntity:strategyAwardEntities){
            Integer awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();
            for (int i = 0; i <awardRate.multiply(rateRange).setScale(0,RoundingMode.CEILING).intValue(); i++) {
                strategyAwardSearchTables.add(awardId);
            }
        }
        //6、打乱 比如刚开始我们是 101 101 101.。。102 102 .。。 顺序打乱但其实占位还是不变的
        Collections.shuffle(strategyAwardSearchTables);

        //7、生成Map集合，value就是奖品，后续我们可以通过随机值 即key 来找到value
        HashMap<Integer,Integer> shuffleStartegyAwardSearchTables=new HashMap<>();
        for (int i = 0; i <strategyAwardSearchTables.size() ; i++) {
            shuffleStartegyAwardSearchTables.put(i,strategyAwardSearchTables.get(i));
            //比如 1-->102  2->104 等
        }
        //8、 存放到 Redis  注意这里不能是 rateRange ,如果概率不为1 ，rateRange可能为100，但是实际存放的值可能只有70个，就会有 null的存在。
        repository.storeStrategyAwardSearchRateTable(strategyId,shuffleStartegyAwardSearchTables.size(),shuffleStartegyAwardSearchTables);

        return true;
    }

    @Override
    public Integer getRandomAwardId(long strategyId) {
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rateRange = repository.getRateRange(strategyId);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(strategyId, new SecureRandom().nextInt(rateRange));
    }

}
