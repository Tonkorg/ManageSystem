package ru.test.ManageSystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.test.ManageSystem.DTO.CommentDto;
import ru.test.ManageSystem.entity.Comment;
import ru.test.ManageSystem.entity.User;
import ru.test.ManageSystem.exception.ResourceNotFoundException;
import ru.test.ManageSystem.mapper.CommentMapper;
import ru.test.ManageSystem.repository.CommentRepository;
import ru.test.ManageSystem.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления комментариями к задачам.
 * Предоставляет методы для создания, обновления, удаления и получения комментариев,
 * а также проверки авторства комментария.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    /**
     * Создаёт новый комментарий к задаче.
     * Связывает комментарий с задачей и текущим пользователем, устанавливает время создания.
     *
     * @param taskId  идентификатор задачи, к которой добавляется комментарий
     * @param content текст комментария
     * @return объект {@link CommentDto}, представляющий созданный комментарий
     * @throws ResourceNotFoundException если задача с указанным идентификатором не найдена
     */
    @Transactional
    public CommentDto createComment(Long taskId, String content) {
        Comment comment = Comment.builder()
                .content(content)
                .task(taskRepository.findById(taskId)
                        .orElseThrow(() -> new ResourceNotFoundException("Task not found")))
                .author(userService.getCurrentUser())
                .createdAt(LocalDateTime.now())
                .build();
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    /**
     * Обновляет существующий комментарий.
     * Проверяет, что комментарий принадлежит указанной задаче, и обновляет его содержимое.
     *
     * @param taskId    идентификатор задачи, к которой относится комментарий
     * @param commentId идентификатор комментария, который нужно обновить
     * @param content   новое содержимое комментария
     * @return объект {@link CommentDto}, представляющий обновлённый комментарий
     * @throws ResourceNotFoundException если комментарий не найден
     * @throws IllegalArgumentException  если комментарий не принадлежит указанной задаче
     */
    @Transactional
    public CommentDto updateComment(Long taskId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        if (!comment.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("Comment does not belong to this task");
        }
        comment.setContent(content);
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    /**
     * Удаляет комментарий по его идентификатору.
     *
     * @param commentId идентификатор комментария, который нужно удалить
     * @throws ResourceNotFoundException если комментарий не найден
     */
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        commentRepository.delete(comment);
    }

    /**
     * Возвращает список всех комментариев для указанной задачи.
     * Проверяет существование задачи перед получением комментариев.
     *
     * @param taskId идентификатор задачи, для которой запрашиваются комментарии
     * @return список объектов {@link CommentDto}, представляющих комментарии
     * @throws ResourceNotFoundException если задача не найдена
     */
    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return commentRepository.findByTaskId(taskId)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, является ли текущий пользователь автором комментария.
     *
     * @param commentId идентификатор комментария для проверки
     * @return {@code true}, если текущий пользователь является автором комментария, иначе {@code false}
     * @throws ResourceNotFoundException если комментарий не найден
     */
    public boolean isCommentAuthor(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        User currentUser = userService.getCurrentUser();
        return comment.getAuthor().getId().equals(currentUser.getId());
    }
}