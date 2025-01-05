package com.cky.domain.award.repository;

import com.cky.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * @ClassName IAwardRepository
 * @Description  奖品仓储层接口
 * @Author lukcy
 * @Date 2025/1/5 11:22
 * @Version 1.0
 */
public interface IAwardRepository {


    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);
}
