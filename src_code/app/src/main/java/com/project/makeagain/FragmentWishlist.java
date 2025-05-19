package com.project.makeagain;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FragmentWishlist extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<ModelBook> wishlist = WishlistManager.getWishlist();
        View emptyView = view.findViewById(R.id.empty_view);
        TextView exploreButton = view.findViewById(R.id.exploreButton);

        if (wishlist.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            exploreButton.setOnClickListener(v -> {
                // Example: navigate to HomeFragment or wherever you want
                // If you use Navigation Component:
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.fragmentHome);
            });

        } else {
            view.findViewById(R.id.empty_view).setVisibility(View.GONE);
        }

        RecyclerView recyclerView = view.findViewById(R.id.recyclerWishlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new WishlistAdapter(wishlist));
    }

}