package arvindandroid.com.arvind.bingoonlinegame.Activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import arvindandroid.com.arvind.bingoonlinegame.Fragments.ChooseDefaultBingoMatrixFragment;
import arvindandroid.com.arvind.bingoonlinegame.Fragments.GameFragment;
import arvindandroid.com.arvind.bingoonlinegame.Fragments.HowToPlayFragment;
import arvindandroid.com.arvind.bingoonlinegame.Fragments.PlayOptionFragment;
import arvindandroid.com.arvind.bingoonlinegame.Fragments.SettingFragment;
import arvindandroid.com.arvind.bingoonlinegame.Models.User;
import arvindandroid.com.arvind.bingoonlinegame.R;
import arvindandroid.com.arvind.bingoonlinegame.Service.MyService;
import arvindandroid.com.arvind.bingoonlinegame.Utils.NetworkCheck;

public class OptionsActivity extends AppCompatActivity {

    private DatabaseReference userReference;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); //settting back arrow on activity
        userReference= FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth=FirebaseAuth.getInstance();

        if(!NetworkCheck.isNetworkAvailable(OptionsActivity.this)){
            Toast.makeText(OptionsActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
            finishAffinity();
            finish();
        }
//        checkUpdateOfApp();
        addDifferentFragment(PlayOptionFragment.newInstance(),"playOptionFragment");
//
//        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
//                    User user=itemSnapshot.getValue(User.class);
//                    user.setOnline(false);
//                    user.setGame(null);
//                    user.setRequest(null);
//                    userReference.child(itemSnapshot.getKey()).setValue(user);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        startService(new Intent(OptionsActivity.this, MyService.class));
    }

    private void addDifferentFragment(Fragment fragment,String tag) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment,tag).commit();
    }

    public void setActionBarTitle(String title){
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    private void exitAlertDialog() {

        new AlertDialog.Builder(this)
                .setMessage("Do you really want to exit?")
                .setTitle("Exit")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager=getSupportFragmentManager();
        if(fragmentManager.findFragmentByTag("defaultBingoFragment") instanceof ChooseDefaultBingoMatrixFragment){
            addDifferentFragment(GameFragment.newInstance(null),"gameFragment");
        }else if(fragmentManager.findFragmentByTag("gameFragment") instanceof GameFragment){
            leaveGameAlertDialog();
        }else if(fragmentManager.findFragmentByTag("settingFragment") instanceof SettingFragment ||
                fragmentManager.findFragmentByTag("howToPlayFragment") instanceof HowToPlayFragment) {
            addDifferentFragment(PlayOptionFragment.newInstance(), "playOptionFragment");
        }else{
            //if there is a request object
            userReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("request").removeValue();
            exitAlertDialog();
            makeUserOfflineOrOnline(false);
        }
    }

    private void makeUserOfflineOrOnline(final Boolean isOnline) {
//        showKProgress();
            if(firebaseAuth.getCurrentUser()!=null) {
                userReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                            if(Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("online")){
                                userReference.child(firebaseAuth.getCurrentUser().getUid()).child("online").setValue(isOnline);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
    }

    private void leaveGameAlertDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Game Quit")
                .setMessage("Do you really want to quit this game?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addDifferentFragment(PlayOptionFragment.newInstance(),"playOptionFragment");
                        //Also delete user game object,chat object and request object
                        deleteGameRequestAndChatObject();
                    }
                }).show();
    }
    private void deleteGameRequestAndChatObject(){
        userReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("game").removeValue();
        userReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("request").removeValue();
        userReference.child(firebaseAuth.getCurrentUser().getUid()).child("chat").removeValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeUserOfflineOrOnline(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        makeUserOfflineOrOnline(false);
    }
}
