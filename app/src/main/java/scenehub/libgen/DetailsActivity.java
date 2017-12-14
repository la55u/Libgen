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

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "DetailsActivity";
    private Button btnDownload, btnFavorite;
    private DatabaseHelper dbHelper;
    private Book b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.activity_title_details);

        dbHelper = new DatabaseHelper(this);

        ImageView imageViewCover = findViewById(R.id.cover);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvAuthor = findViewById(R.id.tv_author);
        TextView tvYear = findViewById(R.id.tv_year);
        TextView tvExtension = findViewById(R.id.tv_extension);
        TextView tvFileSize = findViewById(R.id.tv_fileSize);
        TextView tvPublisher = findViewById(R.id.tv_publisher);
        TextView tvPages = findViewById(R.id.tv_pages);
        TextView tvEdition = findViewById(R.id.tv_edition);


        if(getIntent().getSerializableExtra("book") != null){
            b = (Book)getIntent().getSerializableExtra("book");

            Picasso.with(this).load("http://libgen.io/covers/" + b.getCoverurl()).into(imageViewCover);

            tvAuthor.setText(b.getAuthor());
            tvTitle.setText(b.getTitle());
            tvYear.setText(b.getYear());
            tvExtension.setText(b.getExtension());
            tvFileSize.setText(Formatter.formatFileSize(getApplicationContext(), b.getFilesize()));
            tvPublisher.setText(b.getPublisher());
            tvPages.setText(getString(R.string.pages_count,b.getPages()));
            tvEdition.setText(b.getEdition());

            getDownloadUrl(b.getMd5());
            initButtons();

        }

    }

    // 5. dia copy-paste
    public boolean requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // aszinkron módon magyarázat megjelenítése dialógusban,
                // majd újra kérés manuálisan
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            return true;
        }
        return false;
    }

    private void getDownloadUrl(String md5){
        NetworkManager.getInstance().getDownloadUrlJSON(md5).enqueue(new Callback<DownloadUrl>() {
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

    private void downloadFile(Book book) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(b.getDownloadUrl()));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String fileName = book.getTitle() +" by "+ book.getAuthor()+'.'+book.getExtension();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        DownloadManager manager = (DownloadManager)this.getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            manager.enqueue(request);
            Snackbar.make(findViewById(R.id.scrollview), "Download started", Snackbar.LENGTH_LONG).show();
        }

    }


   public void initButtons() {
       btnDownload = findViewById(R.id.btnDownload);
       btnDownload.setEnabled(false); //amig nincs behuzva a letolto link addig nem engedelyezett a gomb
       btnDownload.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (requestNeededPermission()) {
                   downloadFile(b);
               }
           }
       });

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
                   if (dbHelper.deleteFavoriteBook(b.getId())>0) {
                       Snackbar.make(findViewById(R.id.scrollview), "Successfully removed from favorites", Snackbar.LENGTH_LONG).show();
                       btnFavorite.setText(R.string.btn_text_add_favorite);
                   } else {
                       Snackbar.make(findViewById(R.id.scrollview), "Error occurred", Snackbar.LENGTH_LONG).show();
                   }
               }
           }
       });
   }


}