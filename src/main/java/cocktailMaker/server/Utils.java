package cocktailMaker.server;

    import javafx.scene.control.Alert;

    import java.io.IOException;
    import java.math.BigInteger;
    import java.net.URL;
    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;
    import java.util.Properties;

/**
 * Created by yasen.marinov on 6/17/2017.
 */
public class Utils {

    public static Properties loadPropertiesFile(String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(ServerLauncher.class.getClassLoader().getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties;
    }

    public static String md5(String string) {
        String hash;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            BigInteger bigInteger = new BigInteger(1, md.digest(string.getBytes()));
            hash = bigInteger.toString(16);
        } catch (NoSuchAlgorithmException e) {
            hash = "";
            e.printStackTrace();
        }
        return hash;
    }

    public static class Dialogs {

        // Info
        public static final String TITLE_CALIBRATE_INSTRUCTIONS = "Calibrate instructions";
        public static final String TITLE_CALIBRATION = "Calibration";

        public static final String CONTENT_CALIBRATE_INSTRUCTIONS = "In order to calibrate fill exactly 100 ml of the ingredient attached to " +
                "the specified dispenser. Once ready press Start. Once the liquid has run out press Stop.";
        public static final String CONTENT_CALIBRATION_SUCCESS = "Calibration successful!";
        // Error
        public static final String TITLE_LOGIN_FAILED = "Login failed";
        public static final String TITLE_INCONSISTENT_DATA = "Inconsistent Data";
        public static final String TITLE_DELETE_FAILED = "Delete Failed";
        public static final String TITLE_PASSWORD_DO_NOT_MATCH = "Passwords do not match";
        public static final String TITLE_INGREDIENTS_UNAVAILABLE = "Ingredients unavailable";
        public static final String TITLE_UNRECOGNIZED_CARD = "Card not recognized";

        public static final String CONTENT_LOGIN_FAILED = "Username or password are incorrect";
        public static final String CONTENT_PASSWORDS_DO_NOT_MATCH = "The Password is different from the Confirm Password";
        public static final String CONTENT_ADD_INGREDIENT_TO_DISPENSER = "Please add the ingredient to an enabled Dispenser before calibration";
        public static final String CONTENT_CALIBRATION_FAILED = "Calibration failed!";
        public static final String CONTENT_UNRECOGNIZED_CARD = "Please make sure the card is configured";
        public static final String CONTENT_USED_CARD = "This card is already used";

        public static void openAlert(Alert.AlertType alertType, String title, String content) {
            openAlert(alertType, title, null, content);
        }

        public static void openAlert(Alert.AlertType alertType, String title, String header, String content) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        }
    }
}
