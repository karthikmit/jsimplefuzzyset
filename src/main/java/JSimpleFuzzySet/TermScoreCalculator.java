package JSimpleFuzzySet;

/**
 * TermScoreCalculator interface should be implemented by domain specific score calculators, if needed.
 */

public interface TermScoreCalculator {

    long calculate(String keyTerm);
}
