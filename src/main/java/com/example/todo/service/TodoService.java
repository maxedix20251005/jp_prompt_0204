package com.example.todo.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.todo.entity.Todo;
import com.example.todo.form.TodoForm;
import com.example.todo.repository.TodoRepository;

/**
 * ToDoのビジネスロジックを提供するサービス。
 *
 * <p>フォームからエンティティへ変換する際は {@code Todo.builder()} を使用します。</p>
 *
 * @author Codex
 * @version 1.0
 * @since 1.0
 * @see TodoServiceUseCase
 */
@Service
public class TodoService implements TodoServiceUseCase {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    /** {@inheritDoc} */
    @Override
    public Todo create(TodoForm form) {
        Todo todo = toEntity(form);
        return todoRepository.save(todo);
    }

    /** {@inheritDoc} */
    @Override
    public List<Todo> findAllOrderByCreatedAtDesc() {
        return todoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /** {@inheritDoc} */
    @Override
    public Todo findById(Long id) {
        return todoRepository.findById(id).orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public Todo toggleCompleted(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Todo not found: " + id));
        todo.setCompleted(!Boolean.TRUE.equals(todo.getCompleted()));
        return todoRepository.save(todo);
    }

    /** {@inheritDoc} */
    @Override
    public Todo update(Long id, TodoForm form) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (form == null) {
            throw new IllegalArgumentException("form must not be null");
        }
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Todo not found: " + id));
        if (form.getVersion() != null && !form.getVersion().equals(todo.getVersion())) {
            throw new jakarta.persistence.OptimisticLockException("Todo was updated by another transaction");
        }
        todo.setTitle(form.getTitle());
        todo.setDescription(form.getDescription());
        todo.setAuthor(form.getAuthor());
        todo.setDueDate(form.getDueDate());
        todo.setPriority(form.getPriority());
        return todoRepository.save(todo);
    }

    /**
     * ID指定でフォーム用データを取得します。
     *
     * @param id ToDoのID
     * @return フォームオブジェクト
     * @throws jakarta.persistence.EntityNotFoundException 対象が存在しない場合
     */
    public TodoForm findFormById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Todo not found: " + id));
        return toForm(todo);
    }

    /**
     * 指定IDのToDoを削除します。
     *
     * @param id ToDoのID
     * @throws IllegalArgumentException idが{@code null}の場合
     * @throws jakarta.persistence.EntityNotFoundException 対象が存在しない場合
     */
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (!todoRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Todo not found: " + id);
        }
        todoRepository.deleteById(id);
    }

    /**
     * フォームからエンティティへ変換します。
     *
     * @param form 入力フォーム
     * @return 変換されたエンティティ
     * @throws IllegalArgumentException formが{@code null}の場合
     */
    private Todo toEntity(TodoForm form) {
        if (form == null) {
            throw new IllegalArgumentException("form must not be null");
        }
        return Todo.builder()
                .author(form.getAuthor())
                .title(form.getTitle())
                .description(form.getDescription())
                .dueDate(form.getDueDate())
                .priority(form.getPriority())
                .build();
    }

    /**
     * エンティティからフォームへ変換します。
     *
     * @param todo エンティティ
     * @return 変換されたフォーム
     * @throws IllegalArgumentException todoが{@code null}の場合
     */
    private TodoForm toForm(Todo todo) {
        if (todo == null) {
            throw new IllegalArgumentException("todo must not be null");
        }
        return new TodoForm(
                todo.getAuthor(),
                todo.getTitle(),
                todo.getDescription(),
                todo.getDueDate(),
                todo.getPriority(),
                todo.getVersion()
        );
    }
}
