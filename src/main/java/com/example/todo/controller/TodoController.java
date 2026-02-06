package com.example.todo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.todo.entity.Todo;
import com.example.todo.form.TodoForm;
import com.example.todo.service.TodoService;

@Controller
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping({"/todos", "/todos/"})
    public String list(@PageableDefault(size = 10) Pageable pageable,
                       @RequestParam(required = false) Map<String, String> params,
                       @RequestParam(required = false) String sort,
                       @RequestParam(required = false) String dir,
                       Model model) {
        Page<Todo> page = todoService.findPageByMyBatis(pageable, sort, dir);
        applyPagingModel(model, page, pageable);
        model.addAttribute("todos", page.getContent());
        model.addAttribute("queryParams", sanitizeQueryParams(params));
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("overdueMode", false);
        return "todo/list";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/todos";
    }

    @GetMapping({"/todo/export", "/todos/export"})
    public ResponseEntity<byte[]> exportCsv() {
        List<Todo> todos = todoService.findAllOrderByCreatedAtDesc();
        String csv = buildCsv(todos);
        byte[] bom = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] body = csv.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] out = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, out, 0, bom.length);
        System.arraycopy(body, 0, out, bom.length, body.length);

        String filename = "todo_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        return new ResponseEntity<>(out, headers, HttpStatus.OK);
    }

    @GetMapping("/todos/overdue")
    public String overdue(@PageableDefault(size = 10) Pageable pageable,
                          @RequestParam(required = false) Map<String, String> params,
                          @RequestParam(required = false) String sort,
                          @RequestParam(required = false) String dir,
                          Model model) {
        Page<Todo> page = todoService.findOverduePageByMyBatis(LocalDate.now(), pageable, sort, dir);
        applyPagingModel(model, page, pageable);
        model.addAttribute("todos", page.getContent());
        model.addAttribute("overdueMode", true);
        model.addAttribute("queryParams", sanitizeQueryParams(params));
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        return "todo/list";
    }

    @GetMapping("/todos/new")
    public String createForm() {
        return "todo/form";
    }

    @GetMapping("/todos/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        return "todo/detail";
    }

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
            bindingResult.reject("optimisticLock", "他のユーザーにより更新されました。再読み込みしてください。");
            model.addAttribute("todoId", id);
            return "todo/edit";
        }
        redirectAttributes.addFlashAttribute("successMessage", "更新が完了しました");
        return "redirect:/todos";
    }

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

    @PostMapping("/todos/back")
    public String backToForm() {
        return "redirect:/todos/new";
    }

    @PostMapping("/todos/complete")
    public String complete(@ModelAttribute("todoForm") TodoForm todoForm,
                           RedirectAttributes redirectAttributes) {
        Objects.requireNonNull(todoForm, "todoForm must not be null");
        todoService.create(todoForm);
        redirectAttributes.addFlashAttribute("message", "登録が完了しました");
        return "redirect:/todos";
    }

    @GetMapping("/todos/complete")
    public String showComplete() {
        return "todo/complete";
    }

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

    @PostMapping("/todos/bulk-delete")
    public String bulkDelete(@RequestParam(name = "ids", required = false) List<Long> ids,
                             RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "削除する項目を選択してください");
            return "redirect:/todos";
        }
        int deleted = todoService.deleteByIds(ids);
        redirectAttributes.addFlashAttribute("successMessage", "選択した項目を削除しました（" + deleted + "件）");
        return "redirect:/todos";
    }

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

    private void applyPagingModel(Model model, Page<Todo> page, Pageable pageable) {
        model.addAttribute("page", page);
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("totalPages", page.getTotalPages());
        long total = page.getTotalElements();
        long start = total == 0 ? 0 : pageable.getOffset() + 1;
        long end = Math.min(pageable.getOffset() + page.getNumberOfElements(), total);
        model.addAttribute("totalElements", total);
        model.addAttribute("rangeStart", start);
        model.addAttribute("rangeEnd", end);
        model.addAttribute("pageSize", pageable.getPageSize());
    }

    private Map<String, String> sanitizeQueryParams(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        Map<String, String> cleaned = new java.util.LinkedHashMap<>(params);
        cleaned.remove("page");
        cleaned.remove("size");
        cleaned.remove("sort");
        cleaned.remove("dir");
        return cleaned;
    }

    private String buildCsv(List<Todo> todos) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,タイトル,登録者,ステータス,作成日\n");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        for (Todo todo : todos) {
            String status = Boolean.TRUE.equals(todo.getCompleted()) ? "完了" : "未完了";
            sb.append(escapeCsv(String.valueOf(todo.getId()))).append(",");
            sb.append(escapeCsv(nullToEmpty(todo.getTitle()))).append(",");
            sb.append(escapeCsv(nullToEmpty(todo.getAuthor()))).append(",");
            sb.append(escapeCsv(status)).append(",");
            sb.append(escapeCsv(todo.getCreatedAt() == null ? "" : todo.getCreatedAt().format(fmt)));
            sb.append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuote = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        String escaped = value.replace("\"", "\"\"");
        return needsQuote ? "\"" + escaped + "\"" : escaped;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    public record ToggleResponse(Long id, Boolean completed) { }
}
