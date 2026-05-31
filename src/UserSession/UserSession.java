package UserSession;

import controllers.UserController;
import models.userModels.User.User;

public class UserSession extends User {
    private static UserSession instance = null;

    private UserSession(User user) {
        super(user.getUserId(), user.getFirstName(), user.getLastName(), user.getAge(), user.getEmailAddress(), user.getPassword());
    }

    public static synchronized void setUserSession(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        instance = new UserSession(user);
    }

    public static synchronized void setUserSession(int userId) {
        UserController userController = new UserController();
        User user = userController.getUserById(userId);

        if (user == null) {
            throw new IllegalArgumentException("No user found for ID: " + userId);
        }

        setUserSession(user);
    }

    public static synchronized void setUserSession(String userEmail) {
        UserController userController = new UserController();
        User user = userController.getUserByEmail(userEmail);

        if (user == null) {
            throw new IllegalArgumentException("No user found for email: " + userEmail);
        }

        setUserSession(user);
    }

    public static synchronized UserSession getUserSession() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("User session has not been initialized.");
        }

        return instance;
    }

    public static synchronized UserSession getUserSession(int userId) throws IllegalStateException {
        setUserSession(userId);
        return getUserSession();
    }

    public static synchronized UserSession getUserSession(String userEmail) throws IllegalStateException {
        setUserSession(userEmail);
        return getUserSession();
    }

    public static synchronized void clearSession() {
        instance = null;
    }

    public boolean isActive() {
        return instance == this;
    }
}
