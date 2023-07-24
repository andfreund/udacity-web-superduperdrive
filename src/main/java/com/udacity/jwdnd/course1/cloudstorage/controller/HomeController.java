package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Note;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.NoteService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String viewPage(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);
        model.addAttribute("notes", noteService.getNotesFor(user));
        return "home";
    }

    @PostMapping("/notes")
    public String addNote(Note note, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);

        if (note.getNoteId() > 0) {
            // note already exists in DB -> edit
            noteService.updateNote(note);
        } else {
            // note doesn't exist in DB -> create
            note.setUserId(user.getUserId());
            int noteId = noteService.createNote(note, user);

            if (noteId < 0) {
                model.addAttribute("alertMessage", "Error during note creation!");
                model.addAttribute("alertError", true);
            } else {
                model.addAttribute("alertMessage", "Note successfully created!");
                model.addAttribute("alertSuccess", true);
            }
        }

        model.addAttribute("notes", noteService.getNotesFor(user));
        return "home";
    }

    @GetMapping("/notes/delete/{noteid}")
    public String deleteNote(@PathVariable("noteid") String noteId, Model model, Authentication authentication) {
        // TODO DRY: display all notes
        String username = authentication.getName();
        User user = userService.getUser(username);

        if (noteService.deleteNote(Integer.parseInt(noteId)) < 0) {
            model.addAttribute("alertMessage", "Note deletion failed!");
            model.addAttribute("alertError", true);
        } else {
            model.addAttribute("alertMessage", "Note successfully deleted!");
            model.addAttribute("alertSuccess", true);
        }

        model.addAttribute("notes",  noteService.getNotesFor(user));
        return "home";
    }
}
