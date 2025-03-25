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

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

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

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        commentRepository.delete(comment);
    }

    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return commentRepository.findByTaskId(taskId)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    public boolean isCommentAuthor(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        User currentUser = userService.getCurrentUser();
        return comment.getAuthor().getId().equals(currentUser.getId());
    }
}