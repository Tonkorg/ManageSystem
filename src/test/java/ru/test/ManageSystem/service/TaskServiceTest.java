package ru.test.ManageSystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.test.ManageSystem.DTO.TaskCreateDto;
import ru.test.ManageSystem.DTO.TaskDto;
import ru.test.ManageSystem.DTO.TaskFilterDto;
import ru.test.ManageSystem.entity.Task;
import ru.test.ManageSystem.entity.User;
import ru.test.ManageSystem.enums.TaskPriority;
import ru.test.ManageSystem.enums.TaskStatus;
import ru.test.ManageSystem.exception.ResourceNotFoundException;
import ru.test.ManageSystem.repository.TaskRepository;
import ru.test.ManageSystem.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;
    private TaskCreateDto taskCreateDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.PENDING)
                .author(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        taskCreateDto = new TaskCreateDto();
        taskCreateDto.setTitle("New Task");
        taskCreateDto.setDescription("New Description");
        taskCreateDto.setPriority(TaskPriority.HIGH);
    }

    @Test
    void createTask_ShouldReturnTaskDto() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto result = taskService.createTask(taskCreateDto);

        assertNotNull(result);
        assertEquals(task.getTitle(), result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldReturnUpdatedTaskDto() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));


        Task updatedTask = Task.builder()
                .id(1L)
                .title(taskCreateDto.getTitle())
                .description(taskCreateDto.getDescription())
                .priority(taskCreateDto.getPriority())
                .status(TaskStatus.PENDING)
                .author(user)
                .assignee(null)
                .comments(task.getComments())
                .createdAt(task.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();


        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);


        TaskDto result = taskService.updateTask(1L, taskCreateDto);


        assertNotNull(result);
        assertEquals(taskCreateDto.getTitle(), result.getTitle());
        assertEquals(taskCreateDto.getDescription(), result.getDescription());
        assertEquals(taskCreateDto.getPriority(), result.getPriority());
        verify(taskRepository, times(1)).save(any(Task.class));
    }



    @Test
    void updateTaskStatus_ShouldReturnUpdatedTaskDto() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDto result = taskService.updateTaskStatus(1L, TaskStatus.COMPLETED);

        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(task);
    }


    @Test
    void getTaskById_ShouldReturnTaskDto() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDto result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(task.getTitle(), result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_ShouldThrowResourceNotFoundException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L));
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getAllTasks_ShouldReturnTasksForCurrentUser() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findByAuthorOrAssignee(user, user)).thenReturn(Collections.singletonList(task));

        List<TaskDto> result = taskService.getAllTasks();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByAuthorOrAssignee(user, user);
    }

    @Test
    void isTaskAssigneeOrAuthor_ShouldReturnTrue() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(user);

        assertTrue(taskService.isTaskAssigneeOrAuthor(1L));
    }

    @Test
    void isTaskAuthor_ShouldReturnTrue() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(user);

        assertTrue(taskService.isTaskAuthor(1L));
    }
}
