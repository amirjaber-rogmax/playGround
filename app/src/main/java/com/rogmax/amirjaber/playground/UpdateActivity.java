package com.rogmax.amirjaber.playground;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rogmax.amirjaber.playground.facades.MainFacade;
import com.rogmax.amirjaber.playground.models.Value;
import com.rogmax.amirjaber.playground.retrofit.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateActivity extends AppCompatActivity {


    MainFacade userController;

    ProgressDialog progressDialog;
    Long id;
    String name, address;
    EditText updateName, updateAddress;
    Button update;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = getIntent();
        Long oldId = intent.getLongExtra("id", 0);
        progressDialog = new ProgressDialog(this);


        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete");
                builder
                        .setMessage("are you sure you would like to delete?")
                        .setCancelable(false)
                        .setPositiveButton("Delete", (dialog, which) -> {

                            Call<Value> call = userController.delete(oldId);
                            call.enqueue(new Callback<Value>() {
                                @Override
                                public void onResponse(@NonNull Call<Value> call, @NonNull Response<Value> response) {
                                    String value = response.body().getValue();
                                    String message = response.body().getMessage();
                                    progressDialog.dismiss();

                                    if (value.equals("1")) {
                                        Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Value> call, @NonNull Throwable t) {
                                    t.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(UpdateActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();

                                }
                            });
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Intent intent = getIntent();
        Long oldId = intent.getLongExtra("id", 0);
        String oldName = intent.getStringExtra("name");
        String oldAddress = intent.getStringExtra("address");

        updateName = findViewById(R.id.edit_text_name);
        updateAddress = findViewById(R.id.edit_text_address);
        update = findViewById(R.id.button_update);
        userController = APIUtils.getFacadeService();


        updateName.setText(oldName);
        updateAddress.setText(oldAddress);

        update.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Loading..");
            progressDialog.show();

            id = oldId;
            name = updateName.getText().toString();
            address = updateAddress.getText().toString();

            Call<Value> call = userController.update(id, name, address);
            call.enqueue(new Callback<Value>() {
                @Override
                public void onResponse(@NonNull Call<Value> call, @NonNull Response<Value> response) {
                    String value = response.body().getValue();
                    String message = response.body().getMessage();
                    progressDialog.dismiss();

                    if (value.equals("1")) {
                        Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Value> call, @NonNull Throwable t) {
                    t.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(UpdateActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();

                }
            });

        });

    }
}
