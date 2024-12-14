package com.cky.test.infrastructure;

import com.alibaba.fastjson.JSON;
import com.cky.infrastructure.persistent.dao.IAwardDao;
import com.cky.infrastructure.persistent.dao.IstrategyAwardDao;
import com.cky.infrastructure.persistent.dao.IstrategyDao;
import com.cky.infrastructure.persistent.dao.IstrategyRuleDao;
import com.cky.infrastructure.persistent.po.Award;
import com.cky.infrastructure.persistent.po.strategy;
import com.cky.infrastructure.persistent.po.strategyAward;
import com.cky.infrastructure.persistent.po.strategyRule;
import com.cky.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {


    @Resource
    private IstrategyAwardDao strategyAwardDao;
    @Resource
    private IAwardDao awardDao;
    @Resource
    private IstrategyRuleDao strategyRuleDao;
    @Resource
    private IstrategyDao strategyDao;


    @Test
    public void test1() {

        List<strategyAward> strategyAwards =  strategyAwardDao.queryStrategyAwardListByStrategyId(10002L);
        log.info("测试结果：{}", JSON.toJSONString(strategyAwards));

    }
    @Resource
    private IRedisService redisService;
    @Test
    public void test2(){

        RMap<Object, Object> map = redisService.getMap("strategy_id_10001");
        map.put(1,"101");
        map.put(2,"101");
        map.put(3,"101");
        map.put(4,"102");
        System.out.println(redisService.getFromMap("strategy_id_10001", 1).toString());

    }
}
