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

/**
 * Контроллер для управления задачами.
 * Предоставляет методы для создания, обновления, удаления и получения задач
 * с учетом прав доступа текущего пользователя.
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "API для управления задачами")
public class TaskController {

    private final TaskService taskService;

    /**
     * Создаёт новую задачу от имени текущего пользователя.
     * Доступно для пользователей с ролями ADMIN или USER.
     *
     * @param dto объект {@link TaskCreateDto} с данными для создания задачи
     * @return ResponseEntity с объектом {@link TaskDto}, представляющим созданную задачу
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     */
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

    /**
     * Обновляет существующую задачу.
     * Доступно для пользователей с ролями ADMIN или USER, которые являются автором или исполнителем задачи.
     *
     * @param taskId идентификатор задачи, которую нужно обновить
     * @param dto    объект {@link TaskCreateDto} с новыми данными задачи
     * @return ResponseEntity с объектом {@link TaskDto}, представляющим обновлённую задачу
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     * @throws jakarta.persistence.EntityNotFoundException если задача не найдена
     */
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

    /**
     * Обновляет статус задачи.
     * Доступно только для пользователей с ролью ADMIN.
     *
     * @param taskId идентификатор задачи, статус которой нужно обновить
     * @param status новый статус задачи из перечисления {@link TaskStatus}
     * @return ResponseEntity с объектом {@link TaskDto}, представляющим задачу с обновлённым статусом
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     * @throws jakarta.persistence.EntityNotFoundException если задача не найдена
     */
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

    /**
     * Удаляет задачу по её идентификатору.
     * Доступно для администраторов или автора задачи.
     *
     * @param taskId идентификатор задачи, которую нужно удалить
     * @return ResponseEntity с кодом 204 (No Content) при успешном удалении
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     * @throws jakarta.persistence.EntityNotFoundException если задача не найдена
     */
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

    /**
     * Возвращает задачу по её идентификатору.
     * Доступно для пользователей с ролями ADMIN или USER, которые являются автором или исполнителем задачи.
     *
     * @param taskId идентификатор задачи
     * @return ResponseEntity с объектом {@link TaskDto}, представляющим задачу
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     * @throws jakarta.persistence.EntityNotFoundException если задача не найдена
     */
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

    /**
     * Возвращает список всех задач текущего пользователя.
     * Доступно для пользователей с ролями ADMIN или USER.
     *
     * @return ResponseEntity со списком объектов {@link TaskDto}, представляющих задачи
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     */
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

    /**
     * Возвращает отфильтрованный список задач с пагинацией.
     * Доступно для пользователей с ролями ADMIN или USER.
     *
     * @param filter   объект {@link TaskFilterDto} с параметрами фильтрации
     * @param pageable объект {@link Pageable} для настройки пагинации и сортировки
     * @return ResponseEntity с объектом {@link Page} содержащим список {@link TaskDto}
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     */
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