package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.*;
import com.udacity.jwdnd.course1.cloudstorage.services.*;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequestMapping("/home")
public class HomeController {
    private NoteService noteService;
    private UserService userService;
    private CredentialService credentialService;
    private EncryptionService encryptionService;
    private FileService fileService;

    public HomeController(NoteService noteService, UserService userService, CredentialService credentialService, EncryptionService encryptionService, FileService fileService) {
        this.noteService = noteService;
        this.userService = userService;
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
        this.fileService = fileService;
    }

    @GetMapping
    public String viewPage(Model model) {
        setupModelSuccess(model, null);
        return "home";
    }

    @PostMapping("/notes")
    public String addAndUpdateNotes(Note note, Model model) {
        User user = getCurrentUser();

        if (note.getNoteId() > 0) {
            int updatedRecords = noteService.updateNote(note, user);

            if (updatedRecords <= 0) {
                setupModelError(model, "Note update failed!");
            } else {
                setupModelSuccess(model, "Note successfully updated!");
            }
        } else {
            int noteId = noteService.createNote(note, user);

            if (noteId < 0) {
                setupModelError(model, "Note creation failed!");
            } else {
                setupModelSuccess(model, "Note successfully created!");
            }
        }

        return "home";
    }

    @GetMapping("/notes/delete/{noteid}")
    public String deleteNote(@PathVariable("noteid") String noteId, Model model) {
        if (noteService.deleteNote(Integer.parseInt(noteId)) < 0) {
            setupModelError(model, "Note deletion failed!");
        } else {
            setupModelSuccess(model, "Note successfully deleted!");
        }
        return "home";
    }

    @PostMapping("/credentials")
    public String addAndUpdateCredentials(Credential credential, Model model) {
        User user = getCurrentUser();

        if (credential.getCredentialId() > 0) {
            int updatedRecords = credentialService.updateCredential(credential, user);

            if (updatedRecords <= 0) {
                setupModelError(model, "Credential updated failed!");
            } else {
                setupModelSuccess(model, "Credential successfully updated!");
            }
        } else {
            int credentialId = credentialService.createCredential(credential, user);

            if (credentialId < 0) {
                setupModelError(model, "Credential creation failed!");
            } else {
                setupModelSuccess(model, "Credential successfully created!");
            }
        }

        return "home";
    }

    @GetMapping("/credentials/delete/{credentialid}")
    public String deleteCredential(@PathVariable("credentialid") String credentialId, Model model) {
        if (credentialService.deleteNote(Integer.parseInt(credentialId)) < 0) {
            setupModelError(model, "Credential deletion failed!");
        } else {
            setupModelSuccess(model, "Credential successfully deleted!");
        }
        return "home";
    }

    @PostMapping("/files")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload, Model model) {
        User user = getCurrentUser();
        byte[] fileData;
        String fileContentType = "application/octet-stream";
        String fileName;

        fileName = fileUpload.getOriginalFilename();
        if (fileService.fileExists(fileName)) {
            setupModelError(model, "File already exists!");
            return "home";
        }

        try {
            InputStream fis = fileUpload.getInputStream();
            fileData = fis.readAllBytes();

            if (fileData.length == 0) {
                setupModelError(model, "File is empty!");
                return "home";
            }

            if (fileName != null) {
                fileContentType = Files.probeContentType(Paths.get(fileName));
            }
        } catch (IOException | RuntimeException e) {
            setupModelError(model, "File upload failed!");
            return "home";
        }

        int fileId = fileService.createFile(fileName, fileContentType, fileData, user);
        if (fileId < 0) {
            setupModelError(model, "File upload failed!");
            return "home";
        }

        setupModelSuccess(model, "File successfully uploaded!");
        return "home";
    }

    @GetMapping("/files/view/{fileid}")
    public ResponseEntity<ByteArrayResource> viewFile(@PathVariable("fileid") String fileId, Model model) {
        setupModelSuccess(model, null);
        File file = fileService.getFile(Integer.parseInt(fileId));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(new ByteArrayResource(file.getFileData()));
    }

    @GetMapping("/files/delete/{fileid}")
    public String deleteFile(@PathVariable("fileid") String fileId, Model model) {
        if (fileService.deleteFile(Integer.parseInt(fileId)) < 0) {
            setupModelError(model, "File deletion failed!");
        } else {
            setupModelSuccess(model, "File successfully deleted!");
        }
        return "home";
    }

    private User getCurrentUser() {
        return userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private void setupModelSuccess(Model model, String message) {
        setupModel(model, message, false);
    }

    private void setupModelError(Model model, String message) {
        setupModel(model, message, true);
    }

    private void setupModel(Model model, String message, boolean error) {
        if (message != null) {
            if (error) {
                model.addAttribute("alertError", true);
            } else {
                model.addAttribute("alertSuccess", true);
            }
            model.addAttribute("alertMessage", message);
        }

        User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("files", fileService.getFilesFor(user));
        model.addAttribute("notes",  noteService.getNotesFor(user));
        model.addAttribute("credentials", credentialService.getCredentialsFor(user));
        model.addAttribute("encryptionService", encryptionService);
    }
}
