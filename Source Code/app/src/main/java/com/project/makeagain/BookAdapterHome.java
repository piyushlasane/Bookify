package com.project.makeagain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.util.List;

public class BookAdapterHome extends RecyclerView.Adapter<BookAdapterHome.BookHolder> {

    private final Context context;
    private final List<ModelBook> bookList;
    private final LayoutInflater inflater;
    private boolean showShimmer = true; // initially true
    private static final int SHIMMER_ITEM_COUNT = 6;
    private static final int VIEW_TYPE_SHIMMER = 0;
    private static final int VIEW_TYPE_BOOK = 1;

    @Override
    public int getItemCount() {
        return showShimmer ? SHIMMER_ITEM_COUNT : bookList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return showShimmer ? VIEW_TYPE_SHIMMER : VIEW_TYPE_BOOK;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void startShimmer() {
        showShimmer = true;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void stopShimmer() {
        if (showShimmer) {
            showShimmer = false;
            notifyDataSetChanged();
        }
    }

    public BookAdapterHome(Context context, List<ModelBook> bookList) {
        this.context = context;
        this.bookList = bookList;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_SHIMMER) {
            view = inflater.inflate(R.layout.item_shimmer_book, parent, false);
        } else {
            view = inflater.inflate(R.layout.book_item_home, parent, false);
        }
        return new BookHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull BookHolder holder, int position) {

        if (showShimmer) {
            ShimmerFrameLayout shimmerLayout = (ShimmerFrameLayout) holder.itemView;
            shimmerLayout.startShimmer();
            return;
        }

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
            String mainImageUrl = volumeInfo.getImageLinks() != null ? volumeInfo.getImageLinks().getThumbnail() : null;
            if (!TextUtils.isEmpty(mainImageUrl)) {
                if (mainImageUrl.startsWith("http://")) {
                    mainImageUrl = mainImageUrl.replace("http://", "https://");
                }
                holder.bookImage.setVisibility(View.INVISIBLE);
                holder.shimmerImageLayout.startShimmer();
                holder.shimmerImageLayout.setVisibility(View.VISIBLE);

                Glide.with(context)
                        .load(mainImageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.color.white) // optional fallback
                        .error(R.drawable.no_image)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                holder.bookImage.setVisibility(View.VISIBLE);
                                holder.shimmerImageLayout.stopShimmer();
                                holder.shimmerImageLayout.setVisibility(View.GONE);
                                return false; // let Glide handle error placeholder
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                holder.bookImage.setVisibility(View.VISIBLE);
                                holder.shimmerImageLayout.stopShimmer();
                                holder.shimmerImageLayout.setVisibility(View.GONE);
                                return false; // let Glide set the image
                            }
                        })
                        .into(holder.bookImage);

            } else {
                holder.bookImage.setImageResource(R.drawable.no_image);
            }

            // Handle click to open webReaderLink
            holder.itemView.setOnClickListener(v -> {

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
                            .placeholder(R.color.white)
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
    }

    static class BookHolder extends RecyclerView.ViewHolder {
        TextView bookTitle, bookAuthor;
        ImageView bookImage;
        ShimmerFrameLayout shimmerImageLayout;

        public BookHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            bookImage = itemView.findViewById(R.id.bookImage);
            shimmerImageLayout = itemView.findViewById(R.id.shimmerImageLayout);
        }
    }
}
