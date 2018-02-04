package scenehub.libgen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import scenehub.libgen.api.ApiClient;


public class FragmentHome extends android.support.v4.app.Fragment{
    private Map<String, Object> dbInfo;
    private static final String TAG = FragmentHome.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        new Ping(getView(), R.id.state_server).execute("https://scenehub.tk");
        new Ping(getView(), R.id.state_libgen).execute("http://libgen.io");
        getDbInfo();
    }

    private void getDbInfo(){
        ApiClient.getInstance().getDbInfoJSON().enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d(TAG, "onResponse: " + response.code());
                if (response.isSuccessful()) {
                    //Toast.makeText(getContext(), response.body().toString(), Toast.LENGTH_LONG).show();
                    dbInfo = new Gson().fromJson(response.body(), new TypeToken<HashMap<String, String>>(){}.getType());
                    fillInfo();
                } else {
                    Toast.makeText(getContext(), "Error: "+response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "Error in network request: "+t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fillInfo() {
        View view = getView();
        if(view != null){
            TextView dbLast = view.findViewById(R.id.state_db);
            TextView dbCnt = view.findViewById(R.id.state_dbcnt);

            String cnt = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(dbInfo.get("cnt").toString()));
            dbCnt.setText(cnt);
            dbLast.setText(dbInfo.get("last").toString());

        }
    }

    private static class Ping extends AsyncTask<String, Void, String>{

        private View rootView;
        private int textViewID;

        Ping(View rootView, int textViewID){
            this.rootView = rootView;
            this.textViewID = textViewID;
        }

        @Override
        protected String doInBackground(String... urls) {
            return pingURL(urls[0], 3000) ? "UP" : "DOWN";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView stateView = rootView.findViewById(textViewID);
            stateView.setText(s);
        }


        /*
         * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in
         * the 200-399 range.
        */
        private static boolean pingURL(String url, int timeout) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(timeout);
                connection.setReadTimeout(timeout);
                connection.setRequestMethod("HEAD");
                int responseCode = connection.getResponseCode();
                return (200 <= responseCode && responseCode <= 399);
            } catch (IOException exception) {
                return false;
            }
        }


    }



}
