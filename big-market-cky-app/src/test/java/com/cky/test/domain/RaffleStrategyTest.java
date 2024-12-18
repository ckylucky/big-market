package com.cky.test.domain;


import com.alibaba.fastjson.JSON;
import com.cky.domain.strategy.model.entity.RaffleAwardEntity;
import com.cky.domain.strategy.model.entity.RaffleFactorEntity;

import com.cky.domain.strategy.service.IRaffleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖策略测试
 * @create 2024-01-06 13:28
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTest {

    @Resource
    private IRaffleStrategy raffleStrategy;




    @Test
    public void test_performRaffle() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("cky")
                .strategyId(10003L)
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    //INFO  DefaultRaffleStrategy  - 抽奖前规则过滤 userId: cky ruleModel: rule_weight code: 0000 info: 放行；执行后续的流程，不受规则引擎影响
        //24-12-16.10:21:40.644 [main            ] INFO  RaffleStrategyTest     - 请求参数：{"strategyId":10002,"userId":"cky"}
        //24-12-16.10:21:42.406 [main            ] INFO  RaffleStrategyTest     - 测试结果：{"awardId":109}
    }

    @Test
    public void test_performRaffle_blacklist() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user003")  // 黑名单用户 user001,user002,user003
                .strategyId(10002L)
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));

        //24-12-16.10:50:42.442 [main            ] INFO  RaffleStrategyTest     - 请求参数：{"strategyId":10002,"userId":"user003"}
        //24-12-16.10:50:42.505 [main            ] INFO  RaffleStrategyTest     - 测试结果：{"awardId":100}
    }

}
