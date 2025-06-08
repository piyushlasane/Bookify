package com.project.makeagain;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class BookAdapterProfile extends RecyclerView.Adapter<BookAdapterProfile.BookViewHolder> {

    private final Context context;
    private final List<ModelBook> bookList;

    public BookAdapterProfile(Context context, List<ModelBook> bookList) {
        this.context = context;
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
        ModelBook.VolumeInfo volumeInfo = book.getVolumeInfo();

        if (volumeInfo != null) {
            // Set Title
            if (volumeInfo.getTitle() != null) {
                holder.bookTitle.setText(volumeInfo.getTitle());
            } else {
                holder.bookTitle.setText(R.string.no_title_available);
            }

            // Load Image using Glide
            if (volumeInfo.getImageLinks() != null && volumeInfo.getImageLinks().getThumbnail() != null) {
                String imageUrl = volumeInfo.getImageLinks().getThumbnail();
                if (imageUrl != null && imageUrl.startsWith("http://")) {
                    imageUrl = imageUrl.replace("http://", "https://");
                }

                Log.d("GlideDebug", "Loading image URL: " + imageUrl);
                Glide.with(context)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Ensures caching
                        .placeholder(R.color.white)  // Temporary image while loading
                        .error(R.drawable.no_image)  // If failed, show default image
                        .into(holder.bookImage);

            } else {
                holder.bookImage.setImageResource(R.drawable.no_image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookTitle;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            bookTitle = itemView.findViewById(R.id.bookTitle);
        }
    }
}