package org.duder.model;

import android.view.View;
import android.widget.Button;

import org.duder.dto.user.Dude;

public class DudeItem {
    private Dude dude;
    private Button mInviteFriendBtn;

    public DudeItem(Dude dude, Button mInviteFriendBtn) {
        this.dude = dude;
        this.mInviteFriendBtn = mInviteFriendBtn;
    }

    public Dude getDude() {
        return dude;
    }

    public Button getmInviteFriendBtn() {
        return mInviteFriendBtn;
    }
}
