package arvindandroid.com.arvind.bingoonlinegame.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import arvindandroid.com.arvind.bingoonlinegame.Common;
import arvindandroid.com.arvind.bingoonlinegame.Models.Message;
import arvindandroid.com.arvind.bingoonlinegame.R;

public class ChatDialogFragment extends DialogFragment {

    private EditText messageEditText;
    private ImageButton pushImageButton;
    private ArrayList<Message> chatMessageArrayList;
    private TextView chatFromTextView;
    private ImageView tickImageView;
    private DatabaseReference userReference;
    private Context context;
    private int widthPixels;
    private int sendMessagePosition=0;
    private ImageView dialogOpponentImageivew;
    private TextView dialogOpponentTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view=inflater.inflate(R.layout.chat_dialog_fragment_layout,container,false);
        context=getContext();
        messageEditText=view.findViewById(R.id.messageEditText);
        pushImageButton=view.findViewById(R.id.messagePushButton);
        dialogOpponentImageivew=view.findViewById(R.id.dialogOpponentImageivew);
        dialogOpponentTextView=view.findViewById(R.id.dialogOpponentTextView);
        userReference= FirebaseDatabase.getInstance().getReference("Users");
        Picasso.with(context).load(Common.opponentImageUrl).into(dialogOpponentImageivew);
        dialogOpponentTextView.setText(Common.opponentUserName);

        //Below will calculate the width of current displaying fragment
        DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
        widthPixels=displayMetrics.widthPixels;

        pushImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageIntoFirebase(view);
            }
        });

        Bundle bundle=getArguments();
        if(bundle!=null){
            chatMessageArrayList=(ArrayList<Message>)bundle.getSerializable("chatMessageArrayList");
        }
        if (chatMessageArrayList != null)
            sendMessagePosition=chatMessageArrayList.size();

        //Once i got the messages i delete all of them
        settingMessagesToLinearLayout(view);
        startCheckingMessages(view);
        checkOpponentSeen();
        return view;
    }


    private void sendMessageIntoFirebase(View rootView) {
        String text=messageEditText.getText().toString().trim();
        if(text.length()>0){
            final Message message=new Message();
            message.setMessage(text);
            message.setSeen(false);
            message.setMine(false); //because this message i am putting on opponent node that's why i set it false.
            //Storing message into opponent node
            showUserSendMessageToHim(rootView,message.getMessage());
            messageEditText.setText("");
            userReference.child(Common.opponentPlayerUid).child("chat").child(UUID.randomUUID().toString()).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        //make tick color to grey
                        message.setMine(true); //setting setMine to true so that i will recognise that this is not mine message
//                        myMessageArrayList.add(message);
                        chatMessageArrayList.add(message);
                    }
                }
            });
        }
        else{
            Toast.makeText(context,"Please First Enter Text",Toast.LENGTH_SHORT).show();
        }
    }

    private void showUserSendMessageToHim(View rootView,String message) {


        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.chat_right_raw_layout, null, false);
        tickImageView = view.findViewById(R.id.tickImageView);
        chatFromTextView = view.findViewById(R.id.chatFromTextView);
        chatFromTextView.setText(message);
        ViewGroup viewGroup = rootView.findViewById(R.id.messagesLinearLayout);
        viewGroup.addView(view,sendMessagePosition, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sendMessagePosition+=1; //This position is important. it will decide where to set which view.
    }

    private void settingMessagesToLinearLayout(View rootView) {

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < chatMessageArrayList.size(); i++) {
            View view;
            if(chatMessageArrayList.get(i).isMine()) {
                view = layoutInflater.inflate(R.layout.chat_right_raw_layout, null, false);
                tickImageView = view.findViewById(R.id.tickImageView);
                chatFromTextView = view.findViewById(R.id.chatFromTextView);
                chatFromTextView.setText(chatMessageArrayList.get(i).getMessage());
                if(chatMessageArrayList.get(i).isSeen()){
                    tickImageView.setImageDrawable(getResources().getDrawable(R.drawable.check_blue_image));
                }
            }else{
                view = layoutInflater.inflate(R.layout.chat_left_raw_layout, null, false);
                chatFromTextView = view.findViewById(R.id.chatGettingTextView);
                chatFromTextView.setText(chatMessageArrayList.get(i).getMessage());
                chatFromTextView.setMaxWidth(widthPixels-32);
            }
            ViewGroup viewGroup = rootView.findViewById(R.id.messagesLinearLayout);
            viewGroup.addView(view, i, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
    public static ChatDialogFragment newInstance (ArrayList<Message> chatMessageArrayList) {

        Bundle args = new Bundle();
        args.putSerializable("chatMessageArrayList",chatMessageArrayList);
        ChatDialogFragment fragment = new ChatDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Intent intent=new Intent();
//        intent.putExtra("myMessageArrayList", myMessageArrayList);
        intent.putExtra("chatMessageArrayList", chatMessageArrayList);
        if (getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
        }
    }

    private void startCheckingMessages(final View rootView) {
        userReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message=dataSnapshot.getValue(Message.class);
                if(message!=null) {
                    //Removing the message object from firebase after add in arraylist.
                    userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").child(dataSnapshot.getKey()).removeValue();
                    if (!chatMessageArrayList.contains(message)) {
//                    myMessageArrayList.add(message);
                        chatMessageArrayList.add(message);
                        showOpponentMessage(rootView, message.getMessage());
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


    private void checkOpponentSeen() {
        if(Common.opponentPlayerUid!=null) {
            userReference.child(Common.opponentPlayerUid).child("chat").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    //As i am removing the message object from firebase if user open the dialog fragment. if child is removed
                    //then this method will be called.
                    Message message=dataSnapshot.getValue(Message.class);

                    if(message!=null) {
                        //While adding object into firebase i was making setMine false(setMine true for me while filling in
                        // arraylist) so they will be equal only if i will set its value true.
                        message.setMine(true);
                        Log.i("message", message.getMessage() + " " + message.isSeen() + " " + message.isMine());
                        if (chatMessageArrayList.contains(message)) {
                            int position = chatMessageArrayList.indexOf(message);
                            message.setSeen(true);
                            Log.i("Position", String.valueOf(position));
                            chatMessageArrayList.set(position, message);
                        }
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
    }

    private void showOpponentMessage(View rootView,String message) {

        LayoutInflater layoutInflater = (LayoutInflater) Objects.requireNonNull(getContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        if (layoutInflater != null) {
            view = layoutInflater.inflate(R.layout.chat_left_raw_layout, null, false);
            chatFromTextView = view.findViewById(R.id.chatGettingTextView);
            chatFromTextView.setText(message);
            ViewGroup viewGroup = rootView.findViewById(R.id.messagesLinearLayout);
            viewGroup.addView(view,sendMessagePosition, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            sendMessagePosition+=1; //This position is important. it will decide where to set which view.
        }

    }
}
