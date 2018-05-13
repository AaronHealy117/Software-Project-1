package x14532757.softwareproject.User;

/**
 * Created by x14532757 on 06/03/2018.
 */

public class User {

    //model class for the user

    private String UserName;
    private String UserEmail;
    private String UserPhoneNumber;

    public User() {
    }

    public User(String userName, String userEmail, String userPhoneNumber) {
        this.UserName = userName;
        this.UserEmail = userEmail;
        this.UserPhoneNumber = userPhoneNumber;
    }

    String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    String getUserPhoneNumber() {
        return UserPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        UserPhoneNumber = userPhoneNumber;
    }
}
