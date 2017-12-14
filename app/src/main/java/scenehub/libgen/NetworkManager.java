package scenehub.libgen;

import java.util.List;

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



    public Call<List<Book>> getBooksJSON(String query) {
        return apiInterface.getBooksJSON(query);
    }

    public Call<DownloadUrl> getDownloadUrlJSON(String md5) {
        return apiInterface.getDownloadUrlJSON(md5);
    }

}