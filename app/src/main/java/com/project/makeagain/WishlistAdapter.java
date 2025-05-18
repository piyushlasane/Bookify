package com.project.makeagain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.BookViewHolder> {

    private final List<ModelBook> bookList;

    public WishlistAdapter(List<ModelBook> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item_wishlist, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        ModelBook book = bookList.get(position);
        ModelBook.VolumeInfo info = book.getVolumeInfo();

        if (info == null) {
            holder.title.setText(R.string.no_title);
            holder.author.setText(R.string.unknown_author);
            holder.ratingBar.setRating(0);
            holder.image.setImageResource(R.drawable.no_image);
            return;
        }

        holder.title.setText(info.getTitle() != null ? info.getTitle() : "No Title");

        List<String> authors = info.getAuthors();
        if (authors != null && !authors.isEmpty()) {
            holder.author.setText(authors.get(0));
        } else {
            holder.author.setText(R.string.unknown_author);
        }

        holder.ratingBar.setRating(info.getAverageRating());

        String imageUrl = (info.getImageLinks() != null) ? info.getImageLinks().getThumbnail() : null;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.image.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.icon_read_book)
                    .error(R.drawable.no_image)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.no_image);
        }

        holder.removeIcon.setOnClickListener(v -> {
            WishlistManager.removeFromWishlist(book);
            notifyItemRemoved(holder.getAdapterPosition());
            notifyItemRangeChanged(holder.getAdapterPosition(), bookList.size());
            Toast.makeText(v.getContext(), "Removed from Wishlist", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView title, author;
        ImageView image, removeIcon;
        RatingBar ratingBar;

        public BookViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);  // Ensure these IDs are present
            author = itemView.findViewById(R.id.bookAuthor);
            image = itemView.findViewById(R.id.bookImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            removeIcon = itemView.findViewById(R.id.removeIcon);
        }
    }
}
