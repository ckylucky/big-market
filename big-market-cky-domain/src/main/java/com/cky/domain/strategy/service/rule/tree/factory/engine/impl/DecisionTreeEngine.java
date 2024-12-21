package com.cky.domain.strategy.service.rule.tree.factory.engine.impl;


import com.cky.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.cky.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import com.cky.domain.strategy.model.valobj.RuleTreeNodeVO;
import com.cky.domain.strategy.model.valobj.RuleTreeVO;
import com.cky.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.cky.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.cky.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 决策树引擎
 * @create 2024-01-27 11:34
 */
@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardData process(String userId, Long strategyId, Integer awardId) {
        DefaultTreeFactory.StrategyAwardData strategyAwardData = null;

        // 获取基础信息
        String nextNode = ruleTreeVO.getTreeRootRuleNode();//根节点
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();//规则树的map
        // 获取起始节点「根节点记录了第一个要执行的规则」
        RuleTreeNodeVO ruleTreeNode = treeNodeMap.get(nextNode);
        //获得每个节点的 rule_value值，以便后续具体的实现去根据其来操作
        String ruleValue = ruleTreeNode.getRuleValue();
        while (null != nextNode) {
            // 获取对应的决策节点  根据树节点的规则key  通过我们的map获得决策的具体实现类
            ILogicTreeNode logicTreeNode = logicTreeNodeGroup.get(ruleTreeNode.getRuleKey());

            // 决策节点计算  得到信息 包括放行或者接管 以及对应的抽奖奖品id和规则
            DefaultTreeFactory.TreeActionEntity logicEntity = logicTreeNode.logic(userId, strategyId, awardId,ruleValue);
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = logicEntity.getRuleLogicCheckType();
            strategyAwardData = logicEntity.getStrategyAwardData();
            log.info("决策树引擎【{}】treeId:{} node:{} code:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), nextNode, ruleLogicCheckTypeVO.getCode());

            // 获取下个节点  通过当前节点的接管以及放行状态 获取当前节点连线对应的节点
            //比如 lock 如果是ALLOW 则会找到 stock 反之会找到luck_award
            nextNode = nextNode(ruleLogicCheckTypeVO.getCode(), ruleTreeNode.getTreeNodeLineVOList());
            ruleTreeNode = treeNodeMap.get(nextNode); //根据下一个节点的名字 得到其  todo 与nextNode区别 nextnode只是一个string的key 通过key找到具体的树节点
        }

        // 返回最终结果
        return strategyAwardData;
    }

    public String nextNode(String matterValue, List<RuleTreeNodeLineVO> treeNodeLineVOList) {
        if (null == treeNodeLineVOList || treeNodeLineVOList.isEmpty()) return null;
        for (RuleTreeNodeLineVO nodeLine : treeNodeLineVOList) {
            if (decisionLogic(matterValue, nodeLine)) {
                return nodeLine.getRuleNodeTo();
            }
        }
        //rule_stock节点扣减完库存后，无法正常退出。因为这种情况下就是到达不了rule_luck_award的，会导致抛出异常  如果都不可达，应该返回null
        //throw new RuntimeException("决策树引擎，nextNode 计算失败，未找到可执行节点！");
        return null;
    }

    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine) {
        switch (nodeLine.getRuleLimitType()) {
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            // 以下规则暂时不需要实现
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }

}
