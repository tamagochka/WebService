package my.tamagochka.accounts;

import java.util.HashMap;
import java.util.Map;

public class AccountService {

    private final Map<String, UserProfile> loginToProfile; // bind user login to user profile
    private final Map<String, UserProfile> sessionToProfile; // bind session id to user profile

    public AccountService() {
        loginToProfile = new HashMap<>();
        sessionToProfile = new HashMap<>();
    }

    public void addNewUser(UserProfile userProfile) { loginToProfile.put(userProfile.getLogin(), userProfile); }
    public UserProfile getUserByLogin(String login) { return loginToProfile.get(login); }
    public UserProfile getUserBySession(String session) { return sessionToProfile.get(session); }

    public void addSession(String session, UserProfile userProfile) {
        sessionToProfile.put(session, userProfile);
    }

    public void deleteSession(String session) { sessionToProfile.remove(session); }
}
