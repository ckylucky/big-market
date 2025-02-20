package com.cky.tigger.api;

import com.cky.tigger.api.dao.ActivityDrawRequestDTO;
import com.cky.tigger.api.dao.ActivityDrawResponseDTO;
import com.cky.tigger.api.dao.UserActivityAccountRequestDTO;
import com.cky.tigger.api.dao.UserActivityAccountResponseDTO;
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

    Response<Boolean> calendarSignRebate(String userid);

    /**
     * 判断是否完成日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到结果 true 已签到，false 未签到
     */
    Response<Boolean> isCalendarSignRebate(String userId);

    /**
     * 查询用户活动账户
     *
     * @param request 请求对象「活动ID、用户ID」
     * @return 返回结果「总额度、月额度、日额度」
     */
    Response<UserActivityAccountResponseDTO> queryUserActivityAccount(UserActivityAccountRequestDTO request);

}
