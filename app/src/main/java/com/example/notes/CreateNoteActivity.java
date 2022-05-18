package com.example.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputTitle, inputNote;
    private TextView dateTime;
    private AppCompatButton imageSave;
    private ImageView addImgButton, imageNote;
    String selectedImgPath;

    private static final int CODE_STORAGE = 1;
    private static final int CODE_SELECT = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        RelativeLayout imageBack = findViewById(R.id.image_back);

        inputNote = findViewById(R.id.inputNote);
        inputTitle = findViewById(R.id.inputNoteTitle);
        dateTime = findViewById(R.id.text_date);
        imageSave = findViewById(R.id.img_save);
        addImgButton = findViewById(R.id.AddImageButton);
        imageNote = findViewById(R.id.ImageNote);
        selectedImgPath = null;

        addImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreateNoteActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                            CODE_STORAGE);
                }else {
                    selectImage();
                }
            }
        });

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

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent, CODE_SELECT);
        }
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
        note.setImagePath(selectedImgPath);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CODE_STORAGE && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else{
                Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODE_SELECT && resultCode==RESULT_OK){
            if(data !=null){
                Uri selectedImg = data.getData();
                if(selectedImg != null){
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(selectedImg);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        selectedImgPath =  getPathFromUri(data.getData());
                        Log.d("MyLog", selectedImgPath + "        " + selectedImg);
                    }catch(Exception e){

                    }
                }
            }

        }
    }

    String getPathFromUri(Uri uri){
        String filePath;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if(cursor != null){
            filePath = uri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
}