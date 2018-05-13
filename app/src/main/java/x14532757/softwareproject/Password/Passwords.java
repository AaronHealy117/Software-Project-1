package x14532757.softwareproject.Password;

/**
 * Created by x14532757 on 16/10/2017.
 */

public class Passwords {

    private String PasswordName;
    private String PasswordText;
    private String PinCode;
    private String UserID;


    public Passwords() {
    }

    public Passwords(String passwordName, String passwordText, String pinCode, String userID) {
        PasswordName = passwordName;
        PasswordText = passwordText;
        PinCode = pinCode;
        UserID = userID;
    }

    String getPasswordName() {
        return PasswordName;
    }

    public void setPasswordName(String passwordName) {
        PasswordName = passwordName;
    }

    public String getPasswordText() {
        return PasswordText;
    }

    public void setPasswordText(String passwordText) {
        PasswordText = passwordText;
    }

    String getPinCode() {
        return PinCode;
    }

    public void setPinCode(String pinCode) {
        PinCode = pinCode;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

}
