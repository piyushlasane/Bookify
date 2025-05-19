package com.project.makeagain;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ModelBook {

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ModelBook other = (ModelBook) obj;

        String thisId = (this.getVolumeInfo() != null ? this.getVolumeInfo().getTitle() : "")
                + (this.getAccessInfo() != null ? this.getAccessInfo().getWebReaderLink() : "");

        String otherId = (other.getVolumeInfo() != null ? other.getVolumeInfo().getTitle() : "")
                + (other.getAccessInfo() != null ? other.getAccessInfo().getWebReaderLink() : "");

        return thisId.equals(otherId);
    }

    @Override
    public int hashCode() {
        String id = (getVolumeInfo() != null ? getVolumeInfo().getTitle() : "")
                + (getAccessInfo() != null ? getAccessInfo().getWebReaderLink() : "");
        return id.hashCode();
    }

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

        @SerializedName("description")
        private String description;

        @SerializedName("averageRating")
        private float averageRating;

        public String getTitle() {
            return title;
        }

        public List<String> getAuthors() {
            return Optional.ofNullable(authors).orElse(Collections.emptyList());
        }

        public ImageLinks getImageLinks() {
            return imageLinks;
        }

        public String getDescription() {
            return (description != null) ? description : "No description available.";
        }

        public float getAverageRating() {
            return averageRating;
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
