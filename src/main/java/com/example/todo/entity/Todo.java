package com.example.todo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ToDoを表すエンティティ。
 *
 * <p>テーブル名は {@code todos} です。</p>
 *
 * <p>主な項目:</p>
 * <ul>
 *   <li>タイトル</li>
 *   <li>期限日</li>
 *   <li>完了状態</li>
 * </ul>
 *
 * @author Codex
 * @version 1.0
 * @since 1.0
 * @see com.example.todo.form.TodoForm
 */
@Entity
@Table(name = "todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    /** 主キー。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** タイトル（必須、最大100文字）。 */
    @Column(nullable = false, length = 100)
    private String title;

    /** 説明（任意、最大500文字）。 */
    @Column(length = 500)
    private String description;

    /** 期限日。 */
    private LocalDate dueDate;

    /** 優先度（デフォルト: 1）。 */
    @Column(nullable = false)
    private Integer priority = 1;

    /** 完了フラグ（デフォルト: false）。 */
    @Column(nullable = false)
    private Boolean completed = false;

    /** 作成日時。 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 更新日時。 */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 作成時に日時とデフォルト値を設定します。
     *
     * @throws IllegalStateException 日時の設定に失敗した場合
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (priority == null) {
            priority = 1;
        }
        if (completed == null) {
            completed = false;
        }
    }

    /**
     * 更新時に更新日時とデフォルト値を設定します。
     *
     * @throws IllegalStateException 日時の設定に失敗した場合
     */
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        if (priority == null) {
            priority = 1;
        }
        if (completed == null) {
            completed = false;
        }
    }
}
