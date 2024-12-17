package com.cky.infrastructure.persistent.dao;

import com.cky.infrastructure.persistent.po.strategy;
import com.cky.infrastructure.persistent.po.strategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IstrategyRuleDao
 * @Description TODO
 * @Author lukcy
 * @Date 2024/12/13 11:06
 * @Version 1.0
 */
@Mapper
public interface IstrategyRuleDao {
    List<strategyRule> queryStrategyRuleList();

    strategyRule queryStrategyRule(strategyRule strategyRuleReq);

    String queryStrategyRuleValue(strategyRule strategyRule);
}
