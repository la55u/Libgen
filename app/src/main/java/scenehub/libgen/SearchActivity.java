package scenehub.libgen;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private static final String TAG = SearchActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private ArrayList<Book> books;
    private DataAdapter adapter;
    private ProgressBar progressBar = null;
    private Map<String, Object> searchMap;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        startSearch();
    }


    private void startSearch(){
        searchMap = new HashMap<>();
        if(getIntent().getSerializableExtra("query") != null){
            String query = (String)getIntent().getSerializableExtra("query");
            searchMap.put("query",query);
        }
        else if (getIntent().getSerializableExtra("barcode") != null){
            String barcode = (String)getIntent().getSerializableExtra("barcode");
            searchMap.put("barcode",barcode);
        }
        loadJSON();
    }


    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void loadJSON(){
        NetworkManager.getInstance().getBooksJSON(searchMap).enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                Log.d(TAG, "onResponse: " + response.code());
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
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
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                t.printStackTrace();
                Snackbar.make(findViewById(R.id.mylayout), "Error in network request, check LOG", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        loadJSON();
    }
}
