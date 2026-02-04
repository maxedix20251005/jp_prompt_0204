package com.example.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.todo.entity.Todo;
import com.example.todo.form.TodoForm;
import com.example.todo.repository.TodoRepository;
import com.example.todo.service.TodoService;

@Controller
public class TodoController {

    private final TodoRepository todoRepository;
    private final TodoService todoService;

    public TodoController(TodoRepository todoRepository, TodoService todoService) {
        this.todoRepository = todoRepository;
        this.todoService = todoService;
    }

    // Display the todo list page.
    @GetMapping("/todos")
    public String list(Model model) {
        model.addAttribute("todos", todoRepository.findAll());
        return "todo/list";
    }

    // Display the form to create a new todo.
    @GetMapping("/todos/new")
    public String createForm() {
        return "todo/form";
    }

    // Display the detail page for a single todo by id.
    @GetMapping("/todos/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo == null) {
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        return "todo/detail";
    }

    // Display the edit page for a single todo by id.
    @GetMapping("/todos/{id}/edit")
    public String edit(@PathVariable("id") Long id, Model model) {
        Todo todo = todoRepository.findById(id).orElse(null);
        if (todo == null) {
            return "redirect:/todos";
        }
        model.addAttribute("todo", todo);
        return "todo/edit";
    }

    // Receive form submission via POST and show a confirmation page.
    @PostMapping("/todos/confirm")
    public String confirm(@ModelAttribute("todoForm") TodoForm todoForm) {
        return "todo/confirm";
    }

    // Go back to the input screen while keeping the entered values.
    @PostMapping("/todos/back")
    public String backToForm() {
        return "redirect:/todos/new";
    }

    // Complete registration and show the completion page.
    @PostMapping("/todos/complete")
    public String complete(@ModelAttribute("todoForm") TodoForm todoForm,
                           RedirectAttributes redirectAttributes) {
        todoService.create(todoForm);
        redirectAttributes.addFlashAttribute("message", "登録が完了しました");
        return "redirect:/todos";
    }

    @GetMapping("/todos/complete")
    public String showComplete() {
        return "todo/complete";
    }

}
