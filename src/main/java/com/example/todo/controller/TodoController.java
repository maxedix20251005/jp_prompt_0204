package com.example.todo.controller;

import java.util.Objects;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.todo.entity.Todo;
import com.example.todo.form.TodoForm;
import com.example.todo.service.TodoService;

/**
 * ToDoに関する画面遷移を提供するコントローラ。
 *
 * <p>主なエンドポイントは {@link #list(Model)} を起点とします。</p>
 *
 * <p>HTMLの例: {@code <a th:href="@{/todos}">}</p>
 *
 * @author Codex
 * @version 1.0
 * @since 1.0
 * @see TodoService
 */
@Controller
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    /**
     * 一覧画面を表示します。
     *
     * @param model 画面表示に使用するモデル
     * @return 一覧テンプレート名
     * @throws org.springframework.dao.DataAccessException 取得に失敗した場合
     */
    @GetMapping("/todos")
    public String list(Model model) {
        model.addAttribute("todos", todoService.findAllOrderByCreatedAtDesc());
        return "todo/list";
    }

    /**
     * 新規作成フォームを表示します。
     *
     * @return 入力テンプレート名
     * @throws IllegalStateException 画面生成に失敗した場合
     */
    @GetMapping("/todos/new")
    public String createForm() {
        return "todo/form";
    }

    /**
     * 詳細画面を表示します。
     *
     * @param id ToDoのID
     * @param model 画面表示に使用するモデル
     * @return 詳細テンプレート名
     * @throws org.springframework.dao.DataAccessException 取得に失敗した場合
     */
    @GetMapping("/todos/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        return "todo/detail";
    }

    /**
     * 編集画面を表示します。
     *
     * @param id ToDoのID
     * @param model 画面表示に使用するモデル
     * @return 編集テンプレート名
     * @throws org.springframework.dao.DataAccessException 取得に失敗した場合
     */
    @GetMapping("/todos/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        return "todo/edit";
    }

    /**
     * 入力内容を確認画面へ渡します。
     *
     * @param todoForm 入力フォーム
     * @return 確認テンプレート名
     * @throws IllegalArgumentException todoFormが{@code null}の場合
     */
    @PostMapping("/todos/confirm")
    public String confirm(@ModelAttribute("todoForm") TodoForm todoForm) {
        Objects.requireNonNull(todoForm, "todoForm must not be null");
        return "todo/confirm";
    }

    /**
     * 入力画面へ戻ります。
     *
     * @return 入力画面へのリダイレクト
     * @throws IllegalStateException 画面遷移に失敗した場合
     */
    @PostMapping("/todos/back")
    public String backToForm() {
        return "redirect:/todos/new";
    }

    /**
     * 登録処理を実行し一覧へリダイレクトします。
     *
     * @param todoForm 入力フォーム
     * @param redirectAttributes フラッシュメッセージ用
     * @return 一覧へのリダイレクト
     * @throws IllegalArgumentException todoFormが{@code null}の場合
     * @throws org.springframework.dao.DataAccessException 登録に失敗した場合
     */
    @PostMapping("/todos/complete")
    public String complete(@ModelAttribute("todoForm") TodoForm todoForm,
                           RedirectAttributes redirectAttributes) {
        Objects.requireNonNull(todoForm, "todoForm must not be null");
        todoService.create(todoForm);
        redirectAttributes.addFlashAttribute("message", "登録が完了しました");
        return "redirect:/todos";
    }

    /**
     * 完了画面を表示します。
     *
     * @return 完了テンプレート名
     * @throws IllegalStateException 画面生成に失敗した場合
     */
    @GetMapping("/todos/complete")
    public String showComplete() {
        return "todo/complete";
    }

    /**
     * 指定IDのToDoを削除します。
     *
     * @param id ToDoのID
     * @param redirectAttributes フラッシュメッセージ用
     * @return 一覧へのリダイレクト
     * @throws jakarta.persistence.EntityNotFoundException 対象が存在しない場合
     */
    @PostMapping("/todos/{id}/delete")
    public String delete(@PathVariable("id") Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            todoService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "ToDoを削除しました");
        } catch (jakarta.persistence.EntityNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "削除に失敗しました");
        }
        return "redirect:/todos";
    }

}
