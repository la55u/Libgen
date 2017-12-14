package scenehub.libgen;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("libgen/api.php")
    Call<List<Book>> getBooksJSON(@Query("q") String query);

    @GET("libgen/api.php")
    Call<DownloadUrl> getDownloadUrlJSON(@Query("md5") String md5);
}