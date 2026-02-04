package com.example.todo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.todo.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // Filter by completion status.
    List<Todo> findByCompleted(Boolean completed);

    // Partial match on title.
    List<Todo> findByTitleContainingIgnoreCase(String keyword);

    // Due date on or before the given date.
    List<Todo> findByDueDateLessThanEqual(LocalDate date);

    // Sort by priority (ascending).
    List<Todo> findAllByOrderByPriorityAsc();

    // Example of using @Query for a custom JPQL query.
    @Query("select t from Todo t where t.completed = :completed order by t.priority asc")
    List<Todo> findByCompletedOrderByPriority(Boolean completed);
}
