package com.example.notesappatiq

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).noteDao()

    // READ: Flow dari Room diubah menjadi StateFlow untuk UI
    val notes: StateFlow<List<Note>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // CREATE
    fun addNote(title: String, note: String) {
        val trimmedTitle = title.trim()
        val trimmedNote = note.trim()
        if (trimmedTitle.isEmpty() && trimmedNote.isEmpty()) return
        viewModelScope.launch {
            dao.insert(Note(title = trimmedTitle, note = trimmedNote))
        }
    }

    // UPDATE
    fun updateNote(note: Note, newTitle: String, newNote: String) {
        val trimmedTitle = newTitle.trim()
        val trimmedNote = newNote.trim()
        if (trimmedTitle.isEmpty() && trimmedNote.isEmpty()) return
        viewModelScope.launch {
            dao.update(note.copy(title = trimmedTitle, note = trimmedNote))
        }
    }

    // DELETE
    fun deleteNote(note: Note) {
        viewModelScope.launch { dao.delete(note) }
    }
}