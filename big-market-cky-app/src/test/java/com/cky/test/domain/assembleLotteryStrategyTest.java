package com.cky.test.domain;

import com.cky.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @ClassName assembleLotteryStrategyTest
 * @Description
 * @Author lukcy
 * @Date 2024/12/14 14:56
 * @Version 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class assembleLotteryStrategyTest {

    @Resource
    private IStrategyArmory strategyArmory;

    @Test
    public void testArmory(){
        boolean success = strategyArmory.assembleLotteryStrategy(10003L);
        log.info("测试结果：{}", success);
    }



}
