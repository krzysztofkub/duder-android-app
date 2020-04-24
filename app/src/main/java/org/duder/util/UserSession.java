package org.duder.util;

import org.duder.model.user.Account;

public class UserSession {
    private Account account;
    private static UserSession userSession;

    private UserSession(Account account) {
        this.account = account;
    }

    public static UserSession getUserSession() {
        return userSession;
    }

    public static void createUserSession(Account account) {
        if (userSession == null) {
            userSession = new UserSession(account);
        }
    }

    public static boolean isSessionEmpty() {
        return userSession == null;
    }

    public Account getAccount() {
        return account;
    }
}
