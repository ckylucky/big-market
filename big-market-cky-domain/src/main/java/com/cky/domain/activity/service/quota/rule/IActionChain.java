package com.cky.domain.activity.service.quota.rule;

import com.cky.domain.activity.model.entity.ActivityCountEntity;
import com.cky.domain.activity.model.entity.ActivityEntity;
import com.cky.domain.activity.model.entity.ActivitySkuEntity;

/**
 * @ClassName IActionChain
 * @Description  下单规则过滤接口
 * @Author lukcy
 * @Date 2024/12/28 11:08
 * @Version 1.0
 */
public interface IActionChain extends IActionChainArmory{
    boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);
}
