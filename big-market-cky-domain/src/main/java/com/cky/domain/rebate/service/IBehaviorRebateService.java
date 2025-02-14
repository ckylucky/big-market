package com.cky.domain.rebate.service;

import com.cky.domain.rebate.model.entity.BehaviorEntity;

import java.util.List;

/**
 * @ClassName IBehaviorRebateService
 * @Description
 * @Author lukcy
 * @Date 2025/2/14 16:38
 * @Version 1.0
 */
public interface IBehaviorRebateService {
    List<String> createOrder(BehaviorEntity behaviorEntity);
}
