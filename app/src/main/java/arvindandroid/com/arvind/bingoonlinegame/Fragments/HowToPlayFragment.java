package arvindandroid.com.arvind.bingoonlinegame.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import arvindandroid.com.arvind.bingoonlinegame.Activities.OptionsActivity;
import arvindandroid.com.arvind.bingoonlinegame.R;

public class HowToPlayFragment extends Fragment {

    private TextView textView;
    private Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.how_to_play_fragment_layout,container,false);
        context=getContext();
        textView=view.findViewById(R.id.howToPlayTextView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }

    public static HowToPlayFragment newInstance() {
        
        Bundle args = new Bundle();
        
        HowToPlayFragment fragment = new HowToPlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((OptionsActivity)context).setActionBarTitle("How To Play ?");
    }
}
