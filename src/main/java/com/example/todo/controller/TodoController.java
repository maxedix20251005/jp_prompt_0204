package com.example.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.todo.form.TodoForm;

@Controller
public class TodoController {

    // Display the todo list page.
    @GetMapping("/todos")
    public String list() {
        return "todo/list";
    }

    // Display the form to create a new todo.
    @GetMapping("/todos/new")
    public String createForm(@ModelAttribute("todoForm") TodoForm todoForm) {
        return "todo/form";
    }

    // Display the detail page for a single todo by id.
    @GetMapping("/todos/{id}")
    public String detail(@PathVariable("id") Long id) {
        return "todo/detail";
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
}
