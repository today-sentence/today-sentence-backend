package today.todaysentence.util.email;

import java.security.SecureRandom;

public class VerificationCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$";
    private static final int CODE_LENGTH = 6;
    private static final int PASSWORD_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();


    public static String generateCode() {

        StringBuilder code = new StringBuilder(CODE_LENGTH);


        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }

    public static String generatePassword() {
        StringBuilder code = new StringBuilder(PASSWORD_LENGTH);

        code.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        code.append(SPECIAL_CHARACTERS.charAt(RANDOM.nextInt(SPECIAL_CHARACTERS.length())));

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }


}
