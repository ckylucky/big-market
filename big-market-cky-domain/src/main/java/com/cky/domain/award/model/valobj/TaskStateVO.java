package com.cky.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName TaskStateVO
 * @Description 任务状态值对象
 * @Author lukcy
 * @Date 2025/1/5 11:10
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum TaskStateVO {
    create("create", "创建"),
    complete("complete", "发送完成"),
    fail("fail", "发送失败"),
    ;

    private final String code;
    private final String desc;

}
