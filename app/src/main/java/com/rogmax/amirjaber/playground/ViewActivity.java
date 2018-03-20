package com.rogmax.amirjaber.playground;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.rogmax.amirjaber.playground.adapters.RecyclerViewAdapter;
import com.rogmax.amirjaber.playground.facades.MainFacade;
import com.rogmax.amirjaber.playground.models.LocationDto;
import com.rogmax.amirjaber.playground.models.Value;
import com.rogmax.amirjaber.playground.retrofit.APIUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<LocationDto> list;
    private RecyclerViewAdapter viewAdapter;
    MainFacade userController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        viewAdapter = new RecyclerViewAdapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);
        userController = APIUtils.getFacadeService();

        loadDataList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataList();
    }

    private void loadDataList() {

        Call<Value> call = userController.read();

        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(@NonNull Call<Value> call, @NonNull Response<Value> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                progressBar.setVisibility(View.GONE);

                if (value.equals("1")) {
                    list = response.body().getResult();
                    viewAdapter = new RecyclerViewAdapter(ViewActivity.this, list);
                    recyclerView.setAdapter(viewAdapter);
                    Toast.makeText(ViewActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search for name");
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Call<Value> call = userController.search(newText);

        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (value.equals("1")) {
                    list = response.body().getResult();
                    viewAdapter = new RecyclerViewAdapter(ViewActivity.this, list);
                    recyclerView.setAdapter(viewAdapter);
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
        return true;
    }

}
