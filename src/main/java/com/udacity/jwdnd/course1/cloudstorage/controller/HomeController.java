package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class HomeController {
    private NoteService noteService;
    private UserService userService;

    public HomeController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    @GetMapping
    public String viewPage() {
        return "home";
    }

    @PostMapping("/note")
    public String addNote(Note note, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);

        note.setUserId(user.getUserId());
        int noteId = noteService.createNote(note, user);
        if (noteId < 0) {
            model.addAttribute("noteCreationError", true);
            model.addAttribute("noteCreationSuccess", false);
        } else {
            model.addAttribute("noteCreationError", false);
            model.addAttribute("noteCreationSuccess", true);
        }

        return "home";
    }
}
