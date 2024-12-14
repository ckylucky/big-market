package com.cky.infrastructure.persistent.dao;

import com.cky.infrastructure.persistent.po.Award;
import com.cky.infrastructure.persistent.po.strategy;
import com.cky.infrastructure.persistent.po.strategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IstrategyDao
 * @Description TODO
 * @Author lukcy
 * @Date 2024/12/13 11:05
 * @Version 1.0
 */
@Mapper
public interface IstrategyDao {
    List<strategy> queryStrategyList();

    List<strategyAward> queryStrategyAwardListByStrategyId(Long strategyId);
}
