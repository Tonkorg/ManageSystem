package ru.test.ManageSystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.test.ManageSystem.DTO.CommentDto;
import ru.test.ManageSystem.entity.Comment;
import ru.test.ManageSystem.entity.Task;
import ru.test.ManageSystem.entity.User;
import ru.test.ManageSystem.exception.ResourceNotFoundException;
import ru.test.ManageSystem.mapper.CommentMapper;
import ru.test.ManageSystem.repository.CommentRepository;
import ru.test.ManageSystem.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Task task;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");

        comment = Comment.builder()
                .id(1L)
                .content("Test Comment")
                .task(task)
                .author(user)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createComment_ShouldReturnCommentDto() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.createComment(1L, "Test Comment");

        assertNotNull(result);
        assertEquals(comment.getContent(), result.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_ShouldThrowResourceNotFoundException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(1L, "Test Comment"));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateComment_ShouldReturnUpdatedCommentDto() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.updateComment(1L, 1L, "Updated Comment");

        assertNotNull(result);
        assertEquals("Updated Comment", result.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_ShouldThrowResourceNotFoundException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(1L, 1L, "Updated Comment"));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateComment_ShouldThrowIllegalArgumentException() {
        Task task = new Task();
        task.setId(2L);

        Comment invalidComment = Comment.builder()
                .id(2L)
                .content("Invalid Comment")
                .task(task)
                .author(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(commentRepository.findById(2L)).thenReturn(Optional.of(invalidComment));

        assertThrows(IllegalArgumentException.class, () -> commentService.updateComment(1L, 2L, "Updated Comment"));
        verify(commentRepository, never()).save(any(Comment.class));
    }


    @Test
    void deleteComment_ShouldDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_ShouldThrowResourceNotFoundException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(1L));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void getCommentsByTaskId_ShouldReturnCommentDtos() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(commentRepository.findByTaskId(1L)).thenReturn(Collections.singletonList(comment));

        List<CommentDto> result = commentService.getCommentsByTaskId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(comment.getContent(), result.get(0).getContent());
        verify(commentRepository, times(1)).findByTaskId(1L);
    }

    @Test
    void getCommentsByTaskId_ShouldThrowResourceNotFoundException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentsByTaskId(1L));
        verify(commentRepository, never()).findByTaskId(any(Long.class));
    }

    @Test
    void isCommentAuthor_ShouldReturnTrue() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userService.getCurrentUser()).thenReturn(user);

        assertTrue(commentService.isCommentAuthor(1L));
    }

    @Test
    void isCommentAuthor_ShouldThrowResourceNotFoundException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.isCommentAuthor(1L));
    }
}
