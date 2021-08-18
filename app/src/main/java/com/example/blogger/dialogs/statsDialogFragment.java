package com.example.blogger.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.blogger.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class statsDialogFragment extends DialogFragment {

    private MaterialToolbar toolbar;
    private FirebaseAnalytics analytics;

    public statsDialogFragment() {
        // Required empty public constructor
    }

    String title;

    public statsDialogFragment(String title) {
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        analytics = FirebaseAnalytics.getInstance(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();

        Objects.requireNonNull(getDialog())
                .getWindow()
                .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_stats_dialog, container, false);

        //call methods here
        init(view);
        myToolbar(view);
        getGraphDetails(view);

        //testWeatherAPI(view);

        return view;
    }

    private void init(ViewGroup view)
    {
        toolbar = view.findViewById(R.id.tool_bar);
    }

    private void myToolbar(ViewGroup view) {
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(v -> dismiss());
    }

    private void getGraphDetails(ViewGroup view)
    {
        FirebaseFirestore
                .getInstance()
                .collection("Posts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    int postNumber = queryDocumentSnapshots.size();

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data = new ArrayList<>();

                    data.add(new ValueDataEntry("Users", 12000));
                    data.add(new ValueDataEntry("Posts", postNumber*1000));
                    data.add(new ValueDataEntry("Likes", 18000));

                    pie.data(data);

                    AnyChartView anyChartView = view.findViewById(R.id.any_chart_view);
                    anyChartView.setChart(pie);
                });
    }

    private void testWeatherAPI(View view) {

        String url = "https://community-open-weather-map.p.rapidapi.com/find?q=london&cnt=0&mode=null&lon=0&type=link%2C%20accurate&lat=0&units=imperial%2C%20metric";
        String appid = "5208ad76fbmsh721c0599cfed623p1c7795jsneb3a9fc123f2";

        String tempUrl = url + "?q" + "polokwane" + "," + "south africa" + "&appid" + appid;

        StringRequest request = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
            }
        }, error ->
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show());

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}