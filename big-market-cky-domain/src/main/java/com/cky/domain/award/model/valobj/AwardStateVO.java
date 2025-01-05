package com.cky.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName AwardStateVO
 * @Description 奖品状态枚举值对象 【值对象，用于描述对象属性的值，一个对象中，一个属性，有多个状态值。】
 * @Author lukcy
 * @Date 2025/1/5 11:06
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum AwardStateVO {
    create("create", "创建"),
    complete("complete", "发奖完成"),
    fail("fail", "发奖失败"),
    ;
    private final String code;
    private final String info;

}
