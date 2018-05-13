package x14532757.softwareproject.Images;

/**
 * Created by x14532757 on 12/11/2017.
 */

public class Image {

    private String ImageDesc;
    private String ImageName;
    private String ImageURL;
    private String ImageUserID;
    private String PinCode;

    public Image() {
    }

    public Image(String imageDesc, String imageName, String imageURL, String imageUserID, String pinCode) {
        ImageDesc = imageDesc;
        ImageName = imageName;
        ImageURL = imageURL;
        ImageUserID = imageUserID;
        PinCode = pinCode;
    }

    String getImageDesc() {
        return ImageDesc;
    }

    public void setImageDesc(String imageDesc) {
        ImageDesc = imageDesc;
    }

    String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getImageUserID() {
        return ImageUserID;
    }

    public void setImageUserID(String imageUserID) {
        ImageUserID = imageUserID;
    }

    String getPinCode() {
        return PinCode;
    }

    public void setPinCode(String pinCode) {
        PinCode = pinCode;
    }
}
