package com.example.todo.service;

import java.util.List;

import com.example.todo.entity.Todo;
import com.example.todo.form.TodoForm;

/**
 * ToDoのユースケースを提供するサービス契約。
 *
 * <p>実装クラスは {@link TodoService} を参照してください。</p>
 *
 * @author Codex
 * @version 1.0
 * @since 1.0
 * @see TodoService
 */
public interface TodoServiceUseCase {

    /**
     * 入力フォームからToDoを作成して永続化します。
     *
     * @param form 入力フォーム
     * @return 作成されたToDo
     * @throws org.springframework.dao.DataAccessException 永続化に失敗した場合
     */
    Todo create(TodoForm form);

    /**
     * 作成日時の新しい順でToDo一覧を取得します。
     *
     * @return ToDo一覧
     * @throws org.springframework.dao.DataAccessException 取得に失敗した場合
     */
    List<Todo> findAllOrderByCreatedAtDesc();

    /**
     * ID指定でToDoを取得します。
     *
     * @param id ToDoのID
     * @return ToDo。存在しない場合は {@code null}
     * @throws org.springframework.dao.DataAccessException 取得に失敗した場合
     */
    Todo findById(Long id);

    /**
     * 指定IDのToDoを更新します。
     *
     * @param id ToDoのID
     * @param form 入力フォーム
     * @return 更新されたToDo
     * @throws jakarta.persistence.EntityNotFoundException 対象が存在しない場合
     * @throws jakarta.persistence.OptimisticLockException 楽観的ロックで競合した場合
     */
    Todo update(Long id, TodoForm form);

    /**
     * 完了状態を反転します。
     *
     * @param id ToDoのID
     * @return 更新後のToDo
     * @throws jakarta.persistence.EntityNotFoundException 対象が存在しない場合
     */
    Todo toggleCompleted(Long id);

    /**
     * 複数IDを一括削除します。
     *
     * @param ids ToDoのIDリスト
     * @return 削除件数
     */
    int deleteByIds(List<Long> ids);
}
