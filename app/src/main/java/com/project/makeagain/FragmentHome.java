package com.project.makeagain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment {

    private TextView name;
    private EditText searchTerm;
    private RecyclerView recyclerView;
    private BookAdapterHome adapter;
    private List<ModelBook> bookList;
    private TextView statusMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.enterName);
        searchTerm = view.findViewById(R.id.searchTerm);
        recyclerView = view.findViewById(R.id.recyclerViewHome);
        bookList = new ArrayList<>();
        statusMessage = view.findViewById(R.id.statusMessage);

        // Observe name from SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getName().observe(getViewLifecycleOwner(), nameToSet -> {
            if (nameToSet != null && !nameToSet.trim().isEmpty()) {
                String firstName = Utils.getFirstWord(Utils.capitalizeWords(nameToSet));
                name.setText(firstName);
            } else {
                name.setText(R.string.reader);  // Default greeting
            }
        });

        setupRecyclerView();

        searchTerm.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||  // Handles ✔️ button
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String query = searchTerm.getText().toString().trim();
                if (!query.isEmpty()) {
                    fetchBooks(query);
                    hideKeyboard();
                }
                return true;
            }
            return false;
        });

    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns

        if (Utils.isNetworkAvailable(requireContext())) {
            statusMessage.setVisibility(View.GONE);
            fetchBooks("Chava"); // Call API when Enter or ✔️ is pressed
            hideKeyboard();
        } else {
            statusMessage.setText(R.string.no_internet_connection);
            statusMessage.setVisibility(View.VISIBLE);
        }

        adapter = new BookAdapterHome(getContext(), bookList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchBooks(String searchTerm) {

        if (!Utils.isNetworkAvailable(requireContext())) {
            statusMessage.setText(R.string.no_internet_connection);
            statusMessage.setVisibility(View.VISIBLE);
            bookList.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        statusMessage.setText(R.string.loading);
        statusMessage.setVisibility(View.VISIBLE);

        RetrofitInstance.getInstance().apiResponse.getBooks(searchTerm, BuildConfig.API_KEY)
                .enqueue(new Callback<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<BookResponse> call, @NonNull Response<BookResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getItems() != null) {
                            Log.d("API_RESPONSE", new Gson().toJson(response.body()));
                            bookList.clear();
                            bookList.addAll(response.body().getItems());
                            adapter.notifyDataSetChanged();

                            if (bookList.isEmpty()) {
                                statusMessage.setText(R.string.no_results_found);
                                statusMessage.setVisibility(View.VISIBLE);
                            } else {
                                statusMessage.setVisibility(View.GONE);
                            }
                        } else {
                            statusMessage.setText(R.string.no_results_found);
                            statusMessage.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BookResponse> call, @NonNull Throwable t) {
                        Log.e("API_ERROR", "Failed to fetch data", t);
                        statusMessage.setText(R.string.failed_to_load);
                        statusMessage.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = requireView();
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
