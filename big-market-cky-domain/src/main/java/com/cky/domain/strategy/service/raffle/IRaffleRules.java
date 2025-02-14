package com.cky.domain.strategy.service.raffle;

import java.util.Map;

/**
 * @ClassName IRaffleRules
 * @Description  抽奖规则接口
 * @Author lukcy
 * @Date 2025/1/9 11:40
 * @Version 1.0
 */
public interface IRaffleRules {
    /**
     * 根据规则树ID集合查询奖品中加锁数量的配置「部分奖品需要抽奖N次解锁」
     *
     * @param treeIds 规则树ID值
     * @return key 规则树，value rule_lock 加锁值
     */
    Map<String,Integer> queryAwardRuleLockCount(String[] treeIds);
}
