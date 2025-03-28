package ru.test.ManageSystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.test.ManageSystem.DTO.CommentCreateDto;
import ru.test.ManageSystem.DTO.CommentDto;
import ru.test.ManageSystem.service.CommentService;

import java.util.List;

/**
 * Контроллер для управления комментариями к задачам.
 * Предоставляет методы для создания, обновления, удаления и получения комментариев,
 * связанных с конкретной задачей.
 */
@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "API для управления комментариями к задачам")
public class CommentController {

    private final CommentService commentService;

    /**
     * Создаёт новый комментарий к указанной задаче.
     * Доступно только для пользователей с ролями ADMIN или USER, которые являются автором
     * или исполнителем задачи.
     *
     * @param taskId идентификатор задачи, к которой добавляется комментарий
     * @param dto    объект {@link CommentCreateDto} с содержимым комментария
     * @return ResponseEntity с объектом {@link CommentDto}, представляющим созданный комментарий
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     * @throws jakarta.persistence.EntityNotFoundException если задача не найдена
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') and @taskService.isTaskAssigneeOrAuthor(#taskId)")
    @Operation(summary = "Создать комментарий", description = "Добавляет новый комментарий к задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно создан"),
            @ApiResponse(responseCode = "403", description = "Нет доступа"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<CommentDto> createComment(@PathVariable Long taskId,
                                                    @Valid @RequestBody CommentCreateDto dto) {
        return ResponseEntity.ok(commentService.createComment(taskId, dto.getContent()));
    }

    /**
     * Обновляет существующий комментарий к задаче.
     * Доступно только для пользователей с ролями ADMIN или USER, которые являются автором
     * или исполнителем задачи.
     *
     * @param taskId    идентификатор задачи, к которой относится комментарий
     * @param commentId идентификатор комментария, который нужно обновить
     * @param dto       объект {@link CommentCreateDto} с новым содержимым комментария
     * @return ResponseEntity с объектом {@link CommentDto}, представляющим обновлённый комментарий
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     * @throws jakarta.persistence.EntityNotFoundException если задача или комментарий не найдены
     */
    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') and @taskService.isTaskAssigneeOrAuthor(#taskId)")
    @Operation(summary = "Обновить комментарий", description = "Обновляет существующий комментарий")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно обновлен"),
            @ApiResponse(responseCode = "403", description = "Нет доступа"),
            @ApiResponse(responseCode = "404", description = "Комментарий или задача не найдены")
    })
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long taskId,
                                                    @PathVariable Long commentId,
                                                    @Valid @RequestBody CommentCreateDto dto) {
        return ResponseEntity.ok(commentService.updateComment(taskId, commentId, dto.getContent()));
    }

    /**
     * Удаляет комментарий по его идентификатору.
     * Доступно для администраторов или автора комментария.
     *
     * @param commentId идентификатор комментария, который нужно удалить
     * @return ResponseEntity с кодом 204 (No Content) при успешном удалении
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     * @throws jakarta.persistence.EntityNotFoundException если комментарий не найден
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentAuthor(#commentId)")
    @Operation(summary = "Удалить комментарий", description = "Удаляет комментарий по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Комментарий успешно удален"),
            @ApiResponse(responseCode = "403", description = "Нет доступа"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Возвращает список всех комментариев для указанной задачи.
     * Доступно только для пользователей с ролями ADMIN или USER, которые являются автором
     * или исполнителем задачи.
     *
     * @param taskId идентификатор задачи, для которой запрашиваются комментарии
     * @return ResponseEntity со списком объектов {@link CommentDto}, представляющих комментарии
     * @throws org.springframework.security.access.AccessDeniedException если у пользователя нет прав
     * @throws jakarta.persistence.EntityNotFoundException если задача не найдена
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER') and @taskService.isTaskAssigneeOrAuthor(#taskId)")
    @Operation(summary = "Получить комментарии задачи", description = "Возвращает все комментарии для указанной задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список комментариев успешно получен"),
            @ApiResponse(responseCode = "403", description = "Нет доступа"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public ResponseEntity<List<CommentDto>> getCommentsByTaskId(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId));
    }
}