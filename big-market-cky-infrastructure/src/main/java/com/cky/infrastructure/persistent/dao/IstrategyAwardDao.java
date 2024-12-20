package com.cky.infrastructure.persistent.dao;

import com.cky.infrastructure.persistent.po.Award;
import com.cky.infrastructure.persistent.po.strategyAward;
import com.google.errorprone.annotations.Var;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.boot.autoconfigure.web.WebProperties;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName IstrategyAwardDao
 * @Description TODO
 * @Author lukcy
 * @Date 2024/12/13 11:05
 * @Version 1.0
 */
@Mapper
public interface IstrategyAwardDao {

    List<strategyAward> queryStrategyAwardList();

    List<Award> queryStrategyByStrategyId();

    List<strategyAward> queryStrategyAwardListByStrategyId(Long strategyId);

    String queryStartegyAwardRuleModels(strategyAward strategyAward);

    String queryStrategyAwardRuleModels(strategyAward strategyAward);
}
