package com.cky.domain.strategy.model.valobj;

import com.cky.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import com.cky.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName StrategyAwardRuleModelVO
 * @Description  对应是数据库奖品的ruleModels 没有唯一ID
 * @Author lukcy
 * @Date 2024/12/17 9:47
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StrategyAwardRuleModelVO {

    private String ruleModels;

    public String[] raffleCenterRuleModels(){
        List<String> rulemodels=new ArrayList<>();
        String[] ruleModelValues = ruleModels.split(Constants.SPLIT);
        for (String ruleModelValue:ruleModelValues){
            if (DefaultLogicFactory.LogicModel.iscenter(ruleModelValue)){
                rulemodels.add(ruleModelValue);
            }
        }
        return rulemodels.toArray(new String[0]);
    }

    public String[] raffleAfterRuleModels(){
        List<String> rulemodels=new ArrayList<>();
        String[] ruleModelValues = ruleModels.split(Constants.SPLIT);
        for (String ruleModelValue:ruleModelValues){
            if (DefaultLogicFactory.LogicModel.isafter(ruleModelValue)){
                rulemodels.add(ruleModelValue);
            }
        }
        return rulemodels.toArray(new String[0]);
    }
}
