package com.cky.domain.activity.service;


import com.alibaba.fastjson.JSON;
import com.cky.domain.activity.model.aggregate.CreateOrderAggregate;
import com.cky.domain.activity.model.entity.*;
import com.cky.domain.activity.repository.IActivityRepository;
import com.cky.domain.activity.service.rule.IActionChain;
import com.cky.domain.activity.service.rule.factory.DefaultActivityChainFactory;
import com.cky.types.enums.ResponseCode;
import com.cky.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖活动抽象类，定义标准的流程
 * @create 2024-03-16 08:42
 */
@Slf4j
public abstract class AbstractRaffleActivity extends RaffleActivitySupport implements IRaffleOrder  {


    public AbstractRaffleActivity(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory) {
        super(activityRepository, defaultActivityChainFactory);
    }

    @Override
    public ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity) {


        // 1. 通过sku查询活动信息
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(activityShopCartEntity.getSku());
        // 2. 查询活动信息
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        // 3. 查询次数信息（用户在活动上可参与的次数）
        ActivityCountEntity activityCountEntity = activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        log.info("查询结果：{} {} {}", JSON.toJSONString(activitySkuEntity), JSON.toJSONString(activityEntity), JSON.toJSONString(activityCountEntity));

        return ActivityOrderEntity.builder().build();
    }
    public String createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity) {
        //1、参数校验
        String userId = skuRechargeEntity.getUserId();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        Long sku = skuRechargeEntity.getSku();
        if(sku!=null&& StringUtils.isBlank(userId)&&StringUtils.isBlank(outBusinessNo)){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        //2、查询活动，次数实体
        //2，1 通过skuid查询sku实体
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        //2.2 查询活动信息
        ActivityEntity activityEntity = queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        //2.3 查询次数配置
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        //3、开启责任链，校验活动相关比如有效期库存等 todo 具体实现后边做
        IActionChain actionChain = defaultActivityChainFactory.openActionChain();
        boolean success = actionChain.action(activitySkuEntity, activityEntity, activityCountEntity);
        //4、创建聚合对象
        CreateOrderAggregate createOrderAggregate= buildOrderAggregate(skuRechargeEntity,activitySkuEntity,activityEntity,activityCountEntity);
        //5、保存订单
        doSaveOrder(createOrderAggregate);

        //6、返回单号

        return createOrderAggregate.getActivityOrderEntity().getOrderId();
    }
    //创建聚合对象 因为这里保存订单的时候有两个操作，一个时更新活动账户或者insert 一个是创建订单，所以这里聚合对象是两个实体的聚合
    //一个是活动账户实体 一个是订单实体  活动账户实体我们并不需要那么多字段
    protected abstract CreateOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity,ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) ;

}
