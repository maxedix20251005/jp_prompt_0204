package com.example.todo.controller;

import java.util.Objects;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;
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
     */
    @GetMapping({"/todos", "/todos/"})
    public String list(Model model) {
        model.addAttribute("todos", todoService.findAllOrderByCreatedAtDesc());
        return "todo/list";
    }

    /**
     * 期限切れのToDo一覧（MyBatis）を表示します。
     *
     * @param model 画面表示に使用するモデル
     * @return 一覧テンプレート名
     */
    @GetMapping("/todos/overdue")
    public String overdue(Model model) {
        model.addAttribute("todos", todoService.findOverdueByMyBatis(LocalDate.now()));
        model.addAttribute("overdueMode", true);
        return "todo/list";
    }

    /**
     * 新規作成フォームを表示します。
     *
     * @return 入力テンプレート名
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
     */
    @GetMapping("/todos/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        try {
            TodoForm form = todoService.findFormById(id);
            model.addAttribute("todoForm", form);
            model.addAttribute("todoId", id);
            return "todo/edit";
        } catch (jakarta.persistence.EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found", ex);
        }
    }

    /**
     * 更新処理を実行します。
     *
     * @param id ToDoのID
     * @param todoForm 入力フォーム
     * @param bindingResult バリデーション結果
     * @param model 画面表示に使用するモデル
     * @param redirectAttributes フラッシュメッセージ用
     * @return 一覧へのリダイレクト、または編集画面
     */
    @PostMapping("/todos/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Valid @ModelAttribute("todoForm") TodoForm todoForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("todoId", id);
            return "todo/edit";
        }
        try {
            todoService.update(id, todoForm);
        } catch (jakarta.persistence.EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found", ex);
        } catch (jakarta.persistence.OptimisticLockException ex) {
            bindingResult.reject("optimisticLock", "他のユーザーにより更新されました。再度お試しください。");
            model.addAttribute("todoId", id);
            return "todo/edit";
        }
        redirectAttributes.addFlashAttribute("successMessage", "更新が完了しました");
        return "redirect:/todos";
    }

    /**
     * 入力内容を確認画面へ渡します。
     *
     * @param todoForm 入力フォーム
     * @param bindingResult バリデーション結果
     * @param model 画面表示に使用するモデル
     * @return 確認テンプレート名
     */
    @PostMapping("/todos/confirm")
    public String confirm(@Valid @ModelAttribute("todoForm") TodoForm todoForm,
                          BindingResult bindingResult,
                          Model model) {
        Objects.requireNonNull(todoForm, "todoForm must not be null");
        if (bindingResult.hasErrors()) {
            return "todo/form";
        }
        return "todo/confirm";
    }

    /**
     * 入力画面へ戻ります。
     *
     * @return 入力画面へのリダイレクト
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

    /**
     * 完了状態を反転します（同期/非同期両対応）。
     *
     * @param id ToDoのID
     * @param requestedWith Ajax判定用ヘッダ
     * @param redirectAttributes フラッシュメッセージ用
     * @return Ajaxの場合はJSON、通常は一覧リダイレクト
     */
    @PostMapping("/todos/{id}/toggle")
    public Object toggle(@PathVariable("id") Long id,
                         @RequestHeader(value = "X-Requested-With", required = false) String requestedWith,
                         RedirectAttributes redirectAttributes) {
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(requestedWith);
        try {
            Todo updated = todoService.toggleCompleted(id);
            if (isAjax) {
                return ResponseEntity.ok().body(new ToggleResponse(updated.getId(), updated.getCompleted()));
            }
            redirectAttributes.addFlashAttribute("successMessage", "完了状態を更新しました");
            return "redirect:/todos";
        } catch (jakarta.persistence.EntityNotFoundException ex) {
            if (isAjax) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ToggleResponse(id, null));
            }
            redirectAttributes.addFlashAttribute("errorMessage", "更新に失敗しました");
            return "redirect:/todos";
        }
    }

    /** Ajax用レスポンス。 */
    public record ToggleResponse(Long id, Boolean completed) { }

}
