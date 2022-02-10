package com.example.hadasrecipe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hadasrecipe.utils.BaseActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class ApiRecipesPage extends BaseActivity implements ApiRecipesInterface {

    RecyclerView recipesRV;
    ApiRecipesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_recipes_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recipesRV = findViewById(R.id.recipes_list);
        adapter = new ApiRecipesAdapter(getApplicationContext(), this);
        recipesRV.setLayoutManager(new LinearLayoutManager(this));
        recipesRV.setAdapter(adapter);

        EditText searchRecipeEditText = findViewById(R.id.search_recipe);

        Button searchButton = findViewById(R.id.search_recipe_button);
        searchButton.setOnClickListener(v -> new RequestTask(searchRecipeEditText.getText().toString()).execute());
    }

    @Override
    public void goToRecipePage(String url) {
        Intent intent = new Intent(this, WebView.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class RequestTask extends AsyncTask<String, String, String> {
        ObjectMapper objectMapper = new ObjectMapper();
        String searchText;
//        ProgressDialog progressDialog;

        public RequestTask(String searchText) {
            this.searchText = searchText;
        }

        @Override
        protected String doInBackground(String... uri) {
//            progressDialog = new ProgressDialog(ApiRecipesPage.this);
//            progressDialog.setCancelable(false);
//            progressDialog.setMessage("Loading...");
//            progressDialog.show();

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                HttpGet httpGet = new HttpGet("https://api.edamam.com/search?app_id=900da95e&app_key=40698503668e0bb3897581f4766d77f9&q=" + searchText);

                response = httpclient.execute(httpGet);

                if (response.getStatusLine().getStatusCode() == 200) {
                    Log.d("response ok", "ok response :/");
                } else {
                    Log.d("response not ok", "Something went wrong :/ " + response.getStatusLine().getStatusCode());
                }

                responseString = new BasicResponseHandler().handleResponse(response);
                System.out.println();
            } catch (IOException e) {
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            progressDialog.dismiss();
            try {
                RecipesApiResponse recipesApiResponse = objectMapper.readValue(result, RecipesApiResponse.class);
                adapter.updateList(recipesApiResponse.hits);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}