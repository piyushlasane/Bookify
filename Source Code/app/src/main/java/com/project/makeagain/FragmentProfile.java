package com.project.makeagain;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentProfile extends Fragment {

    private TextView name, username, editButton;
    private CircleImageView profileImage;
    private PieChart pieChart;
    private SharedViewModel sharedViewModel;
    private ActivityResultLauncher<Intent> resultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initViews(view);

        // Setup ViewModel & Observers
        setupViewModel();

        // Setup RecyclerView
        setupRecyclerView(view);

        // Setup PieChart with sample data
        setupPieChart();

        // Setup Edit Button
        setupEditButton();
    }

    private void initViews(View view) {
        name = view.findViewById(R.id.name);
        username = view.findViewById(R.id.username);
        profileImage = view.findViewById(R.id.profile_image);
        pieChart = view.findViewById(R.id.pieChart);
        editButton = view.findViewById(R.id.editButton);
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recentlyViewRecycler);
        TextView emptyText = view.findViewById(R.id.text_no_recently_viewed);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        JSONArray jsonArray = RecentlyViewedManager.getBooks(requireContext());
        List<ModelBook> books = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            String json = jsonArray.optString(i);
            ModelBook book = new Gson().fromJson(json, ModelBook.class);
            books.add(book);
        }

        Collections.reverse(books);
        if (books.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);

            BookAdapterProfile adapter = new BookAdapterProfile(getContext(), books);
            recyclerView.setAdapter(adapter);
        }
    }

    private void setupViewModel() {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getName().observe(getViewLifecycleOwner(), nameToSet -> name.setText(nameToSet != null && !nameToSet.trim().isEmpty() ?
                Utils.capitalizeWords(nameToSet) : getString(R.string.default_name)));

        sharedViewModel.getUsername().observe(getViewLifecycleOwner(), usernameToSet -> username.setText(usernameToSet != null && !usernameToSet.trim().isEmpty() ?
                String.format("@%s", Utils.getFirstWord(usernameToSet).toLowerCase()) : getString(R.string.default_username)));

        sharedViewModel.getGender().observe(getViewLifecycleOwner(), gender -> profileImage.setImageResource(
                gender != null && gender.equalsIgnoreCase("female") ?
                        R.drawable.profile_icon_female : R.drawable.profile_icon_male));
    }

    private void setupPieChart() {
        JSONArray jsonArray = RecentlyViewedManager.getBooks(requireContext());
        Map<String, Integer> categoryCountMap = new HashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            String json = jsonArray.optString(i);
            ModelBook book = new Gson().fromJson(json, ModelBook.class);

            if (book.getVolumeInfo() != null && book.getVolumeInfo().getCategories() != null) {
                for (String category : book.getVolumeInfo().getCategories()) {
                    categoryCountMap.put(category, categoryCountMap.getOrDefault(category, 0) + 1);
                }
            }
        }

        if (categoryCountMap.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            TextView pieEmptyText = requireView().findViewById(R.id.text_no_pie_data);
            pieEmptyText.setVisibility(View.VISIBLE);
            return;
        } else {
            requireView().findViewById(R.id.text_no_pie_data).setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
        }

        int total = categoryCountMap.values().stream().mapToInt(i -> i).sum();
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : categoryCountMap.entrySet()) {
            float percentage = (entry.getValue() * 100f) / total;
            entries.add(new PieEntry(percentage, entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        List<Integer> colors = Utils.getDistinctColors(entries.size());
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "%d%%", (int) value);
            }
        });

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Keep the rest same (styling, center text, etc.)
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(60f);
        pieChart.setHoleColor(getResources().getColor(R.color.page_background));
        pieChart.setCenterText("Categories");
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(getResources().getColor(R.color.black));

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setWordWrapEnabled(true);
        legend.setTextColor(getResources().getColor(R.color.black));
        legend.setTextSize(12f);

        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);

        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupEditButton() {
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                if (result.getData().hasExtra("name")) {
                    sharedViewModel.setName(result.getData().getStringExtra("name"));
                }
                if (result.getData().hasExtra("username")) {
                    sharedViewModel.setUsername(result.getData().getStringExtra("username"));
                }
                if (result.getData().hasExtra("gender")) {
                    sharedViewModel.setGender(result.getData().getStringExtra("gender"));
                }
            }

        });

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), PageEditProfile.class);
            intent.putExtra("name", sharedViewModel.getName().getValue());
            intent.putExtra("username", sharedViewModel.getUsername().getValue());
            intent.putExtra("gender", sharedViewModel.getGender().getValue());
            resultLauncher.launch(intent);
        });
    }

}