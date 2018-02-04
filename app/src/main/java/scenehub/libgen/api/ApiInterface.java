package scenehub.libgen.api;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import scenehub.libgen.Book;
import scenehub.libgen.DetailsActivity;

public interface ApiInterface {

    @GET("libgen/api.php")
    Call<List<Book>> getBooksJSON(@QueryMap Map<String, Object> map);

    @GET("libgen/api.php")
    Call<DetailsActivity.ParseData> getDownloadUrlJSON(@Query("md5") String md5);

    @GET("libgen/api.php")
    Call<JsonObject> getDbInfoJSON(@Query("info") String asd);

    @GET("libgen/update.php")
    Call<JsonObject> getUpdateJSON();
}