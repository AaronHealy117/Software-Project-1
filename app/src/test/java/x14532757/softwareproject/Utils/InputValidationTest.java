package x14532757.softwareproject.Utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by x14532757 on 08/04/2018.
 */
public class InputValidationTest {
    @Test
    public void SpecialChars() throws Exception {

        String textValue = "Password!;";

        InputValidation validation = new InputValidation();
        List<String> errorList = new ArrayList<>();
        boolean isValid = validation.NoSpecialChars(textValue, errorList);

        assertEquals(true, isValid);


    }

    @Test
    public void noSpecialChars() throws Exception {

        String textValue = "Password";

        InputValidation validation = new InputValidation();
        List<String> errorList = new ArrayList<>();
        boolean isValid = validation.NoSpecialChars(textValue, errorList);

        assertEquals(false, isValid);


    }

}