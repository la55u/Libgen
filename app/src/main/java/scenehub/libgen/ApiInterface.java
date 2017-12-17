package scenehub.libgen;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @GET("libgen/api.php")
    Call<List<Book>> getBooksJSON(@QueryMap Map<String, Object> map);

    @GET("libgen/api.php")
    Call<DownloadUrl> getDownloadUrlJSON(@Query("md5") String md5);

}