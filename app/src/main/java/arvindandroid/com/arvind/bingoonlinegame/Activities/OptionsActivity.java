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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import arvindandroid.com.arvind.bingoonlinegame.Fragments.ChooseDefaultBingoMatrixFragment;
import arvindandroid.com.arvind.bingoonlinegame.Fragments.GameFragment;
import arvindandroid.com.arvind.bingoonlinegame.Fragments.HowToPlayFragment;
import arvindandroid.com.arvind.bingoonlinegame.Fragments.PlayOptionFragment;
import arvindandroid.com.arvind.bingoonlinegame.Fragments.SettingFragment;
import arvindandroid.com.arvind.bingoonlinegame.R;

public class OptionsActivity extends AppCompatActivity {

    private DatabaseReference userReference;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //settting back arrow on activity
        userReference= FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        checkUpdateOfApp();
        addDifferentFragment(PlayOptionFragment.newInstance(),"playOptionFragment");
    }
    private void checkUpdateOfApp() {
        FirebaseDatabase.getInstance().getReference("update").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    showUpdateAlertDialog(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle("Update")
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openGooglePlayStore();
                    }
                }).show();
    }

    private void openGooglePlayStore() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    private void addDifferentFragment(Fragment fragment,String tag) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment,tag).commit();
    }

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
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
            userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("request").removeValue();
            makeUserOffline();
        }
    }

    private void makeUserOffline() {
//        showKProgress();
        exitAlertDialog();
        if(firebaseAuth.getCurrentUser()!=null) {
            userReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                        if(itemSnapshot.getKey().equalsIgnoreCase("online")){
                            userReference.child(firebaseAuth.getCurrentUser().getUid()).child("online").setValue(false);
                             //once user is set to offline then we will show alert dialog
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
        userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("game").removeValue();
        userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("request").removeValue();
        userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").removeValue();
    }
}
