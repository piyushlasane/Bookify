package com.project.makeagain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

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
        ModelBook.VolumeInfo volumeInfo = book.getVolumeInfo();


        if (volumeInfo == null) {
            holder.title.setText(R.string.no_title);
            holder.author.setText(R.string.unknown_author);
            holder.ratingBar.setRating(0);
            holder.image.setImageResource(R.drawable.no_image);
            return;
        }

        holder.title.setText(volumeInfo.getTitle() != null ? volumeInfo.getTitle() : "No Title");

        List<String> authors = volumeInfo.getAuthors();
        if (authors != null && !authors.isEmpty()) {
            holder.author.setText(authors.get(0));
        } else {
            holder.author.setText(R.string.unknown_author);
        }

        holder.ratingBar.setRating(volumeInfo.getAverageRating());

        String imageUrl = (volumeInfo.getImageLinks() != null) ? volumeInfo.getImageLinks().getThumbnail() : null;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http://")) {
                imageUrl = imageUrl.replace("http://", "https://");
            }
            Glide.with(holder.image.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.icon_read_book)
                    .error(R.drawable.no_image)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.no_image);
        }

        holder.removeIcon.setOnClickListener(v -> {

            Context context = v.getContext();

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                    .setTitle("Remove from Wishlist")
                    .setMessage("Do you really want to remove this book?")
                    .setIcon(R.drawable.icon_alert)
                    .setPositiveButton("Remove", (dialog, which) -> {
                        ModelBook bookToRemove = bookList.get(position);
                        WishlistManager.removeFromWishlist(bookToRemove);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Book removed", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(true);

            androidx.appcompat.app.AlertDialog dialog = builder.create();

            dialog.setOnShowListener(d -> {
                int black = ContextCompat.getColor(context, R.color.black);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(black);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(black);
            });

            dialog.show();
        });

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            @SuppressLint("InflateParams") View sheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet, null);
            BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.setContentView(sheetView);

            ImageView previewImage = sheetView.findViewById(R.id.previewImage);
            TextView previewTitle = sheetView.findViewById(R.id.previewTitle);
            TextView previewAuthor = sheetView.findViewById(R.id.previewAuthor);
            TextView descriptionView  = sheetView.findViewById(R.id.description );
            TextView btnOpenPreview = sheetView.findViewById(R.id.btnOpenPreview);
            ImageView starIcon = sheetView.findViewById(R.id.starIcon);
            if (WishlistManager.isInWishlist(book)) {
                starIcon.setImageResource(R.drawable.icon_filled_star);
            } else {
                starIcon.setImageResource(R.drawable.icon_star);
            }
            previewTitle.setText(volumeInfo.getTitle());
            previewAuthor.setText(volumeInfo.getAuthors() != null ? String.join(", ", volumeInfo.getAuthors()) : "Unknown Author");
            descriptionView.setText(volumeInfo.getDescription());

            starIcon.setOnClickListener(x -> {
                ScaleAnimation scale = new ScaleAnimation(
                        0.8f, 1.2f, // from X, to X
                        0.8f, 1.2f, // from Y, to Y
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                scale.setDuration(150);
                scale.setRepeatCount(1);
                scale.setRepeatMode(Animation.REVERSE);

                starIcon.startAnimation(scale);

                if (WishlistManager.isInWishlist(book)) {
                    WishlistManager.removeFromWishlist(book);
                    Toast.makeText(context, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                    starIcon.setImageResource(R.drawable.icon_star);
                } else {
                    WishlistManager.addToWishlist(book);
                    Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                    starIcon.setImageResource(R.drawable.icon_filled_star);
                }
            });



            String sheetImageUrl = volumeInfo.getImageLinks() != null ? volumeInfo.getImageLinks().getThumbnail() : null;
            if (!TextUtils.isEmpty(sheetImageUrl)) {
                if (sheetImageUrl.startsWith("http://")) {
                    sheetImageUrl = sheetImageUrl.replace("http://", "https://");
                }
                Glide.with(context)
                        .load(sheetImageUrl)
                        .placeholder(R.drawable.book_loading)
                        .error(R.drawable.no_image)
                        .into(previewImage);
            } else {
                previewImage.setImageResource(R.drawable.no_image);
            }

            btnOpenPreview.setOnClickListener(btn -> {
                Utils.haptic(btnOpenPreview.getContext());
                if (book.getAccessInfo() != null && book.getAccessInfo().getWebReaderLink() != null) {
                    String link = book.getAccessInfo().getWebReaderLink();

                    // Save to SharedPreferences
                    String bookJson = new Gson().toJson(book);
                    RecentlyViewedManager.addBook(context, bookJson);

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    context.startActivity(intent);
                    dialog.dismiss();
                }
            });

            dialog.show();
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
