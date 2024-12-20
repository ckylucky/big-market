package com.cky.domain.strategy.service.raffle;

import com.cky.domain.strategy.model.entity.RaffleAwardEntity;
import com.cky.domain.strategy.model.entity.StrategyAwardEntity;
import com.cky.domain.strategy.model.valobj.RuleTreeVO;
import com.cky.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.cky.domain.strategy.repository.IStrategyRepository;
import com.cky.domain.strategy.service.AbstractRaffleStrategy;
import com.cky.domain.strategy.service.armory.IStrategyDispatch;
import com.cky.domain.strategy.service.rule.chain.ILogicChain;
import com.cky.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.cky.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import com.cky.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.cky.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 默认的抽奖策略实现
 * @create 2024-01-06 11:46
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {


    public DefaultRaffleStrategy(DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory, IStrategyRepository repository, IStrategyDispatch strategyDispatch) {
        super(defaultChainFactory, defaultTreeFactory, repository, strategyDispatch);
    }

    /*
    * 抽奖前 责任链
    * */
    @Override
    protected DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        // 2. 获取抽奖责任链 - 前置规则的责任链处理
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);

        return logicChain.logic(userId, strategyId);
    }

    /**
     * 抽奖中和后 树型结构
     * @param userId  用户id
     * @param strategyId   策略id
     * @param awardId   奖品id
     * @return
     */
    @Override
    protected DefaultTreeFactory.StrategyAwardData raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        //通过策略id和奖品id去查找奖品对应的模型
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModelVO(strategyId, awardId);
        if (null == strategyAwardRuleModelVO) {
            return DefaultTreeFactory.StrategyAwardData.builder().awardId(awardId).build();
        }
        //通过树的id 查找这棵规则树  这里是model 并不是树的id呀
        RuleTreeVO ruleTreeVO = repository.queryRuleTreeVOByTreeId(strategyAwardRuleModelVO.getRuleModels());
        if (null == ruleTreeVO) {
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 " + strategyAwardRuleModelVO.getRuleModels());
        }
        IDecisionTreeEngine treeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);
        return treeEngine.process(userId, strategyId, awardId);
    }


}
