package arvindandroid.com.arvind.bingoonlinegame.Fragments;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.game_fragment_layout,container,false);
        context=getActivity();
        playerImageView=view.findViewById(R.id.gamePlayerImageView);
        opponentPlayerImageview=view.findViewById(R.id.gameOpponentImageview);
        playerNameTextView=view.findViewById(R.id.gamePlayerNameTextView);
        opponentPlayerNameTextView=view.findViewById(R.id.gameOpponentNameTextView);
        playerGamePointTextView=view.findViewById(R.id.playerGamePointTextView);
        opponentPlayerGamePointTextView=view.findViewById(R.id.opponentPlayerGamePointTextView);
        playerProgressBar=view.findViewById(R.id.playerProgressBar);
        opponentPlayerProgressBar=view.findViewById(R.id.opponentProgressBar);
        whooseTurnTextView=view.findViewById(R.id.whooseTurnTextView);
        volumeImageButton=view.findViewById(R.id.volumeImageButton);
        chooseDefaultBingoImageButton=view.findViewById(R.id.chooseDefaultBingoImageButton);
        chatLinearLayout=view.findViewById(R.id.chatLinearLayout);
        startButton=view.findViewById(R.id.startButton);
        bingoTextView=view.findViewById(R.id.bingoTextView);
        userReference= FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        bingoArray=new int[5][5];
        initialiseAllBingoButton(view);
        setPlayersPhotosAndName();
        settingClickListenerToAllWidget();
        return view;
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
        Picasso.with(context).load(User.getCurrentUser().getImageUrl()).into(playerImageView);
        playerNameTextView.setText(User.getCurrentUser().getUsername());
        userReference.child(firebaseAuth.getCurrentUser().getUid()).child("request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                    if(itemSnapshot.getKey().equalsIgnoreCase("toName")){
                        opponentPlayerNameTextView.setText(itemSnapshot.getValue(String.class));
                    }else if(itemSnapshot.getKey().equalsIgnoreCase("to")){
                        userReference.child(itemSnapshot.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot itemSnapshot2:dataSnapshot.getChildren()){
                                    if(itemSnapshot2.getKey().equalsIgnoreCase("imageUrl")){
                                        Picasso.with(context).load(itemSnapshot2.getValue(String.class)).into(opponentPlayerImageview);
                                        break;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else if(itemSnapshot.getKey().equalsIgnoreCase("fromName")){
                        opponentPlayerNameTextView.setText(itemSnapshot.getValue(String.class));
                    }else if(itemSnapshot.getKey().equalsIgnoreCase("from")){
                        userReference.child(itemSnapshot.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot itemSnapshot2:dataSnapshot.getChildren()){
                                    if(itemSnapshot2.getKey().equalsIgnoreCase("imageUrl")){
                                        Picasso.with(context).load(itemSnapshot2.getValue(String.class)).into(opponentPlayerImageview);
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



    public static GameFragment newInstance() {

        Bundle args = new Bundle();

        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.volumeImageButton:
                break;
            case R.id.chooseDefaultBingoImageButton:
                break;
            case R.id.chatLinearLayout:
                break;
            case R.id.startButton:
                Log.i("startButton","Clicked");
                isGameOn=true;
                startButton.setVisibility(View.INVISIBLE);
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

    private void fillBingoArray(Button bt, int x, int y) {
        if(bt.getText().equals("")){
            bt.setText(String.valueOf(countNumber));
            bingoArray[x][y]=countNumber;
            Log.i("countNumber ",String.valueOf(countNumber));
            countNumber++;
        }else{
            Log.i("xy", x +" "+ y);
            if(isGameOn && bingoArray[x][y]!=0){
                //put cross onto that number
                bt.setBackground(getResources().getDrawable(R.drawable.bingo_runtime_round_rectangle));
//                bt.setBackgroundColor(getResources().getColor(R.color.md_green_500));
                bingoArray[x][y]=0;
                checkForAnyBingo(x,y);
                Log.i("bingoValue ",String.valueOf(totalBingo));

            }
        }
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
                break;
        }
    }


}
