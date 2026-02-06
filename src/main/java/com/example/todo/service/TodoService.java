package com.example.todo.service;

import java.util.List;
import java.util.Collections;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.todo.entity.Todo;
import com.example.todo.form.TodoForm;
import com.example.todo.mapper.TodoMapper;
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
    private final TodoMapper todoMapper;

    public TodoService(TodoRepository todoRepository, TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
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

    /**
     * MyBatisでページング取得します。
     *
     * @param pageable ページ情報
     * @return ページング結果
     */
    public Page<Todo> findPageByMyBatis(Pageable pageable, String sort, String dir) {
        if (pageable == null) {
            return Page.empty();
        }
        String sortColumn = mapSortColumn(sort);
        String sortDir = mapSortDir(dir);
        long total = todoMapper.countAll();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();
        List<Todo> items = todoMapper.selectPage(limit, offset, sortColumn, sortDir);
        return new PageImpl<>(items, pageable, total);
    }

    /**
     * MyBatisで期限切れToDoをページング取得します。
     *
     * @param date 期限日（この日付以前が対象）
     * @param pageable ページ情報
     * @return ページング結果
     */
    public Page<Todo> findOverduePageByMyBatis(java.time.LocalDate date, Pageable pageable, String sort, String dir) {
        if (pageable == null) {
            return Page.empty();
        }
        String sortColumn = mapSortColumn(sort);
        String sortDir = mapSortDir(dir);
        long total = todoMapper.countOverdue(date);
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();
        List<Todo> items = todoMapper.selectOverduePage(date, limit, offset, sortColumn, sortDir);
        return new PageImpl<>(items, pageable, total);
    }

    private String mapSortColumn(String sort) {
        if ("title".equals(sort)) {
            return "title";
        }
        if ("dueDate".equals(sort)) {
            return "due_date";
        }
        if ("priority".equals(sort)) {
            return "priority";
        }
        if ("author".equals(sort)) {
            return "author";
        }
        if ("completed".equals(sort)) {
            return "completed";
        }
        return "created_at";
    }

    private String mapSortDir(String dir) {
        if ("asc".equalsIgnoreCase(dir)) {
            return "ASC";
        }
        return "DESC";
    }

    /**
     * MyBatisで期限切れToDoを取得します。
     *
     * @param date 期限日（この日付以前が対象）
     * @return 期限切れToDo一覧
     */
    public List<Todo> findOverdueByMyBatis(java.time.LocalDate date) {
        return todoMapper.selectOverdue(date);
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

    @Override
    public int deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return todoMapper.deleteByIds(ids);
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
