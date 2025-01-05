package com.cky.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName UserRaffleOrderStateVO
 * @Description 用户抽奖订单状态枚举
 * @Author lukcy
 * @Date 2025/1/3 20:18
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum UserRaffleOrderStateVO {
    create("create", "创建"),
    used("used", "已使用"),
    cancel("cancel", "已作废"),
    ;

    private final String code;
    private final String desc;
}
