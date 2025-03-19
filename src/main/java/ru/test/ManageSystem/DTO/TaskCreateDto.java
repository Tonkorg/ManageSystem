package ru.test.ManageSystem.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.test.ManageSystem.enums.TaskPriority;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateDto {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private TaskPriority priority;
    private Long assigneeId;
}