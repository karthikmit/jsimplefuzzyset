package JSimpleFuzzySet;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple Term Score Calculator
 */
public class TermScoreCalculator {
    public static long calculate(String keyTerm) {
        long score = 0;
        char[] chars = keyTerm.toCharArray();

        if(chars.length == 0) {
            return 0;
        }

        int endIndex = chars.length;
        for(int i = 0 ; i < endIndex; i++) {
            if(Character.isDigit(chars[i])) {
                score += (chars[i] + 40);
            }
            score += chars[i];
        }

        return score;
    }
}
