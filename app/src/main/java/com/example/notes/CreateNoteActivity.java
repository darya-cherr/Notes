package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputTitle, inputNote;
    private TextView dateTime;
    private AppCompatButton imageSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        RelativeLayout imageBack = findViewById(R.id.image_back);

        inputNote = findViewById(R.id.inputNote);
        inputTitle = findViewById(R.id.inputNoteTitle);
        dateTime = findViewById(R.id.text_date);
        imageSave = findViewById(R.id.img_save);

        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        dateTime.setText(new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(new Date()));


        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void saveNote(){
        if(inputNote.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note text is empty!", Toast.LENGTH_SHORT).show();
            return;
        }else if(inputTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note title is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note = new Note();
        note.setTitle(inputTitle.getText().toString());
        note.setNoteText(inputNote.getText().toString());
        note.setDateTime(dateTime.getText().toString());

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void , Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        new SaveNoteTask().execute();

    }
}