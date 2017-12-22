package scenehub.libgen;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = DetailsActivity.class.getSimpleName();
    private Button btnDownload, btnFavorite;
    private DatabaseHelper dbHelper;
    private Book b;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        getDownloadableData();
        initButtons();
    }


    public void initView(){
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvAuthor = findViewById(R.id.tv_author);
        TextView tvYear = findViewById(R.id.tv_year);
        TextView tvExtension = findViewById(R.id.tv_extension);
        TextView tvFileSize = findViewById(R.id.tv_fileSize);
        TextView tvPublisher = findViewById(R.id.tv_publisher);
        TextView tvPages = findViewById(R.id.tv_pages);
        TextView tvEdition = findViewById(R.id.tv_edition);
        TextView tvScanned = findViewById(R.id.tv_scanned);
        TextView tvLanguage = findViewById(R.id.tv_language);

        if(getIntent().getSerializableExtra("book") != null){
            b = (Book)getIntent().getSerializableExtra("book");

            tvAuthor.setText(b.getAuthor());
            tvTitle.setText(b.getTitle());
            tvYear.setText(b.getYear());
            tvExtension.setText(b.getExtension());
            tvFileSize.setText(Formatter.formatFileSize(getApplicationContext(), b.getFilesize()));
            tvPublisher.setText(b.getPublisher());
            tvPages.setText(getString(R.string.pages_count,b.getPages()));
            tvEdition.setText(b.getEdition().equals("") ? "-" : b.getEdition());
            tvLanguage.setText(b.getLanguage());
            tvScanned.setText(b.getScanned().equals("1") ? "Yes" : (b.getScanned().equals("0") ? "No" : "?"));
        }
    }

    // getting everything that sources from the web
    // this method is called in onCreate and in onRefresh
    private void getDownloadableData() {
        // getting cover image
        final ImageView cover = findViewById(R.id.cover);
        Picasso.with(this)
                .load("http://libgen.io/covers/" + b.getCoverurl())
                .into(cover, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        // TODO hide progressbar
                    }
                    @Override
                    public void onError() {
                        cover.setImageResource(R.drawable.cover_placeholder);
                    }
        });

        // getting download url
        getDownloadUrl();
    }


    public boolean requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            return true;
        }
        return false;
    }

    // gets the download url from the server based on the book's MD5
    private void getDownloadUrl(){
        NetworkManager.getInstance().getDownloadUrlJSON(b.getMD5()).enqueue(new Callback<DownloadUrl>() {
            @Override
            public void onResponse(Call<DownloadUrl> call, Response<DownloadUrl> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.isSuccessful()) {
                    btnDownload.setEnabled(true);
                    b.setDownloadUrl(response.body().getDownload());
                } else {
                    Snackbar.make(findViewById(R.id.scrollview), "Error: "+response.message(), Snackbar.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<DownloadUrl> call, Throwable t) {
                t.printStackTrace();
                Snackbar.make(findViewById(R.id.scrollview), "Error requesting download url, check LOG", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void downloadFile() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(b.getDownloadUrl()));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String fileName = b.getTitle() +" by "+ b.getAuthor()+'.'+b.getExtension();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        DownloadManager manager = (DownloadManager)this.getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
            Snackbar.make(findViewById(R.id.scrollview), "Download started", Snackbar.LENGTH_LONG).show();
        }
    }


   public void initButtons() {
       btnDownload = findViewById(R.id.btnDownload);
       btnDownload.setEnabled(false); //download button is not active until download link is received
       btnDownload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (requestNeededPermission()) {
                   downloadFile();
               }
           }
       });

       dbHelper = new DatabaseHelper(this);
       btnFavorite = findViewById(R.id.btnFavorite);
       if(dbHelper.isFavorite(b)){
           btnFavorite.setText(R.string.btn_text_remove_favorite);
       }
       btnFavorite.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(!dbHelper.isFavorite(b)){
                   if (dbHelper.insertFavoriteBook(b)) {
                       Snackbar.make(findViewById(R.id.scrollview), "Successfully added to favorites", Snackbar.LENGTH_LONG).show();
                       btnFavorite.setText(R.string.btn_text_remove_favorite);
                   } else {
                       Snackbar.make(findViewById(R.id.scrollview), "Error occurred", Snackbar.LENGTH_LONG).show();
                   }
               }else{
                   if (dbHelper.deleteFavoriteBook(b.getID())>0) {
                       Snackbar.make(findViewById(R.id.scrollview), "Successfully removed from favorites", Snackbar.LENGTH_LONG).show();
                       btnFavorite.setText(R.string.btn_text_add_favorite);
                   } else {
                       Snackbar.make(findViewById(R.id.scrollview), "Error occurred", Snackbar.LENGTH_LONG).show();
                   }
               }
           }
       });
   }


    @Override
    public void onRefresh() {
        getDownloadableData();
        swipeRefreshLayout.setRefreshing(false);
    }
}