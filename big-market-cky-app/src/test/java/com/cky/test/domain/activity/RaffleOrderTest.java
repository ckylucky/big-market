package com.cky.test.domain.activity;


import com.alibaba.fastjson.JSON;

import com.cky.domain.activity.model.entity.ActivityOrderEntity;
import com.cky.domain.activity.model.entity.ActivityShopCartEntity;
import com.cky.domain.activity.model.entity.SkuRechargeEntity;
import com.cky.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.cky.domain.activity.service.armory.IActivityArmory;
import com.cky.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * @description 抽奖活动订单单测
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleOrderTest {

    @Resource
    private IRaffleActivityAccountQuotaService raffleOrder;

    @Resource
    private IActivityArmory activityArmory;

    @Test
    public void test_createRaffleActivityOrder() {
        ActivityShopCartEntity activityShopCartEntity = new ActivityShopCartEntity();
        activityShopCartEntity.setUserId("xiaofuge");
        activityShopCartEntity.setSku(9011L);
        ActivityOrderEntity raffleActivityOrder = raffleOrder.createRaffleActivityOrder(activityShopCartEntity);
        log.info("测试结果：{}", JSON.toJSONString(raffleActivityOrder));
    }
    @Test
    public void test_createSkuRechargeOrder() {
        SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
        skuRechargeEntity.setUserId("cky");
        skuRechargeEntity.setSku(9112L);
        // outBusinessNo 作为幂等仿重使用，同一个业务单号2次使用会抛出索引冲突 Duplicate entry '700091009111' for key 'uq_out_business_no' 确保唯一性。
        skuRechargeEntity.setOutBusinessNo("700091009098");
        String orderId = raffleOrder.createSkuRechargeOrder(skuRechargeEntity);
        log.info("测试结果：{}", orderId);
    }

    @Test
    public void activityArmory(){
        log.info("活动装配开始");
        activityArmory.assembleActivitySku(9112L);
    }
    /**
     * 测试库存消耗和最终一致更新
 * 1. raffle_activity_sku 库表库存可以设置20个
 * 2. 清空 redis 缓存 flushall
 * 3. for 循环20次，消耗完库存，最终数据库剩余库存为0
 */
    @Test
    public void test_createSkuRechargeOrder2() throws InterruptedException {
        for (int i = 0; i < 25; i++) {
            try {
                SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
                skuRechargeEntity.setUserId("cky");
                skuRechargeEntity.setSku(9112L);
                // outBusinessNo 作为幂等仿重使用，同一个业务单号2次使用会抛出索引冲突 Duplicate entry '700091009111' for key 'uq_out_business_no' 确保唯一性。
                skuRechargeEntity.setOutBusinessNo(RandomStringUtils.randomNumeric(12));
                String orderId = raffleOrder.createSkuRechargeOrder(skuRechargeEntity);
                log.info("测试结果：{}", orderId);
            } catch (AppException e) {
                log.warn(e.getInfo());
            }
        }
        new CountDownLatch(1).await();
    }

}
