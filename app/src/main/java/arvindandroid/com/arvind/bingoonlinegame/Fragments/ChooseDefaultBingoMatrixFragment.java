package arvindandroid.com.arvind.bingoonlinegame.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import arvindandroid.com.arvind.bingoonlinegame.Activities.OptionsActivity;
import arvindandroid.com.arvind.bingoonlinegame.R;

public class ChooseDefaultBingoMatrixFragment extends Fragment implements View.OnClickListener {

    private int[][][] bingoMatrix={{{1,2,3,4,5},{6,7,8,9,10},{11,12,13,14,15},{16,17,18,19,20},{21,22,23,24,25}},
            {{5,4,3,2,1},{6,7,8,9,10},{15,14,13,12,11},{16,17,18,19,20},{25,24,23,22,21}},
            {{1,20,22,23,9},{2,21,19,8,10},{3,24,7,18,11},{4,6,17,25,12},{5,16,15,14,13}},
            {{1,10,11,20,21},{2,9,12,19,22},{3,8,13,18,23},{4,7,14,17,24},{5,6,15,16,25}},
            {{1,25,3,16,17},{24,15,2,18,7},{14,4,19,6,21},{23,5,11,20,8},{13,12,10,9,22}}};
    private ImageView bingo1ImageView;
    private ImageView bingo2ImageView;
    private ImageView bingo3ImageView;
    private ImageView bingo4ImageView;
    private ImageView bingo5ImageView;
    private Activity activity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_default_bingo_matrix_fragment,container,false);
        activity=getActivity();
        bingo1ImageView=view.findViewById(R.id.bingo1ImageView);
        bingo2ImageView=view.findViewById(R.id.bingo2ImageView);
        bingo3ImageView=view.findViewById(R.id.bingo3ImageView);
        bingo4ImageView=view.findViewById(R.id.bingo4ImageView);
        bingo5ImageView=view.findViewById(R.id.bingo5ImageView);
        bingo1ImageView.setOnClickListener(this);
        bingo2ImageView.setOnClickListener(this);
        bingo3ImageView.setOnClickListener(this);
        bingo4ImageView.setOnClickListener(this);
        bingo5ImageView.setOnClickListener(this);
        return view;
    }

    public static ChooseDefaultBingoMatrixFragment newInstance() {

        Bundle args = new Bundle();
        ChooseDefaultBingoMatrixFragment fragment = new ChooseDefaultBingoMatrixFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.bingo1ImageView:
                addDifferentFragment(GameFragment.newInstance(bingoMatrix[0]));
                break;
            case R.id.bingo2ImageView:
                addDifferentFragment(GameFragment.newInstance(bingoMatrix[1]));
                break;
            case R.id.bingo3ImageView:
                addDifferentFragment(GameFragment.newInstance(bingoMatrix[2]));
                break;
            case R.id.bingo4ImageView:
                addDifferentFragment(GameFragment.newInstance(bingoMatrix[3]));
                break;
            case R.id.bingo5ImageView:
                addDifferentFragment(GameFragment.newInstance(bingoMatrix[4]));
                break;
        }

    }
    private void addDifferentFragment(Fragment fragment) {
        FragmentManager fragmentManager=getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment,"gameFragment").commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((OptionsActivity)activity).setActionBarTitle("Default Bingo");
    }
}
