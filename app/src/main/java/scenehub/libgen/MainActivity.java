package scenehub.libgen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.atzcx.appverupdater.AppVerUpdater;
import com.github.atzcx.appverupdater.UpdateErrors;
import com.github.atzcx.appverupdater.callback.Callback;


public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    AppVerUpdater appVerUpdater = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        initFAB();

        checkUpdate();

    }

    private void initFAB(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.requestFocus();
                findViewById(R.id.action_search).performClick();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.menu_settings:
                Snackbar.make(findViewById(R.id.main_content), "Coming soon", Snackbar.LENGTH_LONG).show();
                return true;
            case R.id.menu_scan:
                Intent intent = new Intent(getApplicationContext(), ScanBarcodeActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new FragmentHome();
                case 1: return new FragmentFavorites();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }




    private void checkUpdate(){

        appVerUpdater = new AppVerUpdater()
                .setUpdateJSONUrl("http:/scenehub.tk/libgen/update.json")
                .setShowNotUpdated(true)
                .setViewNotes(true)
                .setCallback(new Callback() {
                    @Override
                    public void onFailure(UpdateErrors error) {

                        if (error == UpdateErrors.NETWORK_NOT_AVAILABLE) {
                            Toast.makeText(MainActivity.this, "No internet connection.", Toast.LENGTH_LONG).show();
                        }
                        else if (error == UpdateErrors.ERROR_CHECKING_UPDATES) {
                            Toast.makeText(MainActivity.this, "An error occurred while checking for updates.", Toast.LENGTH_LONG).show();
                        }
                        else if (error == UpdateErrors.ERROR_DOWNLOADING_UPDATES) {
                            Toast.makeText(MainActivity.this, "An error occurred when downloading updates.", Toast.LENGTH_LONG).show();
                        }
                        else if (error == UpdateErrors.JSON_FILE_IS_MISSING) {
                            Toast.makeText(MainActivity.this, "Json file is missing.", Toast.LENGTH_LONG).show();
                        }
                        else if (error == UpdateErrors.FILE_JSON_NO_DATA) {
                            Toast.makeText(MainActivity.this, "The file containing information about the updates are empty.", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onDownloadSuccess() {
                        // for example, record/reset license
                    }

                    @Override
                    public void onUpdateChecked(boolean downloading) {
                        // Happens after an checkUpdate check, immediately after if checkUpdate check was successful and there
                        // were no dialogs, or, when an checkUpdate dialog is presented and user explicitly dismissed the dialog.
                        // "downloading" is true if user accepted the checkUpdate
                        // Typically used for resetting next checkUpdate check time
                    }
                })
                .setAlertDialogCancelable(true)
                .build(this);

    }


}
