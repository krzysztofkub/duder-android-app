package org.duder.util;

import org.duder.model.User;

public class UserSession {
    private User user;
    private static UserSession userSession;

    private UserSession(User user) {
        this.user = user;
    }

    public static UserSession getUserSession() {
        return userSession;
    }

    public static void createUserSession(User user) {
        if (userSession == null) {
            userSession = new UserSession(user);
        }
    }

    public static boolean isSessionEmpty() {
        return userSession == null;
    }

    public User getUser() {
        return user;
    }
}
