package arvindandroid.com.arvind.bingoonlinegame.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
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

import java.util.ArrayList;
import java.util.Objects;

import arvindandroid.com.arvind.bingoonlinegame.Activities.OptionsActivity;
import arvindandroid.com.arvind.bingoonlinegame.Common;
import arvindandroid.com.arvind.bingoonlinegame.Models.Game;
import arvindandroid.com.arvind.bingoonlinegame.Models.Message;
import arvindandroid.com.arvind.bingoonlinegame.Models.User;
import arvindandroid.com.arvind.bingoonlinegame.R;
public class GameFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;
    private ImageView playerImageView,opponentPlayerImageview;
    private TextView playerGamePointTextView;
    private TextView opponentPlayerGamePointTextView;
    private TextView playerNameTextView;
    private TextView opponentPlayerNameTextView;
    private ProgressBar playerProgressBar;
    private ProgressBar opponentPlayerProgressBar;
    private TextView bingoTextView;
    private TextView whooseTurnTextView;
    private ImageButton volumeImageButton;
    private ImageButton chooseDefaultBingoImageButton;
    private LinearLayout chatLinearLayout;
    private Button startButton;
    private Context context;
    private Button bt1,bt2,bt3,bt4,bt5,bt6,bt7,bt8,bt9,bt10,bt11,
            bt12,bt13,bt14,bt15,bt16,bt17,bt18,bt19,bt20,bt21,bt22,bt23,bt24,bt25;
    private int[][] bingoArray;
    private boolean isGameOn=false;
    private int countNumber=1; //bingo numbers like as (1,2,3,4...)
    private int totalBingo=0;
    private KProgressHUD kProgressHUD;
    private String gamemessage;
    private boolean isMyChance;
    private String opponentPlayerUid;
    private int opponentChoosenNumber=0;

    private TextView wonOrLossTextView;
    private Button noButton;
    private Button yesButton;
    private int[][] defaultBingoArray;
    private ArrayList<Message> chatMessageArrayList;
    private int CHAT_FRAGMENT_REQUEST_CODE=1;
    private int chatCount=0;
    private ImageView chatCountImageView;
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer,bingoMediaPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.game_fragment_layout,container,false);
//        context=getContext();
        playerImageView=view.findViewById(R.id.gamePlayerImageView);
        opponentPlayerImageview=view.findViewById(R.id.gameOpponentImageview);
        playerNameTextView=view.findViewById(R.id.gamePlayerNameTextView);
        opponentPlayerNameTextView=view.findViewById(R.id.gameOpponentNameTextView);
        playerGamePointTextView=view.findViewById(R.id.playerGamePointTextView);
        opponentPlayerGamePointTextView=view.findViewById(R.id.opponentPlayerGamePointTextView);
        playerProgressBar=view.findViewById(R.id.playerProgressBar);
        opponentPlayerProgressBar=view.findViewById(R.id.opponentProgressBar);

        //Below code is For setting the progress height but not working yet now
        playerProgressBar.getLayoutParams().height=60;
        opponentPlayerProgressBar.getLayoutParams().height=60;
        playerProgressBar.invalidate();
        opponentPlayerProgressBar.invalidate();

        chatCountImageView=view.findViewById(R.id.chatCountImageView);
        whooseTurnTextView=view.findViewById(R.id.whooseTurnTextView);
        volumeImageButton=view.findViewById(R.id.volumeImageButton);
        chooseDefaultBingoImageButton=view.findViewById(R.id.chooseDefaultBingoImageButton);
        chatLinearLayout=view.findViewById(R.id.chatLinearLayout);
        startButton=view.findViewById(R.id.startButton);
        bingoTextView=view.findViewById(R.id.bingoTextView);
        userReference= FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        bingoArray=new int[5][5];
        defaultBingoArray=new int[5][5];
        chatMessageArrayList=new ArrayList<>();

        sharedPreferences=context.getSharedPreferences("arvindandroid.com.arvind.bingoonlinegame",Context.MODE_PRIVATE);

        setInitialVolumeImage();
        startCheckingMessages();//This method will continuously check the message if any come
        initialiseAllBingoButton(view);
        setPlayersPhotosAndName();
        settingClickListenerToAllWidget();
//        checkOpponentLeaveGame(); When i will go from right path then i will uncomment it
        Bundle bundle=this.getArguments();
        if(bundle!=null){
            defaultBingoArray=(int[][])bundle.getSerializable("defaultBingoArray");
            if(defaultBingoArray!=null) {
                setBingoGridByDefaultBingoArray();
            }
        }

        return view;
    }

    private void setInitialVolumeImage() {
        int volumeValue=sharedPreferences.getInt("volumeValue",0);
        if(volumeValue==1){
            volumeImageButton.setImageDrawable(getResources().getDrawable(R.drawable.volume_on_image));
        }else if (volumeValue==2){
            volumeImageButton.setImageDrawable(getResources().getDrawable(R.drawable.value_off_image));
        }
    }


    private void setBingoGridByDefaultBingoArray() {
        bt1.setText(String.valueOf(defaultBingoArray[0][0]));
        bt2.setText(String.valueOf(defaultBingoArray[0][1]));
        bt3.setText(String.valueOf(defaultBingoArray[0][2]));
        bt4.setText(String.valueOf(defaultBingoArray[0][3]));
        bt5.setText(String.valueOf(defaultBingoArray[0][4]));
        bt6.setText(String.valueOf(defaultBingoArray[1][0]));
        bt7.setText(String.valueOf(defaultBingoArray[1][1]));
        bt8.setText(String.valueOf(defaultBingoArray[1][2]));
        bt9.setText(String.valueOf(defaultBingoArray[1][3]));
        bt10.setText(String.valueOf(defaultBingoArray[1][4]));
        bt11.setText(String.valueOf(defaultBingoArray[2][0]));
        bt12.setText(String.valueOf(defaultBingoArray[2][1]));
        bt13.setText(String.valueOf(defaultBingoArray[2][2]));
        bt14.setText(String.valueOf(defaultBingoArray[2][3]));
        bt15.setText(String.valueOf(defaultBingoArray[2][4]));
        bt16.setText(String.valueOf(defaultBingoArray[3][0]));
        bt17.setText(String.valueOf(defaultBingoArray[3][1]));
        bt18.setText(String.valueOf(defaultBingoArray[3][2]));
        bt19.setText(String.valueOf(defaultBingoArray[3][3]));
        bt20.setText(String.valueOf(defaultBingoArray[3][4]));
        bt21.setText(String.valueOf(defaultBingoArray[4][0]));
        bt22.setText(String.valueOf(defaultBingoArray[4][1]));
        bt23.setText(String.valueOf(defaultBingoArray[4][2]));
        bt24.setText(String.valueOf(defaultBingoArray[4][3]));
        bt25.setText(String.valueOf(defaultBingoArray[4][4]));
        for(int i=0;i<defaultBingoArray.length;i++) {
            bingoArray[i]=defaultBingoArray[i].clone();
        }
//        for(int i=0;i<5;i++){
//            for(int j=0;j<5;j++)
//                Log.i("num",String.valueOf(bingoArray[i][j]));
//        }
        countNumber=26; //so that start button will work.

    }


    private void initialiseAllBingoButton(View view) {
        bt1=view.findViewById(R.id.bt1);
        bt2=view.findViewById(R.id.bt2);
        bt3=view.findViewById(R.id.bt3);
        bt4=view.findViewById(R.id.bt4);
        bt5=view.findViewById(R.id.bt5);
        bt6=view.findViewById(R.id.bt6);
        bt7=view.findViewById(R.id.bt7);
        bt8=view.findViewById(R.id.bt8);
        bt9=view.findViewById(R.id.bt9);
        bt10=view.findViewById(R.id.bt10);
        bt11=view.findViewById(R.id.bt11);
        bt12=view.findViewById(R.id.bt12);
        bt13=view.findViewById(R.id.bt13);
        bt14=view.findViewById(R.id.bt14);
        bt15=view.findViewById(R.id.bt15);
        bt16=view.findViewById(R.id.bt16);
        bt17=view.findViewById(R.id.bt17);
        bt18=view.findViewById(R.id.bt18);
        bt19=view.findViewById(R.id.bt19);
        bt20=view.findViewById(R.id.bt20);
        bt21=view.findViewById(R.id.bt21);
        bt22=view.findViewById(R.id.bt22);
        bt23=view.findViewById(R.id.bt23);
        bt24=view.findViewById(R.id.bt24);
        bt25=view.findViewById(R.id.bt25);

    }

    private void settingClickListenerToAllWidget() {
        volumeImageButton.setOnClickListener(this);
        chooseDefaultBingoImageButton.setOnClickListener(this);
        chatLinearLayout.setOnClickListener(this);
        startButton.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        bt7.setOnClickListener(this);
        bt8.setOnClickListener(this);
        bt9.setOnClickListener(this);
        bt10.setOnClickListener(this);
        bt11.setOnClickListener(this);
        bt12.setOnClickListener(this);
        bt13.setOnClickListener(this);
        bt14.setOnClickListener(this);
        bt15.setOnClickListener(this);
        bt16.setOnClickListener(this);
        bt17.setOnClickListener(this);
        bt18.setOnClickListener(this);
        bt19.setOnClickListener(this);
        bt20.setOnClickListener(this);
        bt21.setOnClickListener(this);
        bt22.setOnClickListener(this);
        bt23.setOnClickListener(this);
        bt24.setOnClickListener(this);
        bt25.setOnClickListener(this);
        Log.i("clicked ","All click listener");
    }

    private void setPlayersPhotosAndName() {
        if (User.getCurrentUser() != null) {
            Picasso.with(context).load(User.getCurrentUser().getImageUrl()).into(playerImageView);
            playerNameTextView.setText(User.getCurrentUser().getUsername());
        }
        userReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                    if(Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("toName")){
                        opponentPlayerNameTextView.setText(itemSnapshot.getValue(String.class));
                    }else if(itemSnapshot.getKey().equalsIgnoreCase("to")){
                        opponentPlayerUid=itemSnapshot.getValue(String.class);
                        Common.opponentPlayerUid=opponentPlayerUid;
                        checkOpponentLeaveGame();
                        userReference.child(opponentPlayerUid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot itemSnapshot2:dataSnapshot.getChildren()){
                                    if(Objects.requireNonNull(itemSnapshot2.getKey()).equalsIgnoreCase("imageUrl")){
                                        Picasso.with(context).load(itemSnapshot2.getValue(String.class)).into(opponentPlayerImageview);
                                        break;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        setPlayersGamePoint();
                    }else if(itemSnapshot.getKey().equalsIgnoreCase("fromName")){
                        opponentPlayerNameTextView.setText(itemSnapshot.getValue(String.class));
                    }else if(itemSnapshot.getKey().equalsIgnoreCase("from")){
                        opponentPlayerUid=itemSnapshot.getValue(String.class);
                        Common.opponentPlayerUid=opponentPlayerUid;
                        checkOpponentLeaveGame();
                        userReference.child(opponentPlayerUid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot itemSnapshot2:dataSnapshot.getChildren()){
                                    if(Objects.requireNonNull(itemSnapshot2.getKey()).equalsIgnoreCase("imageUrl")){
                                        Picasso.with(context).load(itemSnapshot2.getValue(String.class)).into(opponentPlayerImageview);
                                        break;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        setPlayersGamePoint();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setPlayersGamePoint() {
        Log.i("setting","The player game point");
        userReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int flag=0;
                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                    if(Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("game")){
                        flag=1;
                        Game game=itemSnapshot.getValue(Game.class);
                        if(game!=null) {
                            playerGamePointTextView.setText(String.valueOf(game.getNoOfGameIWin()));
                            playerProgressBar.setProgress((game.getNoOfGameIWin()*100)/game.getTotalGame());
                            Log.i("player progress bar","player progress bar"+game.getTotalGame());
                        }
                    }
                }
                if(flag==0){
                    playerGamePointTextView.setText("0");
                    playerProgressBar.setProgress(100);
                    Log.i("player progress bar","player progress bar");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userReference.child(opponentPlayerUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int flag=0;
                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                    if(Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("game")){
                        flag=1;
                        Game game=itemSnapshot.getValue(Game.class);
                        if(game!=null) {
                            opponentPlayerGamePointTextView.setText(String.valueOf(game.noOfGameIWin));
                            opponentPlayerProgressBar.setProgress((game.getNoOfGameIWin()*100)/game.getTotalGame());
                            Log.i("opponent progress bar","player progress bar"+game.getTotalGame());
                        }
                    }
                }
                if(flag==0){
                    opponentPlayerGamePointTextView.setText("0");
                    opponentPlayerProgressBar.setProgress(100);
                    Log.i("opponent progress bar","player progress bar");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkOpponentLeaveGame() {
        userReference.child(Common.opponentPlayerUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase("game")){
                    showOpponentLeaveAlertDialog();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showOpponentLeaveAlertDialog() {
        new AlertDialog.Builder(context)
                .setTitle("Game Quit")
                .setMessage("Your opponent has quit the game.\nDo you also want to quit this game?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addDifferentFragment(PlayOptionFragment.newInstance(),"playOptionFragment");
                        //delete user game object,chat object and request object
                        deleteGameRequestAndChatObject();
                    }
                }).show();
    }

    private void deleteGameRequestAndChatObject(){
        userReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("game").removeValue();
        userReference.child(firebaseAuth.getCurrentUser().getUid()).child("request").removeValue();
        userReference.child(firebaseAuth.getCurrentUser().getUid()).child("chat").removeValue();
    }

    private void startCheckingMessages() {
           userReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("chat").addChildEventListener(new ChildEventListener() {
               @Override
               public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                   chatMessageArrayList.add(dataSnapshot.getValue(Message.class));
                   chatCount+=1;

                   //This below method is used to check whether used has opened dialog fragment or not
                   if(getFragmentManager()!=null){
                       Fragment prev=getFragmentManager().findFragmentByTag("dialog");
                       if(prev==null){
                           setChatCountTextDrawable(chatCount);
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

    private void setChatCountTextDrawable(int chatCount) {
        TextDrawable textDrawable=TextDrawable.builder()
                .beginConfig()
                .textColor(getResources().getColor(R.color.md_white_1000))
                .endConfig()
                .buildRound(String.valueOf(chatCount),getResources().getColor(R.color.md_green_600));
        chatCountImageView.setImageDrawable(textDrawable);
    }

    //This below listener is not working becuase of another listener on same node in dialog fragment
//    private void checkOpponentSeen() {
//
//        //I am also checking the seen of message from this game fragment
//        if(Common.opponentPlayerUid!=null) {
//            userReference.child(Common.opponentPlayerUid).child("chat").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                    //As i am removing the message object from firebase if user open the dialog fragment. if child is removed
//                    //then this method will be called.
//                    Message message=dataSnapshot.getValue(Message.class);
//                    Log.i("message on firebase",message.getMessage() +" "+message.isSeen() + " "+message.isMine());
//
//                    //While adding object into firebase i was making setMine false(setMin true for me while filling in
//                    // arraylist) so they will be equal only if i will set its value true.
//                    message.setMine(true);
//                    if(chatMessageArrayList.contains(message)){
//                        int position=chatMessageArrayList.indexOf(message);
//                        message.setSeen(true);
//                        Log.i("Position",String.valueOf(position));
//                        chatMessageArrayList.set(position,message); //setting manipulated message
//                        Log.i("mes pagla",chatMessageArrayList.get(position).getMessage()+" "+
//                                chatMessageArrayList.get(position).isSeen()+" "+chatMessageArrayList.get(position).isMine());
////                        chatMessageArrayList.remove(position);
////                        chatMessageArrayList.add(position,message);
//                    }
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.volumeImageButton:
                startButtonSound();
                setVolume();
                break;
            case R.id.chooseDefaultBingoImageButton:
                startButtonSound();
                if(!isGameOn) {
                    addDifferentFragment(ChooseDefaultBingoMatrixFragment.newInstance(),"defaultBingoFragment");
                }else{
                    Toast.makeText(context,"You can select it only at the beginning of the game",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.chatLinearLayout:
                startButtonSound();
                FragmentTransaction fragmentTransaction= null;
                Fragment prev = null;
                if (getFragmentManager() != null) {
                    fragmentTransaction = getFragmentManager().beginTransaction();
                    prev = getFragmentManager().findFragmentByTag("dialog");
                }
                if (prev != null) {
                    fragmentTransaction.remove(prev);
                }
                if (fragmentTransaction != null) {
                    fragmentTransaction.addToBackStack(null);
                    ChatDialogFragment chatDialogFragment=ChatDialogFragment.newInstance(chatMessageArrayList);
                    chatDialogFragment.setTargetFragment(GameFragment.this,CHAT_FRAGMENT_REQUEST_CODE); //it will set the target fragment
                    // which will be called on pressing back button when dialog is opened.

                    //when user will open chat dialog fragment then i will make chatCount=0
                    chatCount=0;
                    chatCountImageView.setImageResource(0); //removing text drawable from imageview
                    chatDialogFragment.show(fragmentTransaction,"dialog");
                }

                break;
            case R.id.startButton:
                startButtonSound();
                if(countNumber==26) {
                    Log.i("startButton", "Clicked");
                    storeTheGameObjectIntoFirebase();
                    isGameOn = true;
                    startButton.setVisibility(View.INVISIBLE);
                }else{
                    Toast.makeText(context,"Please first fill the bingo matrix",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bt1:
                fillBingoArray(bt1,0,0);
                break;
            case R.id.bt2:
                fillBingoArray(bt2,0,1);
                break;
            case R.id.bt3:
                fillBingoArray(bt3,0,2);
                break;
            case R.id.bt4:
                fillBingoArray(bt4,0,3);
                break;
            case R.id.bt5:
                fillBingoArray(bt5,0,4);
                break;
            case R.id.bt6:
                fillBingoArray(bt6,1,0);
                break;
            case R.id.bt7:
                fillBingoArray(bt7,1,1);
                break;
            case R.id.bt8:
                fillBingoArray(bt8,1,2);
                break;
            case R.id.bt9:
                fillBingoArray(bt9,1,3);
                break;
            case R.id.bt10:
                fillBingoArray(bt10,1,4);
                break;
            case R.id.bt11:
                fillBingoArray(bt11,2,0);
                break;
            case R.id.bt12:
                fillBingoArray(bt12,2,1);
                break;
            case R.id.bt13:
                fillBingoArray(bt13,2,2);
                break;
            case R.id.bt14:
                fillBingoArray(bt14,2,3);
                break;
            case R.id.bt15:
                fillBingoArray(bt15,2,4);
                break;
            case R.id.bt16:
                fillBingoArray(bt16,3,0);
                break;
            case R.id.bt17:
                fillBingoArray(bt17,3,1);
                break;
            case R.id.bt18:
                fillBingoArray(bt18,3,2);
                break;
            case R.id.bt19:
                fillBingoArray(bt19,3,3);
                break;
            case R.id.bt20:
                fillBingoArray(bt20,3,4);
                break;
            case R.id.bt21:
                fillBingoArray(bt21,4,0);
                break;
            case R.id.bt22:
                fillBingoArray(bt22,4,1);
                break;
            case R.id.bt23:
                fillBingoArray(bt23,4,2);
                break;
            case R.id.bt24:
                fillBingoArray(bt24,4,3);
                break;
            case R.id.bt25:
                fillBingoArray(bt25,4,4);
                break;
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

    //This below method is called when you come out from the dialog fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CHAT_FRAGMENT_REQUEST_CODE){
            if(resultCode== Activity.RESULT_OK){
                Bundle bundle=data.getExtras();
                if (bundle != null) {
                    chatMessageArrayList=(ArrayList<Message>)bundle.getSerializable("chatMessageArrayList");
                }
                context=getContext(); //setting context again after coming from dialog fragment.
            }
        }
    }

    private void storeTheGameObjectIntoFirebase() {
        showKProgress();

        userReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int flag=0;
                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                    if(Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("game")){
                        flag=1;
                        Game game=itemSnapshot.getValue(Game.class);
                        int totalGame= 0;
                        if (game != null) {
                            totalGame = game.getTotalGame();
                            totalGame += 1;
                            game.setNoOfBingo(0);
                            game.setChoosenNumber(0);
                            game.setTotalGame(totalGame);
                            game.setWantToPlayAgain(2);
                            boolean mychance = game.isMyChance();
                            if (mychance)
                                game.setMyChance(false);
                            else
                                game.setMyChance(true);
                            userReference.child(firebaseAuth.getCurrentUser().getUid()).child("game").setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                        checkMyChance();
                                    }
                            });
                        }
                    }
                }
                //if it is the first match then there will be no game object and hence this if statement will execute
                if(flag==0){
                    final Game game=new Game();
                    game.setNoOfBingo(0);
                    game.setNoOfGameIWin(0);
                    game.setTotalGame(1);
                    game.setChoosenNumber(0);
                    //First chance is giving to that player who has send the request
                    userReference.child(firebaseAuth.getCurrentUser().getUid()).child("request").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int flag=0;
                            for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                                if(Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("to")){
                                    Log.i("inside","setting game object");
                                    flag=1;
                                    game.setMyChance(true);
                                    game.setWantToPlayAgain(2); //2 means user is ready to play.
                                    userReference.child(firebaseAuth.getCurrentUser().getUid()).child("game").setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                checkMyChance();
                                            }
                                        }
                                    });
                                }
                            }
                            if(flag==0){
                                game.setMyChance(false);
                                game.setWantToPlayAgain(2); //2 means user is ready to play . Don't go onto name
                                userReference.child(firebaseAuth.getCurrentUser().getUid()).child("game").setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            checkMyChance();
                                        }
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addDifferentFragment(Fragment fragment,String tag) {
        FragmentManager fragmentManager=getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
//            if(tag.equalsIgnoreCase("defaultBingoFragment"))
//                fragmentTransaction.addToBackStack(tag);
            fragmentTransaction.replace(R.id.frameLayout,fragment,tag).commit();
        }
    }

    private void checkMyChance() {
        //Below code will check the chance of player.
        userReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).child("game").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //below if condition is used to check if both player are ready or not.
                if(Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase("myChance")){

                    //Checking if other player is also ready or not.
                    userReference.child(Common.opponentPlayerUid).child("game").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(dataSnapshot.exists()) {
                                if (Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase("wantToPlayAgain")){
                                    if(dataSnapshot.getValue(Integer.class) == 2){
                                        if (kProgressHUD.isShowing())
                                            kProgressHUD.dismiss(); //Dismiss the progress bar when both of the
    //                                     player is ready for playing.
                                        checkTheWinOfOpponent();
                                    }else if(dataSnapshot.getValue(Integer.class) == 1){
                                        if(kProgressHUD.isShowing())
                                            kProgressHUD.dismiss();
                                        isGameOn=false;
                                        Toast.makeText(context,"Your Opponent don't want to play",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            //if opponent take much time to start the game then it might be happened that all the element(variable)
                            // of game object has been iterated. So if after some time he start the game then it will change the value of
                            // "wantToPlayAgain" and then this below method will
                            //disable the kprogress.
                            //
                            if(Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase("wantToPlayAgain")){
                                if(dataSnapshot.getValue(Integer.class) == 2){
                                    if(kProgressHUD.isShowing())
                                        kProgressHUD.dismiss();
                                    checkTheWinOfOpponent();
                                }else if(dataSnapshot.getValue(Integer.class) == 1){
                                    if(kProgressHUD.isShowing())
                                        kProgressHUD.dismiss();
                                    isGameOn=false;
                                    Toast.makeText(context,"Your Opponent don't want to play",Toast.LENGTH_LONG).show();
                                }
                            }

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
                    isMyChance=(boolean)dataSnapshot.getValue();
                    if(isMyChance){
                        whooseTurnTextView.setText("Now it's your turn");
                    }else{
                        whooseTurnTextView.setText("Opponent's turn. Wait for some time.");
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(dataSnapshot.getKey().equalsIgnoreCase("myChance")){
//                    isMyChance=(boolean)dataSnapshot.getValue();
//                }
                if(Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase("choosenNumber")){
                    isMyChance=true;
                    opponentChoosenNumber=dataSnapshot.getValue(Integer.class);
                    whooseTurnTextView.setText("Opponent say's "+opponentChoosenNumber+". Please first tick " +
                            "this number and then choose your number");
                }
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

    private void checkTheWinOfOpponent() {
        userReference.child(Common.opponentPlayerUid).child("game").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(Objects.requireNonNull(dataSnapshot.getKey()).equalsIgnoreCase("noOfBingo")){
                    if(dataSnapshot.getValue(Integer.class) == 5){
                        //Show the player that you have lost the match
                        showGameResultAlertDialog(false);
                    }
                }
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

    //This below method is used to fill bingo array and also to mark bingo matrix once game started.
    private void fillBingoArray(Button bt, int x, int y) {
        startBingoButtonSound(); //Button sound
        if(bt.getText().equals("")){
            bt.setText(String.valueOf(countNumber));
            bingoArray[x][y]=countNumber;
            Log.i("countNumber ",String.valueOf(countNumber));
            countNumber++;
        }else{
            Log.i("xy", x +" "+ y);
            if(isGameOn && bingoArray[x][y]!=0){
                //put cross onto that number
                if(opponentChoosenNumber!=0){
                    if(bt.getText().toString().equalsIgnoreCase(String.valueOf(opponentChoosenNumber))){
                        bt.setBackground(getResources().getDrawable(R.drawable.bingo_runtime_round_rectangle));
//                        bt.setBackgroundColor(getResources().getColor(R.color.md_green_500));
                        bingoArray[x][y]=0;
                        checkForAnyBingo(x,y);
                        opponentChoosenNumber=0;
                        Log.i("bingoValue ",String.valueOf(totalBingo));
                        whooseTurnTextView.setText("Now it's your turn");
                    }
                }
                else if(isMyChance){
                    bt.setBackground(getResources().getDrawable(R.drawable.bingo_runtime_round_rectangle));
//                    bt.setBackgroundColor(getResources().getColor(R.color.md_green_500));
                    bingoArray[x][y] = 0;
                    checkForAnyBingo(x, y);
                    Log.i("mychance ",String.valueOf(totalBingo));
                    isMyChance=false;
                    giveChanceToOpponent(Integer.parseInt(bt.getText().toString()));
                }
            }
        }
    }

    private void startBingoButtonSound() {
        if(sharedPreferences.getInt("volumeValue",0)==1) {
            bingoMediaPlayer = MediaPlayer.create(context, R.raw.bingo_button);
            bingoMediaPlayer.start();;
            bingoMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    bingoMediaPlayer.release();
                }
            });
        }
    }

    private void giveChanceToOpponent(final int choosenNumber){
        whooseTurnTextView.setText("Opponent's turn . Wait for some time.");
        userReference.child(opponentPlayerUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                    if(Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("game")){
                        Game game=itemSnapshot.getValue(Game.class);
                        if(game!=null) {
                            game.setChoosenNumber(choosenNumber);
                            Log.i("chance", "giving to other");
                            userReference.child(opponentPlayerUid).child("game").setValue(game);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkForAnyBingo(int x, int y) {
        int j, i;

        //Checking row wise
        for(i=0;i<5;i++){
            if(bingoArray[x][i]!=0)
                break;
        }
        if(i==5) {
            totalBingo += 1;
            setBingoTextView();
        }

        //checking column wise
        for(i=0;i<5;i++){
            if(bingoArray[i][y]!=0)
                break;
        }
        if(i==5) {
            totalBingo += 1;
            setBingoTextView();
        }

        //checking left and right diagonal
        if(x+y==4 && x-y==0)
        {
            //need to check left diagonal and right diagonal also because it is centre element of 2d matrix
            //left Diagonal
            for(i=0,j=0;i<5 && j<5;i++,j++){
                if(bingoArray[i][j]!=0)
                    break;
            }
            if(i==5) {
                totalBingo += 1;
                setBingoTextView();
            }

            //Right Diagonal
            for(i=0,j=4;i<5&&j>=0;i++,j--){
                if(bingoArray[i][j]!=0)
                    break;
            }
            if(i==5) {
                totalBingo += 1;
                setBingoTextView();
            }

        }else if(x+y==4){
            //need to check right diagonal
            for(i=0,j=4;i<5&&j>=0;i++,j--){
                if(bingoArray[i][j]!=0)
                    break;
            }
            if(i==5) {
                totalBingo += 1;
                setBingoTextView();
            }
        }
        else if(x-y==0){
            //need to check left diagonal
            for(i=0,j=0;i<5 && j<5;i++,j++){
                if(bingoArray[i][j]!=0)
                    break;
            }
            if(i==5) {
                totalBingo += 1;
                setBingoTextView();
            }
        }

    }

    private void setBingoTextView(){
        switch (totalBingo){
            case 1:
                bingoTextView.setText("B");
                break;
            case 2:
                bingoTextView.setText("B I");
                break;
            case 3:
                bingoTextView.setText("B I N");
                break;
            case 4:
                bingoTextView.setText("B I N G");
                break;
            case 5:
                bingoTextView.setText("B I N G O");
                //Give a option to restart the match;
                userReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                            if(Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("game")){
                                Game game=itemSnapshot.getValue(Game.class);
                                if(game!=null) {
                                    int num = game.getNoOfGameIWin();
                                    num += 1;
                                    game.setNoOfBingo(5);
                                    game.setNoOfGameIWin(num);
                                    game.setWantToPlayAgain(0);// 0 doesn't mean that player will not play again. it is just reseting the value
                                    userReference.child(firebaseAuth.getCurrentUser().getUid()).child("game").setValue(game);
                                    showGameResultAlertDialog(true);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                break;
        }
    }

    public void showGameResultAlertDialog(boolean result){

        try {
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            View view = LayoutInflater.from(context).inflate(R.layout.game_result_alert_dialog_layout, null, false);
            wonOrLossTextView = view.findViewById(R.id.wonOrLossTextView);
            yesButton = view.findViewById(R.id.yesButton);
            noButton = view.findViewById(R.id.noButton);
            if (result) {
                wonOrLossTextView.setText("You Won This Match");
                wonOrLossTextView.setTextColor(getResources().getColor(R.color.md_green_600));
            } else {
                wonOrLossTextView.setText("You Loss This Match");
                wonOrLossTextView.setTextColor(getResources().getColor(R.color.md_red_500));
            }
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Remove him from the game Fragment
                    alertDialog.dismiss();
                    //setting the value of wanttoplayAgain 1
                    userReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                if (Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("game")) {
                                    Game game = itemSnapshot.getValue(Game.class);
                                    if (game != null) {
                                        game.setWantToPlayAgain(1);
                                        userReference.child(firebaseAuth.getCurrentUser().getUid()).child("game").setValue(game);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    if (getFragmentManager() != null) {
                        Toast.makeText(context, "Restarting The Game", Toast.LENGTH_SHORT).show();
                        addDifferentFragment(GameFragment.newInstance(null), "gameFragment");
                    }
                }
            });
            alertDialog.setView(view);
            alertDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showKProgress() {
        kProgressHUD=KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    public static GameFragment newInstance(int [][] bingoArray) {
        Bundle args = new Bundle();
        if(bingoArray!=null){
            args.putSerializable("defaultBingoArray",bingoArray);
        }
        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((OptionsActivity)context).setActionBarTitle("Bingo Online");
    }
}
