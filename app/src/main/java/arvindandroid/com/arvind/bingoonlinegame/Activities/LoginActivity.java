package arvindandroid.com.arvind.bingoonlinegame.Activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import arvindandroid.com.arvind.bingoonlinegame.Common;
import arvindandroid.com.arvind.bingoonlinegame.Models.User;
import arvindandroid.com.arvind.bingoonlinegame.R;
import arvindandroid.com.arvind.bingoonlinegame.Utils.NetworkCheck;
import arvindandroid.com.arvind.bingoonlinegame.Utils.ProgressDialogUtils;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private SignInButton googleSignInButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersReference;
    private FirebaseDatabase firebaseDatabase;

    private int RC_SIGN_IN=9001;


    private GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        usersReference=firebaseDatabase.getReference();
        getSupportActionBar().setTitle("Bingo Online");



        /***************************Google Sign In***************/

        googleSignInButton=findViewById(R.id.googleSignInButton);
        setGooglePlusButtonText(googleSignInButton);
        googleSignInButton.setSize(SignInButton.SIZE_WIDE);

        if(!NetworkCheck.isNetworkAvailable(LoginActivity.this)){
            Toast.makeText(LoginActivity.this,"No internet connection",Toast.LENGTH_LONG).show();
            finishAffinity();
            finish();
        }
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("397105319745-hs5bf4n4nrrbm0023n6470mm9cvcblc0.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialogUtils.showLoadingDialog(LoginActivity.this,"Loading..");
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            signInWithGoogle();
                    }
                });
            }
        });


        /******************Facebook Login*************/

        callbackManager=CallbackManager.Factory.create();
        facebookLoginButton=findViewById(R.id.facebook_login_button);
        facebookLoginButton.setReadPermissions("email","public_profile");
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("Login","Facebook login success");
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i("Login","Facebook login cancel");
                updateUI(null);

            }

            @Override
            public void onError(FacebookException error) {
                Log.i("Login","Facebook login error "+error);
                updateUI(null);
            }
        });
    }

    private void setGooglePlusButtonText(SignInButton signInButton) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("Sign in with Google");
                return;
            }
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("intent data",data.toString());
        Log.i("resultCode",String.valueOf(resultCode));
        Log.i("requestCode", String.valueOf(requestCode));

        if(requestCode==RC_SIGN_IN && resultCode==RESULT_OK){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleTask(task);
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleTask(Task<GoogleSignInAccount> task) {
        Log.i("task",task.toString());
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
            }

        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w("google sign in", "Google sign in failed", e);
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.i("google id token",account.getIdToken());

        ProgressDialogUtils.showLoadingDialog((LoginActivity.this),"Processing..");

        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("google login", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            saveUserDetailIntoFirebaseDatabase(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("google sign in", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {

        Log.i("handle facebook token",String.valueOf(accessToken));
        ProgressDialogUtils.showLoadingDialog(LoginActivity.this,"Processing...");
        AuthCredential authCredential= FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.i("login facebook","sign with credential");
                            FirebaseUser user=firebaseAuth.getCurrentUser();
                            saveUserDetailIntoFirebaseDatabase(user);
                            updateUI(user);
                        }
                        else{
                            Log.i("login facebook","sign in failed "+task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser()!=null){
            Log.i("authe",firebaseAuth.getCurrentUser().getDisplayName());
            makeUserOnline(firebaseAuth.getCurrentUser());
            deleteGameChatAndRequestIfExist();//it will delete the game,chat and request object from user node. if By chance user
            //remove your app from home button then game,chat and request object will not be deleted.So i deleting it here.
            updateUI(firebaseAuth.getCurrentUser());
//            checkUpdateOfApp(); //Want to show update dialog if i will make any update in my app.
        }
    }

    private void deleteGameChatAndRequestIfExist() {
        usersReference.child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("game").removeValue();
        usersReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("request").removeValue();
        usersReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").removeValue();
    }




    private void makeUserOnline(final FirebaseUser currentUser) {
        if(currentUser!=null) {
            usersReference.child("Users").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                        if(Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("online")){
                            usersReference.child("Users").child(currentUser.getUid()).child("online").setValue(true);
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

//    private void signOut(){
//        for (UserInfo userInfo :firebaseAuth.getCurrentUser().getProviderData()) {
//            //Sign out from firebase
//            firebaseAuth.signOut();
//
//            if (userInfo.getProviderId().equals("facebook.com")) {
//                Log.d("TAG", "User is signed in with Facebook");
//                //Sign out from facebook
//                LoginManager.getInstance().logOut();
//            }
//            else{
//                // Google sign out
//                mGoogleSignInClient.signOut().addOnCompleteListener(this,
//                        new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                updateUI(null);
//                            }
//                        });
//            }
//        }
//    }

    private void updateUI(FirebaseUser user) {
        if(user!=null) {
            ProgressDialogUtils.cancelLoading();
            Common.myUid = user.getUid();
            User.setCurrentUser(user.getDisplayName(), user.getPhotoUrl().toString());
            Intent intent = new Intent(LoginActivity.this, OptionsActivity.class);
            startActivity(intent);
        }
    }

    private void saveUserDetailIntoFirebaseDatabase(FirebaseUser firebaseUser) {

        if(firebaseUser!=null) {
            User user = new User();
            user.setUsername(firebaseUser.getDisplayName());
            user.setImageUrl(String.valueOf(firebaseUser.getPhotoUrl()));
            user.setOnline(true);
            usersReference.child("Users").child(firebaseUser.getUid()).setValue(user);
        }
    }

    @Override
    public void onBackPressed() {
        exitAlertDialog();
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
}
