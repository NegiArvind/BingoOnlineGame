package arvindandroid.com.arvind.bingoonlinegame.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import arvindandroid.com.arvind.bingoonlinegame.R;

public class OnlinePlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView playerImageView;
    public ImageView  dotImageView;
    public TextView playerNameTextView;
    public LinearLayout onlineLinearLayout;

    public OnlinePlayerViewHolder(View itemView) {
        super(itemView);
        playerImageView=itemView.findViewById(R.id.playerImageView);
        dotImageView=itemView.findViewById(R.id.dotImageView);
        playerNameTextView=itemView.findViewById(R.id.playerNameTextView);
        onlineLinearLayout=itemView.findViewById(R.id.onlineLinearLayout);
    }

    @Override
    public void onClick(View v) {

    }
}
