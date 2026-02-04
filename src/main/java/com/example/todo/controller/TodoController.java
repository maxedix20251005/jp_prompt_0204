package com.example.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.todo.form.TodoForm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TodoController {

    private static final List<TodoView> TODO_ITEMS = new ArrayList<>();

    static {
        for (long i = 1; i <= 15; i++) {
            TODO_ITEMS.add(new TodoView(
                    i,
                    "サンプルToDo " + i,
                    "詳細説明 " + i,
                    LocalDate.now().plusDays(i),
                    (int) ((i - 1) % 5) + 1,
                    i % 2 == 0 ? "完了" : "未完了"
            ));
        }
    }

    // Display the todo list page.
    @GetMapping("/todos")
    public String list(Model model) {
        model.addAttribute("todos", TODO_ITEMS);
        return "todo/list";
    }

    // Display the form to create a new todo.
    @GetMapping("/todos/new")
    public String createForm(@ModelAttribute("todoForm") TodoForm todoForm) {
        return "todo/form";
    }

    // Display the detail page for a single todo by id.
    @GetMapping("/todos/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Optional<TodoView> todo = TODO_ITEMS.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
        if (todo.isEmpty()) {
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo.get());
        return "todo/detail";
    }

    // Display the edit page for a single todo by id.
    @GetMapping("/todos/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        Optional<TodoView> todo = TODO_ITEMS.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
        if (todo.isEmpty()) {
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo.get());
        return "todo/edit";
    }

    // Receive form submission via POST and show a confirmation page.
    @PostMapping("/todos/confirm")
    public String confirm(@ModelAttribute("todoForm") TodoForm todoForm,
                          RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("todoForm", todoForm);
        return "redirect:/todos/confirm";
    }

    // Show the confirmation page (data comes via RedirectAttributes).
    @GetMapping("/todos/confirm")
    public String showConfirm(@ModelAttribute("todoForm") TodoForm todoForm) {
        return "todo/confirm";
    }

    // Go back to the input screen while keeping the entered values.
    @PostMapping("/todos/back")
    public String backToForm(@ModelAttribute("todoForm") TodoForm todoForm,
                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("todoForm", todoForm);
        return "redirect:/todos/new";
    }

    // Complete registration and show the completion page.
    @PostMapping("/todos/complete")
    public String complete(@ModelAttribute("todoForm") TodoForm todoForm,
                           RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("todoForm", todoForm);
        return "redirect:/todos/complete";
    }

    @GetMapping("/todos/complete")
    public String showComplete() {
        return "todo/complete";
    }

    public static class TodoView {
        private final Long id;
        private final String title;
        private final String description;
        private final LocalDate dueDate;
        private final Integer priority;
        private final String status;

        public TodoView(Long id, String title, String description, LocalDate dueDate, Integer priority, String status) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
            this.priority = priority;
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public Integer getPriority() {
            return priority;
        }

        public String getStatus() {
            return status;
        }
    }
}
