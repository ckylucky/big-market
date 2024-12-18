package com.cky.domain.strategy.repository;

import com.cky.domain.strategy.model.entity.StrategyAwardEntity;
import com.cky.domain.strategy.model.entity.StrategyEntity;
import com.cky.domain.strategy.model.entity.StrategyRuleEntity;
import com.cky.domain.strategy.model.valobj.StrategyAwardRuleModelVO;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName IStrategyRepository
 * @Description  策略仓储接口
 * @Author lukcy
 * @Date 2024/12/14 11:44
 * @Version 1.0
 */
public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(String key, int size, HashMap<Integer, Integer> shuffleStartegyAwardSearchTables);

    int getRateRange(String key);
    int getRateRange(Long StrategyId);
    Integer getStrategyAwardAssemble(String key, int i);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleWeight);
    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);
    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    StrategyAwardRuleModelVO queryStartegyAwardRuleModels(StrategyAwardEntity build);
}
