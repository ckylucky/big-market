package com.cky.domain.activity.service.quota;


import com.cky.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.cky.domain.activity.model.entity.ActivityCountEntity;
import com.cky.domain.activity.model.entity.ActivityEntity;
import com.cky.domain.activity.model.entity.ActivitySkuEntity;
import com.cky.domain.activity.repository.IActivityRepository;
import com.cky.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖活动的支撑类
 * @create 2024-03-23 09:27
 */
public class RaffleActivityAccountQuotaSupport {

    protected DefaultActivityChainFactory defaultActivityChainFactory;

    protected IActivityRepository activityRepository;

    public RaffleActivityAccountQuotaSupport(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory) {
        this.activityRepository = activityRepository;
        this.defaultActivityChainFactory = defaultActivityChainFactory;
    }

    public ActivitySkuEntity queryActivitySku(Long sku) {
        return activityRepository.queryActivitySku(sku);
    }

    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        return activityRepository.queryRaffleActivityByActivityId(activityId);
    }

    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        return activityRepository.queryRaffleActivityCountByActivityCountId(activityCountId);
    }

    public void doSaveOrder(CreateQuotaOrderAggregate createOrderAggregate) {
        activityRepository.doSaveOrder(createOrderAggregate);
    }
}
