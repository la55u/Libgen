package scenehub.libgen;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private RecyclerView recyclerView;
    private ArrayList<Book> books;
    private DataAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initFAB();
        startSearch();
    }


    private void startSearch(){
        Map<String, Object> map = new HashMap<>();
        if(getIntent().getSerializableExtra("query") != null){
            String query = (String)getIntent().getSerializableExtra("query");
            map.put("query",query);
        }
        else if (getIntent().getSerializableExtra("barcode") != null){
            String barcode = (String)getIntent().getSerializableExtra("barcode");
            map.put("barcode",barcode);
        }
        loadJSON(map);
    }

    private void initFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.action_search).performClick();
            }
        });
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void loadJSON(Map map){
        NetworkManager.getInstance().getBooksJSON(map).enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.isSuccessful()) {
                    Snackbar.make(findViewById(R.id.mylayout), response.body().size()+" results found for your query", Snackbar.LENGTH_LONG).show();
                    books = new ArrayList<>(response.body());
                    adapter = new DataAdapter(books, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                } else {
                    Snackbar.make(findViewById(R.id.mylayout), "Error: "+response.message(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                t.printStackTrace();
                Snackbar.make(findViewById(R.id.mylayout), "Error in network request, check LOG", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
