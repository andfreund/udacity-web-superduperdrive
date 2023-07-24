package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.CredentialService;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
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
    private CredentialService credentialService;
    private EncryptionService encryptionService;

    public HomeController(NoteService noteService, UserService userService, CredentialService credentialService, EncryptionService encryptionService) {
        this.noteService = noteService;
        this.userService = userService;
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
    }

    @GetMapping
    public String viewPage(Model model, Authentication authentication) {
        // TODO add everything to model
        addUserNotesToModel(model, authentication);
        return "home";
    }

    @PostMapping("/notes")
    public String addAndUpdateNotes(Note note, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);

        if (note.getNoteId() > 0) {
            // note already exists in DB -> edit
            noteService.updateNote(note, user);
        } else {
            // note doesn't exist in DB -> create
            int noteId = noteService.createNote(note, user);

            if (noteId < 0) {
                model.addAttribute("alertMessage", "Note creation failed!");
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
        if (noteService.deleteNote(Integer.parseInt(noteId)) < 0) {
            model.addAttribute("alertMessage", "Note deletion failed!");
            model.addAttribute("alertError", true);
        } else {
            model.addAttribute("alertMessage", "Note successfully deleted!");
            model.addAttribute("alertSuccess", true);
        }

        addUserNotesToModel(model, authentication);
        return "home";
    }

    @PostMapping("/credentials")
    public String addAndUpdateCredentials(Credential credential, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);

        if (credential.getCredentialId() > 0) {
            // credential already exists in DB -> edit/view
            credentialService.updateCredential(credential, user);
        } else {
            // credential doesn't exist in Db -> create
            int credentialId = credentialService.createCredential(credential, user);

            if (credentialId < 0) {
                model.addAttribute("alertMessage", "Credential creation failed!");
                model.addAttribute("alertError", true);
            } else {
                model.addAttribute("alertMessage", "Credential successfully created!");
                model.addAttribute("alertSuccess", true);
            }
        }

        model.addAttribute("credentials", credentialService.getCredentialsFor(user));
        model.addAttribute("encryptionService", encryptionService);
        return "home";
    }

    @GetMapping("/credentials/delete/{credentialid}")
    public String deleteCredential(@PathVariable("credentialid") String credentialId, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);

        if (credentialService.deleteNote(Integer.parseInt(credentialId)) < 0) {
            model.addAttribute("alertMessage", "Credential deletion failed!");
            model.addAttribute("alertError", true);
        } else {
            model.addAttribute("alertMessage", "Credential successfully deleted!");
            model.addAttribute("alertSuccess", true);
        }

        model.addAttribute("credentials", credentialService.getCredentialsFor(user));
        model.addAttribute("encryptionService", encryptionService);
        return "home";
    }

    private void addUserNotesToModel(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);
        model.addAttribute("notes",  noteService.getNotesFor(user));
    }
}
