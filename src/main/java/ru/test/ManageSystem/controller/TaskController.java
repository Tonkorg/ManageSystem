package ru.test.ManageSystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ru.test.ManageSystem.DTO.TaskCreateDto;
import ru.test.ManageSystem.DTO.TaskDto;
import ru.test.ManageSystem.DTO.TaskFilterDto;
import ru.test.ManageSystem.enums.TaskStatus;
import ru.test.ManageSystem.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "API для управления задачами")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Создать задачу", description = "Создает новую задачу от имени текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskCreateDto dto) {
        return ResponseEntity.ok(taskService.createTask(dto));
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') and @taskService.isTaskAssigneeOrAuthor(#taskId)")
    @Operation(summary = "Обновить задачу", description = "Обновляет существующую задачу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
            @ApiResponse(responseCode = "403", description = "Нет доступа"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long taskId, @Valid @RequestBody TaskCreateDto dto) {
        return ResponseEntity.ok(taskService.updateTask(taskId, dto));
    }

    @PutMapping("/{taskId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить статус задачи", description = "Обновляет статус задачи (только для администратора)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус задачи успешно обновлен"),
            @ApiResponse(responseCode = "403", description = "Нет доступа"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<TaskDto> updateTaskStatus(@PathVariable Long taskId,
                                                    @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status));
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ADMIN') or @taskService.isTaskAuthor(#taskId)")
    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Нет доступа"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') and @taskService.isTaskAssigneeOrAuthor(#taskId)")
    @Operation(summary = "Получить задачу", description = "Возвращает задачу по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "403", description = "Нет доступа"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить все задачи", description = "Возвращает список всех задач текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно получен"),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Получить задачи с фильтрацией", description = "Возвращает отфильтрованный список задач с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно получен"),
            @ApiResponse(responseCode = "403", description = "Нет доступа")
    })
    public ResponseEntity<Page<TaskDto>> getTasks(@ModelAttribute TaskFilterDto filter, Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasks(filter, pageable));
    }
}