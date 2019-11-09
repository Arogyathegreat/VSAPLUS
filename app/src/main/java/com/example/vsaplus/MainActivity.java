package com.example.vsaplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private long time = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.vsaplus", PackageManager.GET_SIGNATURES);
            for(Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash: ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }catch (PackageManager.NameNotFoundException e){

        }catch (NoSuchAlgorithmException e){

        }


        setContentView(R.layout.activity_main);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


        loadFragment(new Homefragment());


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(MainActivity.this);



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int newPosition = 0;

        switch (item.getItemId()) {

            case R.id.home:
                fragment = new Homefragment();
                newPosition = 1;
                break;

            case R.id.games:
                fragment = new GamelistFragment();
                newPosition = 2;
                break;

            case R.id.community:
                fragment = new CommunityFragment();
                newPosition = 3;
                break;

            case R.id.profile:
                fragment = new SignupFragment();
                newPosition = 4;
                break;

        }

        return loadFragment(fragment);
    }

    public boolean loadFragment(Fragment fragment) {
        //switching fragment

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.bottom_navigation_frame, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    @Override
        public void onBackPressed(){

        if((this.getSupportFragmentManager().findFragmentById(R.id.bottom_navigation_frame) instanceof Homefragment))//home fragment 일경우
        {
            if(System.currentTimeMillis()-time>=2000){
                time=System.currentTimeMillis();
                Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
            }else if(System.currentTimeMillis()-time<2000){
                finish();
            }

        }
        else if(this.getSupportFragmentManager().findFragmentById(R.id.bottom_navigation_frame) instanceof WriteCommunityFragment
                ||this.getSupportFragmentManager().findFragmentById(R.id.bottom_navigation_frame) instanceof PostViewFragment ){
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            MenuItem item = bottomNavigationView.getMenu().findItem(R.id.community);
            bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId()).setChecked(false);
            item.setChecked(true);
            onNavigationItemSelected(item);
        }
        else{//homefragment 아닐경우
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            MenuItem item = bottomNavigationView.getMenu().findItem(R.id.home);
            item.setChecked(true);
            bottomNavigationView.getMenu().findItem(bottomNavigationView.getSelectedItemId()).setChecked(false);
            onNavigationItemSelected(item);
        }

        }
}
