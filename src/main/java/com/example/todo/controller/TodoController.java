package com.example.todo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TodoController {

    // Display the todo list page.
    @GetMapping("/todos")
    public String list() {
        return "todo/list";
    }

    // Display the form to create a new todo.
    @GetMapping("/todos/new")
    public String createForm() {
        return "todo/new";
    }

    // Display the detail page for a single todo by id.
    @GetMapping("/todos/{id}")
    public String detail(@PathVariable("id") Long id) {
        return "todo/detail";
    }
}
