package ru.test.ManageSystem.mapper;

import ru.test.ManageSystem.DTO.CommentDto;
import ru.test.ManageSystem.entity.Comment;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(UserMapper.toDto(comment.getAuthor()))
                .createdAt(comment.getCreatedAt())
                .build();
    }

}