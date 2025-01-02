package com.cky.domain.activity.service.rule.impl;

import com.cky.domain.activity.model.entity.ActivityCountEntity;
import com.cky.domain.activity.model.entity.ActivityEntity;
import com.cky.domain.activity.model.entity.ActivitySkuEntity;
import com.cky.domain.activity.service.rule.AbstractActionChain;
import com.cky.domain.activity.service.rule.IActionChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName ActivityBaseActionChain
 * @Description
 * @Author lukcy
 * @Date 2024/12/28 11:11
 * @Version 1.0
 */
@Slf4j
@Component("activity_base_action")
public class ActivityBaseActionChain extends AbstractActionChain {
    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {

        log.info("活动责任链-基础信息【有效期、状态】校验开始。");

        return next().action(activitySkuEntity, activityEntity, activityCountEntity);

}
}
