package arvindandroid.com.arvind.bingoonlinegame.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

import arvindandroid.com.arvind.bingoonlinegame.Activities.LoginActivity;
import arvindandroid.com.arvind.bingoonlinegame.Activities.OptionsActivity;
import arvindandroid.com.arvind.bingoonlinegame.R;

public class SettingFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    private Context context;
    private Preference rateMePreference;
    private Preference reportBugPreference;
    private Preference emailMePreference;
    private Preference sendFeedbackPreference;
    private Preference tellYourFriendPreference;
    private Preference logOutPreference;
    private Preference volumeSwitchPreference;
    private Preference privacyPolicyPreference;
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.setting,s);
        context=getContext();

        rateMePreference=findPreference("rateMe");
        reportBugPreference=findPreference("reportBug");
        emailMePreference=findPreference("emailMe");
        sendFeedbackPreference=findPreference("sendFeedback");
        tellYourFriendPreference=findPreference("tellYourFriend");
        logOutPreference=findPreference("logOut");
        volumeSwitchPreference=findPreference("volume");
        privacyPolicyPreference=findPreference("privacyPolicy");
        mediaPlayer=MediaPlayer.create(context,R.raw.button);

        sharedPreferences=context.getSharedPreferences("arvindandroid.com.arvind.bingoonlinegame",Context.MODE_PRIVATE);
        int value=sharedPreferences.getInt("volumeValue",0);
        if(value==1){
            volumeSwitchPreference.setDefaultValue(true);// volume =1 means volume is on
        }else if(value==2){
            volumeSwitchPreference.setDefaultValue(false);// volume =1 means volume is off
        }

        volumeSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object object) {
                boolean isVolumeOn=(Boolean)object;
                if(isVolumeOn){
                    //make the volume on
                    sharedPreferences.edit().putInt("volumeValue",1).apply(); //save it into sharedPreferences
                }else{
                    //make the volume off
                    sharedPreferences.edit().putInt("volumeValue",2).apply();//save it into sharedPreferences
                }
                return true;
            }
        });

        rateMePreference.setOnPreferenceClickListener(this);
        reportBugPreference.setOnPreferenceClickListener(this);
        emailMePreference.setOnPreferenceClickListener(this);
        sendFeedbackPreference.setOnPreferenceClickListener(this);
        tellYourFriendPreference.setOnPreferenceClickListener(this);
        logOutPreference.setOnPreferenceClickListener(this);
        privacyPolicyPreference.setOnPreferenceClickListener(this);

//        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Toast.makeText(context,"You clicked rateMe",Toast.LENGTH_LONG).show();
//                return true;
//            }
//        });
    }

    public static SettingFragment newInstance() {
        
        Bundle args = new Bundle();
        
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setDivider(Drawable divider) {
        super.setDivider(new ColorDrawable(Color.GRAY));
    }

    @Override
    public void setDividerHeight(int height) {
        super.setDividerHeight(1);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        startButtonSound();
        switch (preference.getKey())
        {
            case "rateMe":
                rateMeFunction();
                break;
            case "reportBug":
                sendEmailIntent("Report Bug in your Bingo Online App");
                break;
            case "emailMe":
                sendEmailIntent("Some Suggestion for your Bingo Online App");
                break;
            case "sendFeedback":
                rateMeFunction(); //also sending to play store to give feedback
                break;
            case "tellYourFriend":
                shareAppWithFriends();
                break;
            case "logOut":
                logOutAlertDialog();
                break;
            case "privacyPolicy":
                showPrivacyPolicy();
                break;

        }
        return false;
    }

    private void showPrivacyPolicy() {
        Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse(getResources().getString(R.string.privacy_policy_url)));
        startActivity(browserIntent);
    }

    private void sendEmailIntent(String subject) {
        Intent emailIntent=new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto","negiarvind229@gmail.com",null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject); //subject of mail
        emailIntent.putExtra(Intent.EXTRA_TEXT,""); //Body of mail
        startActivity(Intent.createChooser(emailIntent,"Send mail..."));
    }

    private void shareAppWithFriends() {
        String appUrl="https://play.google.com/store/apps/details?id="+context.getPackageName();
        String shareBody = "One of the best online two player bingo app : "+appUrl;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Bingo Online Game");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent,"Share link!"));
    }

    private void rateMeFunction() {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
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
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    private void startButtonSound() {

        if(sharedPreferences.getInt("volumeValue",0)==1) {
            mediaPlayer=MediaPlayer.create(context,R.raw.button);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                }
            });
        }
    }



    private void logOutAlertDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Log Out")
                .setMessage("Do you really want to log out ?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logOut();
                    }
                }).show();
    }
    private void logOut(){

        for (UserInfo userInfo : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            //Sign out from firebase
            FirebaseAuth.getInstance().signOut();
            //Here i am only signing out from facebook not from google because signout from google is done at the beginning.
            if (userInfo.getProviderId().equals("facebook.com")) {
                Log.d("TAG", "User is signed in with Facebook");
                //Sign out from facebook
                LoginManager.getInstance().logOut();
            }
        }
        Toast.makeText(context,"Log Out Successfully",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(context,LoginActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        ((OptionsActivity)context).setActionBarTitle("Settings");
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        //Add dividers dividers
//        View rootView = getView();
//        ListView list = rootView.findViewById(android.R.id.list);
//        list.setDivider(new ColorDrawable(Color.GRAY));
//        list.setDividerHeight(1);
//    }
}
