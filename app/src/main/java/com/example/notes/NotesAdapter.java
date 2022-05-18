package com.example.notes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.io.InputStream;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{

    private List<Note> notes;


    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView title, noteText, date;
        ImageView img;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            noteText = itemView.findViewById(R.id.textNote);
            date = itemView.findViewById(R.id.textDateTime);
            img = itemView.findViewById(R.id.item_img);
        }

        void setNote(Note note){
            title.setText(note.getTitle());
            noteText.setText(note.getNoteText());
            date.setText(note.getDateTime());
            if(note.getImagePath()!=null){
                try {

                    Bitmap bitmap = BitmapFactory.decodeFile(note.getImagePath());

                            img.setImageBitmap(bitmap);
                }catch(Exception e){
                    Log.d("MyLog", e.getMessage());
                }
                img.setVisibility(View.VISIBLE);
            }else{
                img.setVisibility(View.GONE);
            }
        }
    }
}
