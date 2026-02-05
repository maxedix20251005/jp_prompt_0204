package com.example.todo.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.todo.entity.Todo;

@Mapper
public interface TodoMapper {

    List<Todo> selectOverdue(@Param("date") LocalDate date);
}
