package arvindandroid.com.arvind.bingoonlinegame.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import arvindandroid.com.arvind.bingoonlinegame.Activities.OptionsActivity;
import arvindandroid.com.arvind.bingoonlinegame.Common;
import arvindandroid.com.arvind.bingoonlinegame.Models.Request;
import arvindandroid.com.arvind.bingoonlinegame.Models.User;
import arvindandroid.com.arvind.bingoonlinegame.R;
import arvindandroid.com.arvind.bingoonlinegame.Utils.DialogUtils;
import arvindandroid.com.arvind.bingoonlinegame.Utils.ProgressDialogUtils;
import arvindandroid.com.arvind.bingoonlinegame.ViewHolders.OnlinePlayerViewHolder;


public class PlayOptionFragment extends Fragment implements View.OnClickListener {

    private CardView playOnlineCardView,rateUsCardView,howToPlayCardView,inviteFriendCardView;
    private Context context;
    private RecyclerView onlinePlayersRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private DatabaseReference usersReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseRecyclerAdapter<User,OnlinePlayerViewHolder> onlinePlayerRecyclerAdapter;
    private KProgressHUD kProgressHUD;
    private AlertDialog onlinePlayerAlertDialog;
    private ImageButton settingImageButton;
    private ImageButton volumeImageButton;
    private SharedPreferences sharedPreferences;
    private ProgressBar onlinePlayerProgressBar;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.play_options_fragment,container,false);
        context=getActivity();
        usersReference= FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        checkAnyRequests(); //Check the request continuously if any
        playOnlineCardView=view.findViewById(R.id.playOnlineCardView);
        rateUsCardView=view.findViewById(R.id.rateUsCardView);
        howToPlayCardView=view.findViewById(R.id.howToPlayCardView);
        inviteFriendCardView=view.findViewById(R.id.inviteFriendCardView);

        settingImageButton=view.findViewById(R.id.settingImageButton);
        volumeImageButton=view.findViewById(R.id.volumeImageButton);
        settingImageButton.setOnClickListener(this);
        volumeImageButton.setOnClickListener(this);
        playOnlineCardView.setOnClickListener(this);
        rateUsCardView.setOnClickListener(this);
        inviteFriendCardView.setOnClickListener(this);
        howToPlayCardView.setOnClickListener(this);

        //Saving two int value in shared preference. 1 for on and 2 for off.
        sharedPreferences=context.getSharedPreferences("arvindandroid.com.arvind.bingoonlinegame",Context.MODE_PRIVATE);
        int volumeValue=sharedPreferences.getInt("volumeValue",0);
        if(volumeValue==0){
            //it is a default value. So i need to set volumeValue to 1 which means on
            sharedPreferences.edit().putInt("volumeValue",1).apply();
            volumeImageButton.setImageDrawable(getResources().getDrawable(R.drawable.volume_on_image)); //by default setting volume on image
        }else if(volumeValue==1){
            volumeImageButton.setImageDrawable(getResources().getDrawable(R.drawable.volume_on_image));
        }else{
            volumeImageButton.setImageDrawable(getResources().getDrawable(R.drawable.value_off_image));
        }

        return view;
    }
    @Override
    public void onClick(View v) {

        if(sharedPreferences.getInt("volumeValue",0)==1){
            startButtonSound();
        }
        switch (v.getId())
        {
            case R.id.playOnlineCardView:
                createOnlinePlayerDialog();
                break;

            case R.id.rateUsCardView:
//                addDifferentFragment(GameFragment.newInstance(null),"gameFragment");
                rateMeFunction();
                break;

            case R.id.settingImageButton:
                addDifferentFragment(SettingFragment.newInstance(),"settingFragment");
                break;
            case R.id.volumeImageButton:
                setVolume();
                break;
            case R.id.howToPlayCardView:
                addDifferentFragment(HowToPlayFragment.newInstance(),"howToPlayFragment");
                break;
            case R.id.inviteFriendCardView:
                shareAppWithFriends();
                break;

        }
    }

    private void startButtonSound() {
        mediaPlayer=MediaPlayer.create(context,R.raw.button);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
            }
        });
    }

    private void setVolume() {
        int value=sharedPreferences.getInt("volumeValue",0);
        if(value==1){
            //it means volume is on and user want to off the volume.
            sharedPreferences.edit().putInt("volumeValue",2).apply();
            volumeImageButton.setImageDrawable(getResources().getDrawable(R.drawable.value_off_image));
            //make the volume off
        }else if(value==2){
            sharedPreferences.edit().putInt("volumeValue",1).apply();
            volumeImageButton.setImageDrawable(getResources().getDrawable(R.drawable.volume_on_image));
            //make the volume on
        }
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

    private void addDifferentFragment(Fragment fragment,String tag) {
        FragmentManager fragmentManager=getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment,tag).commit();
        }
    }


    private void createOnlinePlayerDialog() {
        onlinePlayerAlertDialog=new AlertDialog.Builder(context).create();
        onlinePlayerAlertDialog.setMessage("Choose your Opponent");

        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.online_players_layout,null,false);
        onlinePlayersRecyclerView=view.findViewById(R.id.onlinePlayerRecyclerView);

        int noOfColumns=countNoOfColumns(context);
        gridLayoutManager=new GridLayoutManager(context,noOfColumns);
//        onlinePlayersRecyclerView.setHasFixedSize(true);
        onlinePlayersRecyclerView.setLayoutManager(gridLayoutManager);
        onlinePlayerProgressBar=view.findViewById(R.id.onlinePlayerProgressBar);
        showAllOnlinePlayers();

        onlinePlayerAlertDialog.setView(view);
        checkNewUsers();
        onlinePlayerAlertDialog.show();
    }

    private void showAllOnlinePlayers() {
//        final boolean[] isAnyOnline = {false};
        //Using Firebase Recycler Adapter
        onlinePlayerRecyclerAdapter=new FirebaseRecyclerAdapter<User, OnlinePlayerViewHolder>(User.class,
                R.layout.online_player_raw_layout,OnlinePlayerViewHolder.class,usersReference) {
            @Override
            protected void populateViewHolder(OnlinePlayerViewHolder viewHolder, User model, int position) {
                Log.i("playeruId",onlinePlayerRecyclerAdapter.getRef(position).getKey());
                boolean isMe=firebaseAuth.getCurrentUser().getUid().equalsIgnoreCase(onlinePlayerRecyclerAdapter.getRef(position).getKey());
                Log.i("online ",model.isOnline() +" "+isMe);
                if(model.isOnline()&& !isMe){
//                    isAnyOnline[0] =true;
                    if(onlinePlayerProgressBar!=null){
                        onlinePlayerProgressBar.setVisibility(View.GONE);
                    }
                    setOnlinePlayerData(viewHolder,model,onlinePlayerRecyclerAdapter.getRef(position).getKey());
                }
                else{
                    viewHolder.playerImageView.setVisibility(View.GONE);
                    viewHolder.dotImageView.setVisibility(View.GONE);
                    viewHolder.playerNameTextView.setVisibility(View.GONE);
                }
            }
        };
        onlinePlayersRecyclerView.setAdapter(onlinePlayerRecyclerAdapter);
//        if(!isAnyOnline[0]){
//            Toast.makeText(context,"There are no online Player at this time",Toast.LENGTH_LONG).show();
//        }
    }

    private void setOnlinePlayerData(OnlinePlayerViewHolder viewHolder, final User user, final String playerId) {
        Log.i("Playerid",playerId);
        Picasso.with(context).load(user.getImageUrl()).into(viewHolder.playerImageView);
        viewHolder.playerNameTextView.setText(user.getUsername());
        viewHolder.playerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onlinePlayerAlertDialog.isShowing()) {
                    onlinePlayerAlertDialog.dismiss();
                }
                Common.opponentPlayerUid=playerId;
                Common.opponentUserName=user.getUsername();
                Common.opponentImageUrl=user.getImageUrl();
                //Common.opponentUser=user; //saving the opponent user data so that later on i can access it.
                onPlayerClick(playerId,user.getUsername());

            }
        });
    }

    private void onPlayerClick(final String playerUid, final String playerName){
        ProgressDialogUtils.showLoadingDialog(context,"Request Sending...");
        //if game node is already there it means that the player is already playing the game.
        usersReference.child(playerUid).child("game").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Toast.makeText(context,playerName+" is playing with another player. Please Wait!",
                            Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.cancelLoading();
                }else{
                    final Request request=new Request();
                    request.setFromName(firebaseAuth.getCurrentUser().getDisplayName());
                    request.setFrom(firebaseAuth.getCurrentUser().getUid());
                    usersReference.child(playerUid).child("request").setValue(request);


                    //Storing request object also in current user object so that we know that he has send request
                    Request myRequest = new Request();
                    myRequest.setTo(playerUid);
                    myRequest.setToName(playerName);
                    myRequest.setRequestAccept("true");
                    usersReference.child(firebaseAuth.getCurrentUser().getUid()).child("request").setValue(myRequest);
                    checkBothPlayerRequestContinuous(playerUid);
//                    timeCount();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

//    private  void timeCount(){
//
//        new CountDownTimer(20000,1){
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                ProgressDialogUtils.cancelLoading();
//                if(getFragmentManager().findFragmentByTag("playOptionFragment") instanceof PlayOptionFragment) {
//                    Toast.makeText(context, "it seems like player is not online", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }.start();
//    }



    //This below function will continuously check if there is any request for current user.
    private void checkAnyRequests(){
        usersReference.child(firebaseAuth.getCurrentUser().getUid()).child("request").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    if(Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase("from")){
                        Common.opponentPlayerUid=dataSnapshot.getValue(String.class);
                        saveOpponentNameAndImageUrl(dataSnapshot.getValue(String.class));
                    }else if(dataSnapshot.getKey().equalsIgnoreCase("fromName")){
                        String fromName=dataSnapshot.getValue(String.class);
                        Log.i("request send name ",fromName);
                        showRequestAlertDialog(fromName);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkNewUsers(){
        usersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(onlinePlayerRecyclerAdapter!=null)
                    onlinePlayerRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveOpponentNameAndImageUrl(String opponentPlayerUid) {
        usersReference.child(opponentPlayerUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                    if(itemSnapshot.getKey().equalsIgnoreCase("username")){
                        Common.opponentUserName=itemSnapshot.getValue(String.class);
                    }else if(itemSnapshot.getKey().equalsIgnoreCase("imageUrl")){
                        Common.opponentImageUrl=itemSnapshot.getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRequestAlertDialog(String sendRequestPlayerName) {
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setMessage("Do You want to accept "+sendRequestPlayerName+" Request?");
        alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                showKProgress("Please Wait...","Getting you in");
                //If player will accept the request then we need to update Request object.
                manipulatingAcceptRequest(true);
            }
        });
        alertDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //If player will deny the request then we need to update Request object.
                manipulatingAcceptRequest(false);
            }
        });
        alertDialog.show();
    }

    private void showKProgress(String message,String detail) {
        kProgressHUD=KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(message)
                .setDetailsLabel(detail)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    //This below method will update the acceptRequest field of Request object.
    private void manipulatingAcceptRequest(final boolean isAccept){
        usersReference.child(firebaseAuth.getCurrentUser().getUid()).child("request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        if (isAccept){
                            usersReference.child(firebaseAuth.getCurrentUser().getUid()).child("request").child("requestAccept").
                                    setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        kProgressHUD.dismiss();
                                        goForPlay();
                                    }
                                }
                            });
                        }else{
                            usersReference.child(firebaseAuth.getCurrentUser().getUid()).child("request").child("requestAccept").setValue("false");
                        }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //When request is denied by the user then i am not waiting for completion of
        // setting the request object into the firebase.
//        if(dialogInterface!=null && !isAccept)
//            dialogInterface.dismiss();
    }

    private void checkBothPlayerRequestContinuous(final String opponentPlayerUid) {
        usersReference.child(opponentPlayerUid).child("request").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase("requestAccept")) {
                        String requestAccept = dataSnapshot.getValue(String.class);
//                        String requestAccept=dataSnapshot.child("requestAccept").getValue(String.class);
                        Log.i("requestAccept ", " " + requestAccept);
                        if (requestAccept.equalsIgnoreCase("true")) {
                            goForPlay(); //if both player accepted request
                        } else {
                            //Discard
                            Toast.makeText(context,Common.opponentUserName
                                    +" discard your request. Go for another opponent",Toast.LENGTH_LONG).show();
                            usersReference.child(opponentPlayerUid).child("request").removeValue(); //removing the request
                            // from opponent node if he discard the request.
                        }
                        ProgressDialogUtils.cancelLoading();
                    }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goForPlay() {
        Log.i("start","playing the game");
        Toast.makeText(context,"Start playing game",Toast.LENGTH_LONG).show();
        if(onlinePlayerAlertDialog.isShowing()) {
            onlinePlayerAlertDialog.dismiss();
        }
        if (getFragmentManager() != null) {
//            getFragmentManager().beginTransaction().replace(R.id.frameLayout,GameFragment.newInstance(null)).commit();
            addDifferentFragment(GameFragment.newInstance(null),"gameFragment");
        }
    }

    private int countNoOfColumns(Context context) {
        DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
        float dpWidth=displayMetrics.widthPixels/displayMetrics.density;
        return (int)(dpWidth/ 90);
    }

    public static PlayOptionFragment newInstance() {
        Bundle args = new Bundle();
        PlayOptionFragment fragment = new PlayOptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((OptionsActivity)context).setActionBarTitle("Bingo Online");
    }
}
