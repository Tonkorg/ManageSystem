package ru.test.ManageSystem.mapper;

import ru.test.ManageSystem.DTO.CommentDto;
import ru.test.ManageSystem.entity.Comment;

/**
 * Утилитный класс для преобразования сущности {@link Comment} в объект передачи данных {@link CommentDto}.
 */
public class CommentMapper {

    /**
     * Преобразует сущность {@link Comment} в объект {@link CommentDto}.
     * Копирует идентификатор, содержимое, автора и время создания комментария.
     *
     * @param comment сущность {@link Comment}, представляющая комментарий
     * @return объект {@link CommentDto} с данными комментария
     */
    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(UserMapper.toDto(comment.getAuthor()))
                .createdAt(comment.getCreatedAt())
                .build();
    }
}