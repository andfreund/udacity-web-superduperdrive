package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public int createNote(Note note, User user) {
        note.setNoteId(null);
        note.setUserId(user.getUserId());
        return noteMapper.insertNote(note);
    }

    public List<Note> getNotesFor(User user) {
        int userId = user.getUserId();
        return noteMapper.getNotesFor(userId);
    }
}
