package scenehub.libgen;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    private static final String BASE_URL = "https://scenehub.tk";

    private static NetworkManager instance;
    private ApiInterface apiInterface;

    private NetworkManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
    }


    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }



    public Call<List<Book>> getBooksJSON(Map map) {
        return apiInterface.getBooksJSON(map);
    }

    public Call<DetailsActivity.ParseData> getDownloadUrlJSON(String md5) {
        return apiInterface.getDownloadUrlJSON(md5);
    }

    public Call<JsonObject> getDbInfoJSON(){
        return apiInterface.getDbInfoJSON("asd");
    }

}