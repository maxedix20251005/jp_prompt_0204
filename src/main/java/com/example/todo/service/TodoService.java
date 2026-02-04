package com.example.todo.service;

import org.springframework.stereotype.Service;

import com.example.todo.entity.Todo;
import com.example.todo.form.TodoForm;
import com.example.todo.repository.TodoRepository;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Todo create(TodoForm form) {
        Todo todo = toEntity(form);
        return todoRepository.save(todo);
    }

    private Todo toEntity(TodoForm form) {
        return Todo.builder()
                .title(form.getTitle())
                .description(form.getDescription())
                .dueDate(form.getDueDate())
                .priority(form.getPriority())
                .build();
    }
}
