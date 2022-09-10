package com.example.maveryth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.maveryth.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.invoke.MutableCallSite;
import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ActivityMainBinding binding;
    FirebaseAuth auth;

    ImageView fvrtBtn;

    Button down;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference ref;
    public static final int REQUEST_CODE=1;
    static ArrayList<MusicFiles>musicFiles;
    static boolean shuffleBoolean = false, repeatBoolean = false,fvrtBoolean=false;
    static ArrayList<MusicFiles> albums = new ArrayList<>();
    private String MY_SORT_PREF="SortOrder";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        permission();
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("message");
        auth=FirebaseAuth.getInstance();
        initViewPager();

       /* fvrtBtn.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                if(fvrtBoolean)
                {
                    fvrtBoolean = false;
                    fvrtBtn.setImageResource(R.drawable.ic_fav_off);
                }
                else
                {
                    fvrtBoolean = true;
                    fvrtBtn.setImageResource(R.drawable.ic_fav_on);
                }
            }
        });*/
        down=findViewById(R.id.down);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });
    }
    private void download() {

        storageReference=firebaseStorage.getInstance().getReference();
        ref=storageReference.child("Linkin Park - A Light That Never Comes.mp3");

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
              downloadMusic(MainActivity.this,"Linkin Park - A Light That Never Comes",".mp3",DIRECTORY_DOWNLOADS,url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {

            }
        });
    }

    private void downloadMusic(Context context,String fileName,String fileExtension,String destinationDirectory,String url) {

        DownloadManager downloadManager=(DownloadManager) context.
                getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+fileExtension);

        downloadManager.enqueue(request);

    }

    //pt2
    private void permission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    ,REQUEST_CODE);
        }
        else
        {
            //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            musicFiles = getAllAudio(this);// pt2 951, pt3035
            initViewPager(); //pt3 31
        }
    }
    //pt2 351
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE)
        {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                //3_36pt2 // do toast
                 //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                musicFiles = getAllAudio(this); //pt2 1000
               initViewPager(); //pt3040  music items.xml
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        ,REQUEST_CODE);

            }
        }
    }

    //pt1_414/552
    private void initViewPager()
    {
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPagerAdapter viewPagerAdapter = new viewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragments(new SongsFragment(),"Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Album");
        viewPager.setAdapter(viewPagerAdapter);
        //8_58 P1
        tabLayout.setupWithViewPager(viewPager);
    }

    public static class viewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment>fragments;
        private ArrayList<String>titles;
        public viewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }
        void addFragments(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    //522pt2
    public  ArrayList<MusicFiles>getAllAudio(Context context)
    {
        SharedPreferences preferences=getSharedPreferences(MY_SORT_PREF,MODE_PRIVATE);
        String sortOrder= preferences.getString("sorting","sortByName");
        ArrayList<String> duplicate = new ArrayList<>();
        albums.clear();
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        String order=null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch (sortOrder)
        {
            case "sortByName":
                order=MediaStore.MediaColumns.DISPLAY_NAME+ " ASC";
                break;
            case "sortByDate":
                order=MediaStore.MediaColumns.DATE_ADDED+ " ASC";
                break;
            case "sortBySize":
                order=MediaStore.MediaColumns.SIZE+ " DESC";
                break;
        }
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST

                //655p2
        };

        Cursor cursor = context.getContentResolver().query(uri,projection,
                null,null,order);
        if(cursor != null)
        {
            while(cursor.moveToNext())
            {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration);
                //926pt2
                Log.e ("Path : " + path, "Album : " + album);
                tempAudioList.add(musicFiles);
                if(!duplicate.contains(album))
                {
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close(); //pt2 930
        }
        return tempAudioList;
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search,menu); //search music
        MenuItem menuItem=menu.findItem(R.id.search_option);
        SearchView searchView =(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        MenuInflater inflater=getMenuInflater();

        inflater.inflate(R.menu.menu,menu); //log out

        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SharedPreferences.Editor editor=getSharedPreferences(MY_SORT_PREF,MODE_PRIVATE).edit();
        switch (item.getItemId()){

            case R.id.by_name:
                editor.putString("sorting","sortByName");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_date:
                editor.putString("sorting","sortByDate");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_size:
                editor.putString("sorting","sortBySize");
                editor.apply();
                this.recreate();
                break;
            case R.id.logout:
                auth.signOut();
                Intent intent=new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput=newText.toLowerCase();
        ArrayList<MusicFiles>myFiles=new ArrayList<>();
        for (MusicFiles song:musicFiles) {
            if(song.getTitle().toLowerCase().contains(userInput))
            {
                myFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateList(myFiles);
        return true;
    }

}