package arvindandroid.com.arvind.bingoonlinegame.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import arvindandroid.com.arvind.bingoonlinegame.Models.Request;
import arvindandroid.com.arvind.bingoonlinegame.Models.User;
import arvindandroid.com.arvind.bingoonlinegame.R;
import arvindandroid.com.arvind.bingoonlinegame.Utils.DialogUtils;
import arvindandroid.com.arvind.bingoonlinegame.Utils.ProgressDialogUtils;
import arvindandroid.com.arvind.bingoonlinegame.ViewHolders.OnlinePlayerViewHolder;

public class PlayOptionFragment extends Fragment {

    private CardView playOnlineCardView;
    private Context context;
    private RecyclerView onlinePlayersRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private DatabaseReference usersReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseRecyclerAdapter<User,OnlinePlayerViewHolder> onlinePlayerRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.play_options_fragment,container,false);
        context=getActivity();
        usersReference= FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        checkAnyRequests(); //Check the request continuously if any
        playOnlineCardView=view.findViewById(R.id.playOnlineCardView);
        playOnlineCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOnlinePlayerDialog();
            }
        });
        return view;
    }

    private void createOnlinePlayerDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setMessage("Choose your Opponent");

        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.online_players_layout,null,false);
        onlinePlayersRecyclerView=view.findViewById(R.id.onlinePlayerRecyclerView);

        int noOfColumns=countNoOfColumns(context);
        gridLayoutManager=new GridLayoutManager(context,noOfColumns);
//        onlinePlayersRecyclerView.setHasFixedSize(true);
        onlinePlayersRecyclerView.setLayoutManager(gridLayoutManager);
        showAllOnlinePlayers();

        alertDialog.setView(view);
        alertDialog.show();
    }

    private void showAllOnlinePlayers() {
        //Using Firebase Recycler Adapter
        onlinePlayerRecyclerAdapter=new FirebaseRecyclerAdapter<User, OnlinePlayerViewHolder>(User.class,
                R.layout.online_player_raw_layout,OnlinePlayerViewHolder.class,usersReference) {
            @Override
            protected void populateViewHolder(OnlinePlayerViewHolder viewHolder, User model, int position) {
                Log.i("playeruId",onlinePlayerRecyclerAdapter.getRef(position).getKey());
                boolean isMe=firebaseAuth.getCurrentUser().getUid().equalsIgnoreCase(onlinePlayerRecyclerAdapter.getRef(position).getKey());
                Log.i("online ",model.isOnline() +" "+isMe);
                if(model.isOnline()&& !isMe){
                    showAllOnlinePlayer(viewHolder,model,onlinePlayerRecyclerAdapter.getRef(position).getKey());
                }
                else{
                    viewHolder.playerImageView.setVisibility(View.GONE);
                    viewHolder.dotImageView.setVisibility(View.GONE);
                    viewHolder.playerNameTextView.setVisibility(View.GONE);
                }
            }
        };
        onlinePlayersRecyclerView.setAdapter(onlinePlayerRecyclerAdapter);
    }

    private void showAllOnlinePlayer(OnlinePlayerViewHolder viewHolder, final User user, final String playerId) {
        Log.i("Playerid",playerId);
        Picasso.with(context).load(user.getImageUrl()).into(viewHolder.playerImageView);
        viewHolder.playerNameTextView.setText(user.getUsername());
        viewHolder.playerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                }else{
                    final Request request=new Request();
                    request.setFromName(firebaseAuth.getCurrentUser().getDisplayName());
                    request.setFrom(firebaseAuth.getCurrentUser().getUid());
                    usersReference.child(playerUid).child("request").setValue(request);

                    //Storing request object also in current user object so that we know that he has send request
                    Request myRequest = new Request();
                    myRequest.setTo(playerUid);
                    myRequest.setRequestAccept("true");
                    usersReference.child(firebaseAuth.getCurrentUser().getUid()).child("request").setValue(myRequest);
                    checkBothPlayerRequestContinuous(playerUid);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    //This below function will continuously check if there is any request for current user.
    private void checkAnyRequests(){
        usersReference.child(firebaseAuth.getCurrentUser().getUid()).child("request").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("from").exists()){
                        String fromName=dataSnapshot.child("fromName").getValue(String.class);
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

    private void showRequestAlertDialog(String sendRequestPlayerName) {
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setMessage("Do You want to accept "+sendRequestPlayerName+" Request?");
        alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                DialogUtils.showLoadingDialog(context);
                //If player will accept the request then we need to update Request object.
                manipulatingAcceptRequest(true,dialog);
            }
        });
        alertDialog.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //If player will deny the request then we need to update Request object.
                manipulatingAcceptRequest(false,dialog);
            }
        });
        alertDialog.show();
    }

    //This below method will update the acceptRequest field of Request object.
    private void manipulatingAcceptRequest(final boolean isAccept, final DialogInterface dialogInterface){
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
                                        DialogUtils.cancelLoading();
                                        goForPlay();
                                    }
                                }
                            });
                            dialogInterface.dismiss();
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
        if(dialogInterface!=null && !isAccept)
            dialogInterface.dismiss();
    }

    private void checkBothPlayerRequestContinuous(String opponentPlayerUid) {
        usersReference.child(opponentPlayerUid).child("request").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String requestAccept=dataSnapshot.child("requestAccept").getValue(String.class);
                    Log.i("requestAccept "," "+requestAccept);
                    if(requestAccept!=null) {
                        if (requestAccept.equalsIgnoreCase("true")) {
                            ProgressDialogUtils.cancelLoading();
                            goForPlay(); //if both player accepted request
                        }else{
                            //Discard
                        }
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

    private void goForPlay() {
        Log.i("start","playing the game");
        Toast.makeText(context,"Start playing game",Toast.LENGTH_LONG).show();
    }

    private int countNoOfColumns(Context context) {
        DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
        float dpWidth=displayMetrics.widthPixels/displayMetrics.density;
        int noOfColumns=(int)(dpWidth/ 90);//Here 90 is my grid cell width
        return noOfColumns;
    }

    public static PlayOptionFragment newInstance() {
        Bundle args = new Bundle();
        PlayOptionFragment fragment = new PlayOptionFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
