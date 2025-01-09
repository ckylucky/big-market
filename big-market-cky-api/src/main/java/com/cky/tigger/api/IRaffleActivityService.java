package com.cky.tigger.api;

import com.cky.tigger.api.dao.ActivityDrawRequestDTO;
import com.cky.tigger.api.dao.ActivityDrawResponseDTO;
import com.cky.types.model.Response;

/**
 * @ClassName IRaffleActivityService
 * @Description  抽奖活动服务的api接口
 * @Author lukcy
 * @Date 2025/1/8 10:47
 * @Version 1.0
 */
public interface IRaffleActivityService {

    /**
     * 活动装配
     * @param activity
     * @return
     */
    Response<Boolean> armory(Long activity);

    /**
     * 活动抽奖接口
     * @param request 请求对象
     * @return 返回结果
     */
    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDTO request);
}
