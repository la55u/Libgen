package scenehub.libgen;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class FragmentFavorites extends android.support.v4.app.Fragment {

    DataAdapter adapter;
    ArrayList<Book> favoriteBooks;
    RecyclerView recyclerView;

    TextView tvFavCnt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        favoriteBooks = (ArrayList<Book>) Book.listAll(Book.class);
        recyclerView = getView().findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DataAdapter(favoriteBooks, getContext());
        recyclerView.setAdapter(adapter);

        tvFavCnt = getView().findViewById(R.id.favcount);
        tvFavCnt.setText(getString(R.string.tab_favorites_text, favoriteBooks.size()));

    }


}
