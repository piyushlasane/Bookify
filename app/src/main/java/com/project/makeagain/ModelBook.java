package com.project.makeagain;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

public class ModelBook {

    @SerializedName("volumeInfo")
    private VolumeInfo volumeInfo;

    public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }

    public static class VolumeInfo {
        @SerializedName("title")
        private String title;

        @SerializedName("authors")
        private List<String> authors;

        @SerializedName("imageLinks")
        private ImageLinks imageLinks;  // Correct place for imageLinks

        public String getTitle() {
            return title;
        }

        public List<String> getAuthors() {
            return authors != null ? authors : Collections.emptyList();
        }

        public ImageLinks getImageLinks() {
            return imageLinks;
        }
    }

    public static class ImageLinks {
        @SerializedName("smallThumbnail")  // Handle both smallThumbnail & thumbnail
        private String smallThumbnail;

        @SerializedName("thumbnail")
        private String thumbnail;

        public String getThumbnail() {
            return (thumbnail != null) ? thumbnail : smallThumbnail; // Return best available image
        }
    }
}
