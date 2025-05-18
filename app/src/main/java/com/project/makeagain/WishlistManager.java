package com.project.makeagain;

import java.util.ArrayList;
import java.util.List;

public class WishlistManager {

    private static final List<ModelBook> wishlist = new ArrayList<>();

    public static void addToWishlist(ModelBook book) {
        if (!wishlist.contains(book)) wishlist.add(book);
    }

    public static void removeFromWishlist(ModelBook book) {
        wishlist.remove(book);
    }

    public static List<ModelBook> getWishlist() {
        return wishlist;
    }

}
