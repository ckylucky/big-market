package com.cky.test;

import com.cky.infrastructure.persistent.dao.IAwardDao;
import com.cky.infrastructure.persistent.dao.IstrategyAwardDao;
import com.cky.infrastructure.persistent.dao.IstrategyDao;
import com.cky.infrastructure.persistent.dao.IstrategyRuleDao;
import com.cky.infrastructure.persistent.po.Award;
import com.cky.infrastructure.persistent.po.strategy;
import com.cky.infrastructure.persistent.po.strategyAward;
import com.cky.infrastructure.persistent.po.strategyRule;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    public void test1(){
        List<strategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardList();
        System.out.println(strategyAwards);
        List<Award> awards = awardDao.queryAwardList();
        List<strategyRule> strategyRules = strategyRuleDao.queryStrategyRuleList();
        List<strategy> istrategyDao = strategyDao.queryStrategyList();
        System.out.println(awards);
        System.out.println(strategyRules);
        System.out.println(istrategyDao);

}}
