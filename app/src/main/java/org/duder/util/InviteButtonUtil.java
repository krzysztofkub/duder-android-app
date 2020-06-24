package org.duder.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import org.duder.R;
import org.duder.dto.user.FriendshipStatus;

public class InviteButtonUtil {

    public static void setInviteButtonProperties(Context mContext,
                                                 FriendshipStatus friendshipStatus,
                                                 MaterialButton button) {
        switch (friendshipStatus) {
            case FRIENDS:
                button.setVisibility(View.GONE);
                break;
            case INVITATION_RECEIVED:
                button.setText("ACCEPT");
                button.setVisibility(View.VISIBLE);
                button.setClickable(true);
                button.setTextColor(mContext.getResources().getColor(R.color.white));
                button.setStrokeColorResource(R.color.white);
                button.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.secondary));
                break;
            case INVITATION_SENT:
                button.setText("SENT");
                button.setVisibility(View.VISIBLE);
                button.setClickable(false);
                button.setTextColor(mContext.getResources().getColor(R.color.gray));
                button.setStrokeColorResource(R.color.gray);
                break;
            case NONE:
                button.setText("INVITE");
                button.setVisibility(View.VISIBLE);
                button.setClickable(true);
                button.setTextColor(mContext.getResources().getColor(R.color.secondary));
                button.setStrokeColorResource(R.color.secondary);
                break;
        }
    }
}
