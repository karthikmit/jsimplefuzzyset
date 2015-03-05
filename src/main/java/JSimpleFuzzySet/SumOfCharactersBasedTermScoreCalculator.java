package JSimpleFuzzySet;

/**
 * Simple Term Score Calculator - Which calculates sum of individual characters in the string
 * This score calculator assumes only alpha-numeric characters are allowed in the string.
 *
 * 0-9 is moved just in from lower case a - z in ascii table.
 */
public class SumOfCharactersBasedTermScoreCalculator implements TermScoreCalculator {
    @Override
    public long calculate(String keyTerm) {
        long score = 0;
        char[] chars = keyTerm.toCharArray();

        if(chars.length == 0) {
            return 0;
        }

        for(Character character : chars) {
            if(Character.isDigit(character)) {
                score += (character + 40);
            } else {
                score += character;
            }
        }

        return score - 87;
    }
}
