package com.project.makeagain;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private ImageView micBtn;
    private ActivityResultLauncher<Intent> speechLauncher;
    private SwipeRefreshLayout swipeRefresh;

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
        micBtn = view.findViewById(R.id.micBtn);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        speechLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ArrayList<String> results = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (results != null && !results.isEmpty()) {
                            String spokenText = results.get(0).trim();
                            searchTerm.setText(spokenText);

                            // ✅ Trigger the search automatically
                            if (!TextUtils.isEmpty(spokenText)) {
                                fetchBooks(spokenText);
                                hideKeyboard();
                                searchTerm.clearFocus();
                            }
                        }
                    }
                });

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

        micBtn.setOnClickListener(v -> {
            Utils.haptic(requireContext());
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");

            try {
                speechLauncher.launch(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
            }
        });

        if (Utils.isNetworkAvailable(requireContext())) {
            statusMessage.setVisibility(View.GONE);
            fetchBooks(Utils.getRandomKeyword()); // Call API when Enter or ✔️ is pressed
        } else {
            statusMessage.setText(R.string.no_internet_connection);
            statusMessage.setVisibility(View.VISIBLE);
        }

        searchTerm.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||  // Handles ✔️ button
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String query = searchTerm.getText().toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    fetchBooks(query);
                    hideKeyboard();
                    searchTerm.clearFocus();
                }
                return true;
            }
            return false;
        });

        swipeRefresh.setOnRefreshListener(() -> {
            hideKeyboard();
            searchTerm.clearFocus();
            searchTerm.setText("");
            fetchBooks(Utils.getRandomKeyword());  // Get fresh books on pull
            swipeRefresh.setRefreshing(false); // Stop the spinner manually after fetch
        });

    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns
        Context context = getContext();
        if (context == null) return;
        adapter = new BookAdapterHome(context, bookList);
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchBooks(String searchTerm) {

        // Step 1: Clear list immediately
        bookList.clear();
        adapter.startShimmer();  // Already calls notifyDataSetChanged()
        statusMessage.setVisibility(View.GONE);  // Hide any status message during shimmer

        // Step 2: Handle no internet
        // Handle no internet
        if (!Utils.isNetworkAvailable(requireContext())) {
            statusMessage.setText(R.string.no_internet_connection);
            statusMessage.setVisibility(View.VISIBLE);
            adapter.stopShimmer(); // Stop shimmer if no network
            return;
        }

        // Step 4: Make API call
        RetrofitInstance.getInstance().apiResponse.getBooks(searchTerm, BuildConfig.API_KEY)
                .enqueue(new Callback<>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<BookResponse> call, @NonNull Response<BookResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getItems() != null) {
                            bookList.addAll(response.body().getItems());
                            adapter.stopShimmer(); // stop shimmer and show real data
                            adapter.notifyDataSetChanged();

                            if (bookList.isEmpty()) {
                                statusMessage.setText(R.string.no_results_found);
                                statusMessage.setVisibility(View.VISIBLE);
                            } else {
                                statusMessage.setVisibility(View.GONE);  // ✅ Hide message when results found
                            }
                        } else {
                            statusMessage.setText(R.string.no_results_found);
                            statusMessage.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<BookResponse> call, @NonNull Throwable t) {
                        adapter.stopShimmer();
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
