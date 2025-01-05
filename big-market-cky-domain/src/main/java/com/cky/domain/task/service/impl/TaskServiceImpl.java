package com.cky.domain.task.service.impl;

import com.cky.domain.task.model.entity.TaskEntity;
import com.cky.domain.task.repository.ITaskRepository;
import com.cky.domain.task.service.ITaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName TaskServiceImpl
 * @Description
 * @Author lukcy
 * @Date 2025/1/5 11:48
 * @Version 1.0
 */
@Service
public class TaskServiceImpl implements ITaskService {


    @Resource
    private ITaskRepository taskRepository;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {

        return taskRepository.queryNoSendMessageTaskList();
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {

        taskRepository.sendMessage(taskEntity);
    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        taskRepository.updateTaskSendMessageCompleted(userId, messageId);
    }

    @Override
    public void updateTaskSendMessageFail(String userId, String messageId) {
        taskRepository.updateTaskSendMessageFail(userId, messageId);
    }
}
