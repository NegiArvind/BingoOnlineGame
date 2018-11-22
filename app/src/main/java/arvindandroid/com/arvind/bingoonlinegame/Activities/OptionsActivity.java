package arvindandroid.com.arvind.bingoonlinegame.Activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

import arvindandroid.com.arvind.bingoonlinegame.Fragments.PlayOptionFragment;
import arvindandroid.com.arvind.bingoonlinegame.R;

public class OptionsActivity extends AppCompatActivity {

    private Button logOutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

//        logOutButton=findViewById(R.id.logOutButton);
//        logOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signOut();
//            }
//        });
        addDifferentFragment(PlayOptionFragment.newInstance());
    }

    private void addDifferentFragment(Fragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment).commit();
    }

    private void signOut(){
        for (UserInfo userInfo : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            //Sign out from firebase
            FirebaseAuth.getInstance().signOut();

            if (userInfo.getProviderId().equals("facebook.com")) {
                Log.d("TAG", "User is signed in with Facebook");
                //Sign out from facebook
                LoginManager.getInstance().logOut();
            }
            else{
                // Google sign out
                LoginActivity.mGoogleSignInClient.signOut().addOnCompleteListener(this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(OptionsActivity.this,"Log out successfully",Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        });
            }
        }
    }
}
