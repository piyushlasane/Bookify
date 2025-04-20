package com.project.makeagain;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.gson.Gson;

import java.util.List;

public class BookAdapterHome extends RecyclerView.Adapter<BookAdapterHome.BookHolder> {

    private final Context context;
    private final List<ModelBook> bookList;
    private final LayoutInflater inflater;

    public BookAdapterHome(Context context, List<ModelBook> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.book_item_home, parent, false);
        return new BookHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {
        ModelBook book = bookList.get(position);
        ModelBook.VolumeInfo volumeInfo = book.getVolumeInfo();

        if (volumeInfo != null) {
            // Set Title
            if (volumeInfo.getTitle() != null) {
                holder.bookTitle.setText(volumeInfo.getTitle());
            } else {
                holder.bookTitle.setText(R.string.no_title_available);
            }

            // Set Author
            if (volumeInfo.getAuthors() != null && !volumeInfo.getAuthors().isEmpty()) {
                holder.bookAuthor.setText(String.join(", ", volumeInfo.getAuthors()));
            } else {
                holder.bookAuthor.setText(R.string.unknown_author);
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
                        .placeholder(R.drawable.book_loading)  // Temporary image while loading
                        .error(R.drawable.no_image)  // If failed, show default image
                        .into(holder.bookImage);

            } else {
                holder.bookImage.setImageResource(R.drawable.no_image);
            }

            // Handle click to open webReaderLink
            holder.itemView.setOnClickListener(v -> {
                if (book.getAccessInfo() != null &&
                        book.getAccessInfo().getWebReaderLink() != null &&
                        !book.getAccessInfo().getWebReaderLink().isEmpty()) {

                    Gson gson = new Gson();
                    String bookJson = gson.toJson(book); // Serialize book

                    RecentlyViewedManager.addBook(context, bookJson); // Save to SharedPreferences

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.getAccessInfo().getWebReaderLink()));
                    context.startActivity(intent);
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookHolder extends RecyclerView.ViewHolder {
        TextView bookTitle, bookAuthor;
        ImageView bookImage;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            bookImage = itemView.findViewById(R.id.bookImage);
        }
    }
}
