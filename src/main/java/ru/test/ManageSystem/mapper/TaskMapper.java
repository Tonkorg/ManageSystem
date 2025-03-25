package ru.test.ManageSystem.mapper;

import ru.test.ManageSystem.DTO.TaskDto;
import ru.test.ManageSystem.entity.Task;

/**
 * Утилитный класс для преобразования сущности {@link Task} в объект передачи данных {@link TaskDto}.
 */
public class TaskMapper {

    /**
     * Преобразует сущность {@link Task} в объект {@link TaskDto}.
     * Копирует идентификатор, заголовок, описание, статус, приоритет, идентификаторы автора и исполнителя,
     * а также время создания и обновления задачи.
     *
     * @param task сущность {@link Task}, представляющая задачу
     * @return объект {@link TaskDto} с данными задачи
     */
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