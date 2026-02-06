package com.example.todo.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.todo.entity.Todo;

@Mapper
public interface TodoMapper {

    List<Todo> selectPage(@Param("limit") int limit,
                          @Param("offset") int offset,
                          @Param("sortColumn") String sortColumn,
                          @Param("sortDir") String sortDir);

    long countAll();

    List<Todo> selectOverduePage(@Param("date") LocalDate date,
                                 @Param("limit") int limit,
                                 @Param("offset") int offset,
                                 @Param("sortColumn") String sortColumn,
                                 @Param("sortDir") String sortDir);

    long countOverdue(@Param("date") LocalDate date);

    List<Todo> selectOverdue(@Param("date") LocalDate date);

    int deleteByIds(@Param("ids") List<Long> ids);
}
