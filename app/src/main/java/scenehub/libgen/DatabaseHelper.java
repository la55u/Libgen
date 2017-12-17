package scenehub.libgen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
    static final String DATABASE_NAME ="Book.db";
    static final String TABLE_NAME_FAV ="favorites_table";
    static final String ID ="id";
    static final String TITLE ="title";
    static final String AUTHOR ="author";
    static final String YEAR ="year";
    static final String PUBLISHER ="publisher";
    static final String FILESIZE ="filesize";
    static final String EXTENSION ="extension";
    static final String PAGES ="pages";
    static final String COVERURL ="coverurl";
    static final String EDITION ="edition";
    static final String MD5 ="md5";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ TABLE_NAME_FAV +" (id integer primary key autoincrement, title text, author text,"+
                "year text, publisher text, filesize text, extension text, pages text, coverurl text, md5 text, edition text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME_FAV);
    }

    public boolean insertFavoriteBook(Book b){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, b.getID());
        contentValues.put(TITLE,b.getTitle());
        contentValues.put(AUTHOR,b.getAuthor());
        contentValues.put(YEAR,b.getYear());
        contentValues.put(PUBLISHER,b.getPublisher());
        contentValues.put(FILESIZE,b.getFilesize());
        contentValues.put(EXTENSION,b.getExtension());
        contentValues.put(PAGES,b.getPages());
        contentValues.put(COVERURL,b.getCoverurl());
        contentValues.put(EDITION,b.getEdition());
        contentValues.put(MD5,b.getMD5());
        long result = db.insert(TABLE_NAME_FAV, null, contentValues);
        return result != -1;
    }

    public Integer deleteFavoriteBook(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_FAV, "ID = ?", new String[] {id});
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+ TABLE_NAME_FAV, null);
    }


    public boolean isFavorite(Book b){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM favorites_table WHERE id = ?", new String[]{b.getID()});

        if(cursor!=null && cursor.moveToFirst() && cursor.getCount()>0){
            return true;
        }
        return false;
    }

}
