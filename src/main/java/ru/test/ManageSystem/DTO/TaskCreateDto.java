package ru.test.ManageSystem.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(max = 100, message = "Заголовок не должен превышать 100 символов")
    private String title;

    @Size(max = 500, message = "Описание не должно превышать 500 символов")
    private String description;

    @NotNull(message = "Приоритет не может быть пустым")
    private TaskPriority priority;

    private Long assigneeId;
}