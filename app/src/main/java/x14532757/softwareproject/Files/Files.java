package x14532757.softwareproject.Files;

/**
 * Created by x14532757 on 19/10/2017.
 */

public class Files {

    private String FileName;
    private String FileDesc;
    private String FileURL;
    private String PinCode;
    private String UserID;

    public Files() {
    }

    public Files(String fileName, String fileDesc, String fileURL, String pinCode, String userID) {
        FileName = fileName;
        FileDesc = fileDesc;
        FileURL = fileURL;
        PinCode = pinCode;
        UserID = userID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileDesc() {
        return FileDesc;
    }

    public void setFileDesc(String fileDesc) {
        FileDesc = fileDesc;
    }

    public String getFileURL() {
        return FileURL;
    }

    public void setFileURL(String fileURL) {
        FileURL = fileURL;
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
