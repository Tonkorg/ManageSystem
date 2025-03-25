package ru.test.ManageSystem.service;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.test.ManageSystem.DTO.TaskCreateDto;
import ru.test.ManageSystem.DTO.TaskDto;
import ru.test.ManageSystem.DTO.TaskFilterDto;
import ru.test.ManageSystem.entity.Task;
import ru.test.ManageSystem.entity.User;
import ru.test.ManageSystem.enums.TaskStatus;
import ru.test.ManageSystem.exception.ResourceNotFoundException;
import ru.test.ManageSystem.mapper.TaskMapper;
import ru.test.ManageSystem.repository.TaskRepository;
import ru.test.ManageSystem.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public TaskDto createTask(TaskCreateDto dto) {
        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .status(TaskStatus.PENDING)
                .author(userService.getCurrentUser())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (dto.getAssigneeId() != null) {
            task.setAssignee(userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found")));
        }

        return TaskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto updateTask(Long id, TaskCreateDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task = Task.builder()
                .id(task.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .status(task.getStatus())
                .author(task.getAuthor())
                .assignee(dto.getAssigneeId() != null ?
                        userRepository.findById(dto.getAssigneeId())
                                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found")) :
                        task.getAssignee())
                .comments(task.getComments())
                .createdAt(task.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        return TaskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public TaskDto updateTaskStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        return TaskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        taskRepository.delete(task);
    }

    public Page<TaskDto> getTasks(TaskFilterDto filter, Pageable pageable) {
        Specification<Task> spec = buildSpecification(filter);
        User currentUser = userService.getCurrentUser();
        if (!currentUser.getRoles().contains("ADMIN")) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.equal(root.get("author").get("id"), currentUser.getId()),
                    cb.equal(root.get("assignee").get("id"), currentUser.getId())
            ));
        }
        return taskRepository.findAll(spec, pageable)
                .map(TaskMapper::toDto);
    }

    public TaskDto getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        return TaskMapper.toDto(task);
    }

    public List<TaskDto> getAllTasks() {
        User currentUser = userService.getCurrentUser();
        List<Task> tasks = taskRepository.findByAuthorOrAssignee(currentUser, currentUser);
        return tasks.stream().map(TaskMapper::toDto).collect(Collectors.toList());
    }

    public boolean isTaskAssigneeOrAuthor(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User currentUser = userService.getCurrentUser();
        return task.getAuthor().getId().equals(currentUser.getId()) ||
                (task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId()));
    }

    public boolean isTaskAuthor(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        User currentUser = userService.getCurrentUser();
        return task.getAuthor().getId().equals(currentUser.getId());
    }

    private Specification<Task> buildSpecification(TaskFilterDto filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }
            if (filter.getPriority() != null) {
                predicates.add(cb.equal(root.get("priority"), filter.getPriority()));
            }
            if (filter.getAuthorId() != null) {
                predicates.add(cb.equal(root.get("author").get("id"), filter.getAuthorId()));
            }
            if (filter.getAssigneeId() != null) {
                predicates.add(cb.equal(root.get("assignee").get("id"), filter.getAssigneeId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}