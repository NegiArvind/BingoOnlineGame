package arvindandroid.com.arvind.bingoonlinegame.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MyService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
        Toast.makeText(getApplicationContext(),"Destroyed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //Code here
        Toast.makeText(getApplicationContext(),"Task Removed",Toast.LENGTH_SHORT).show();
        makeUserOffline();
        deleteGameRequestAndChatObject();
    }

    private void makeUserOffline(){
        final DatabaseReference userReference= FirebaseDatabase.getInstance().getReference("Users");
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                        if(Objects.requireNonNull(itemSnapshot.getKey()).equalsIgnoreCase("online")){
                            userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(false);

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

    private void deleteGameRequestAndChatObject(){
        final DatabaseReference userReference= FirebaseDatabase.getInstance().getReference("Users");
        userReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("game").removeValue();
        userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("request").removeValue();
        userReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").removeValue();
        stopSelf();
    }
}
