package com.project.makeagain;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class BookResponse {
    @SerializedName("items")
    private List<ModelBook> items;

    public List<ModelBook> getItems() {
        return items != null ? items : Collections.emptyList();
    }

}
