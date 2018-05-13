package x14532757.softwareproject.Utils;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by x14532757 on 21/03/2018.
 *
 * Code Modified from:
 * Title: Password validate 8 digits, contains upper,lowerCase,and a special Character
 * Author: Ankur
 * Availability: https://stackoverflow.com/questions/36097097/password-validate-8-digits-contains-upper-lowercase-and-a-special-character
 *
 */

@SuppressLint("Registered")
public class InputValidation extends AppCompatActivity {



    public boolean NoSpecialChars(String text, List<String> errorList) {

        //create special character pattern
        Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        errorList.clear();

        boolean flag=false;

        //if a special character is found then show error message
        if (specialCharPatten.matcher(text).find()) {
            errorList.add("These inputs cannot have a special character!");
            flag=true;
        }

        //returns true if special character found, false if no special characters found
        return flag;
    }



}
