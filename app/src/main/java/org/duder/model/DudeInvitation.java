package org.duder.model;

import android.widget.Button;

import org.duder.dto.user.FriendshipStatus;


public class DudeInvitation {
    private FriendshipStatus friendshipStatus;
    private Button mInvFriendBtn;

    public DudeInvitation(FriendshipStatus friendshipStatus, Button mInvFriendBtn) {
        this.friendshipStatus = friendshipStatus;
        this.mInvFriendBtn = mInvFriendBtn;
    }

    public FriendshipStatus getFriendshipStatus() {
        return friendshipStatus;
    }

    public Button getmInvFriendBtn() {
        return mInvFriendBtn;
    }
}
