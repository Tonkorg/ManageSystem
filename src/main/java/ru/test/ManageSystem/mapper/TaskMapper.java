package ru.test.ManageSystem.mapper;


import ru.test.ManageSystem.DTO.TaskDto;
import ru.test.ManageSystem.entity.Task;

public class TaskMapper {

    public static TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .authorId(task.getAuthor().getId())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}