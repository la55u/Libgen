package scenehub.libgen;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by LA on 029, 10/29.
 */

public class FragmentFavorites extends android.support.v4.app.Fragment {

    DatabaseHelper dbHelper;
    ArrayList<Book> favoriteBooks;
    RecyclerView recyclerView;
    DataAdapter adapter;
    Cursor cursor;
    TextView textView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoriteBooks = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DataAdapter(favoriteBooks, getContext());
        recyclerView.setAdapter(adapter);

        dbHelper = new DatabaseHelper(getActivity());
        cursor = dbHelper.getAllData();

        textView = rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.tab_favorites_text, cursor.getCount()));

        initFavorites();
        return rootView;
    }

public void initFavorites(){
    if(cursor != null && cursor.moveToFirst()){
        do{
            String id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ID));
            String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE));
            String author = cursor.getString(cursor.getColumnIndex(DatabaseHelper.AUTHOR));
            String edition = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EDITION));
            String year = cursor.getString(cursor.getColumnIndex(DatabaseHelper.YEAR));
            String pages = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PAGES));
            Long fileSize = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.FILESIZE));
            String coverUrl = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COVERURL));
            String extension = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EXTENSION));
            String md5 = cursor.getString(cursor.getColumnIndex(DatabaseHelper.MD5));
            String publisher = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PUBLISHER));
            String language = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PUBLISHER));
            String scanned = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PUBLISHER));

            Book b = new Book(id,title,author,year,publisher,fileSize,extension,pages,coverUrl,md5,edition, language, scanned, null);
            favoriteBooks.add(b);

        }while (cursor.moveToNext());

    }


}





}
