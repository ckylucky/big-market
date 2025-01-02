package com.cky.domain.activity.service.rule.factory;

import com.cky.domain.activity.service.rule.AbstractActionChain;
import com.cky.domain.activity.service.rule.IActionChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName DefaultActivityChainFactory
 * @Description
 * @Author lukcy
 * @Date 2024/12/28 11:12
 * @Version 1.0
 */
@Service
public class DefaultActivityChainFactory {


    private final IActionChain actionChain;

    public DefaultActivityChainFactory(Map<String, IActionChain> actionChainGroup){
       actionChain = actionChainGroup.get(ActionModel.activity_base_action.code);
       actionChain.appendNext(actionChainGroup.get(ActionModel.activity_sku_stock_action.code));
    }

    public IActionChain openActionChain() {
        return this.actionChain;
    }

    @Getter
    @AllArgsConstructor
    public enum ActionModel {

        activity_base_action("activity_base_action", "活动的库存、时间校验"),
        activity_sku_stock_action("activity_sku_stock_action", "活动sku库存"),
        ;

        private final String code;
        private final String info;

    }
}
