package com.project.makeagain;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ModelBook {

    @SerializedName("volumeInfo")
    private VolumeInfo volumeInfo;

    @SerializedName("accessInfo")
    private AccessInfo accessInfo;

    public VolumeInfo getVolumeInfo() {
        return volumeInfo;
    }

    public AccessInfo getAccessInfo() {
        return accessInfo;
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
            return Optional.ofNullable(authors).orElse(Collections.emptyList());
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

    public static class AccessInfo {
        @SerializedName("webReaderLink")
        private String webReaderLink;

        public String getWebReaderLink() {
            return webReaderLink;
        }
    }


}
