package x14532757.softwareproject.Text;

/**
 * Created by x14532757 on 19/10/2017.
 */

public class Text {
    private String TextName;
    private String TextBlock;
    private String PinCode;
    private String UserID;

    public Text() {
    }

    public Text(String textName, String textBlock, String pinCode, String userID) {
        TextName = textName;
        TextBlock = textBlock;
        PinCode = pinCode;
        UserID = userID;
    }

    public String getTextName() {
        return TextName;
    }

    public void setTextName(String textName) {
        TextName = textName;
    }

    public String getTextBlock() {
        return TextBlock;
    }

    public void setTextBlock(String textBlock) {
        TextBlock = textBlock;
    }

    public String getPinCode() {
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
