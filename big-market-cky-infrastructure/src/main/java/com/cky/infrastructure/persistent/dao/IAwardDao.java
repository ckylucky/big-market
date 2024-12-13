package com.cky.infrastructure.persistent.dao;

import com.cky.infrastructure.persistent.po.Award;
import com.cky.infrastructure.persistent.po.strategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName IAwardDao
 * @Description TODO
 * @Author lukcy
 * @Date 2024/12/13 11:04
 * @Version 1.0
 */
@Mapper
public interface IAwardDao {
    List<Award> queryAwardList();
}
