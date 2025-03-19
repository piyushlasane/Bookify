package com.project.makeagain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapterProfile extends RecyclerView.Adapter<BookAdapterProfile.BookViewHolder> {

    private final List<ModelBook> bookList;

    public BookAdapterProfile(List<ModelBook> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item_profile, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        ModelBook book = bookList.get(position);
        holder.bookTitle.setText(book.getVolumeInfo().getTitle());
        holder.bookImage.setImageResource(R.drawable.no_image);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookTitle, bookProgress;
        ProgressBar progressBar;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookProgress = itemView.findViewById(R.id.bookProgress);
            progressBar = itemView.findViewById(R.id.bookProgressBar);
        }
    }
}