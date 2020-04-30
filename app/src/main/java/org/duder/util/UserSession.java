package org.duder.util;

import org.duder.dto.user.LoggedAccount;

public class UserSession {
    private LoggedAccount account;
    private static UserSession userSession;

    private UserSession(LoggedAccount account) {
        this.account = account;
    }

    public static UserSession getUserSession() {
        return userSession;
    }

    public static void createUserSession(LoggedAccount account) {
        if (userSession == null) {
            userSession = new UserSession(account);
        }
    }

    public static boolean isSessionEmpty() {
        return userSession == null;
    }

    public LoggedAccount getLoggedAccount() {
        return account;
    }
}
