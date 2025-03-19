package ru.test.ManageSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.test.ManageSystem.enums.TaskPriority;
import ru.test.ManageSystem.enums.TaskStatus;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskFilterDto {
    private TaskStatus status;
    private TaskPriority priority;
    private Long authorId;
    private Long assigneeId;
}