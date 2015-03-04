package JSimpleFuzzySet;

import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class implements set interface along with an API, fuzzyMatches, which retrieves items in the set,
 *  which matches by given Maximum Edit Distance. More edit distance and scoreDeviationInPercent
 *      could result in poor performance.
 *  So, edit distance and relax percent should be judiciously chosen as per the requirement.
 */
public class SimpleFuzzySet implements Set<String> {

    // This field decides up the range of scores to consider while processing the output.
    private double scoreDeviationInPercent = 0.10;

    // Max allowed edit distance, is 2, by default.
    private int maxAllowedEditDistance = 2;

    Set<String> terms = new TreeSet<>();
    TreeMap<Long, String> scoreMap = new TreeMap<>();

    private TermScoreCalculator termScoreCalculator = new SumOfCharactersBasedTermScoreCalculator();
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public SimpleFuzzySet() {
    }

    public SimpleFuzzySet(int maxAllowedEditDistance, double scoreDeviationInPercent) {
        this.maxAllowedEditDistance = maxAllowedEditDistance;
        this.scoreDeviationInPercent = scoreDeviationInPercent;
    }

    public SimpleFuzzySet setTermScoreCalculator(TermScoreCalculator termScoreCalculator) {
        this.termScoreCalculator = termScoreCalculator;
        return this;
    }

    @Override
    public int size() {
        return terms.size();
    }

    @Override
    public boolean isEmpty() {
        return terms.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        String preprocessed;
        if (o instanceof String) {
            preprocessed = preProcessToken((String) o);
        } else {
            return false;
        }
        return terms.contains(preprocessed);
    }

    public List<String> fuzzyMatches(String keyTerm) {
        final String processedKeyTerm = preProcessToken(keyTerm);
        if(terms.contains(processedKeyTerm)) {
            return new ArrayList<String>() {{
                add(processedKeyTerm);
            }};
        } else {
            ArrayList<String> results = new ArrayList<>();
            long scoreOfTerm = termScoreCalculator.calculate(processedKeyTerm);
            logger.debug("Score for " + processedKeyTerm + " : " + scoreOfTerm);

            long start = (long) (scoreOfTerm - Math.floor(scoreOfTerm * scoreDeviationInPercent));
            long end = (long) (scoreOfTerm + Math.floor(scoreOfTerm * scoreDeviationInPercent));

            SortedMap<Long, String> subMap = scoreMap.subMap(start, end);

            for(String match : subMap.values()) {
                if(SimpleFuzzySet.calculateEditDistance(match, processedKeyTerm) <= maxAllowedEditDistance) {
                    results.add(match);
                }
            }
            return results;
        }
    }

    // This code for calculating Edit Distance, is hugely inspired from
    //  https://github.com/KevinStern/software-and-algorithms/blob/master/src/main/java/blogspot/software_and_algorithms/stern_library/string/DamerauLevenshteinAlgorithm.java
    public static int calculateEditDistance(String input, String target) {

        // In case, any of the strings is of zero length, Edit distance will be other string's length.
        if (input.length() == 0) {
            return target.length();
        }
        if (target.length() == 0) {
            return input.length();
        }


        int[][] table = new int[input.length()][target.length()];
        Map<Character, Integer> sourceIndexByCharacter = new HashMap<>();
        if (input.charAt(0) != target.charAt(0)) {
            table[0][0] = 1;
        }
        sourceIndexByCharacter.put(input.charAt(0), 0);
        for (int i = 1; i < input.length(); i++) {
            int deleteDistance = table[i - 1][0] + 1;
            int insertDistance = (i + 1) + 1;
            int matchDistance = i + (input.charAt(i) == target.charAt(0) ? 0 : 1);
            table[i][0] = Math.min(Math.min(deleteDistance, insertDistance),
                    matchDistance);
        }
        for (int j = 1; j < target.length(); j++) {
            int deleteDistance = (j + 1) + 1;
            int insertDistance = table[0][j - 1] + 1;
            int matchDistance = j
                    + (input.charAt(0) == target.charAt(j) ? 0 : 1);
            table[0][j] = Math.min(Math.min(deleteDistance, insertDistance),
                    matchDistance);
        }
        for (int i = 1; i < input.length(); i++) {
            int maxSourceLetterMatchIndex = input.charAt(i) == target.charAt(0) ? 0
                    : -1;
            for (int j = 1; j < target.length(); j++) {
                Integer candidateSwapIndex = sourceIndexByCharacter.get(target
                        .charAt(j));
                int jSwap = maxSourceLetterMatchIndex;
                int deleteDistance = table[i - 1][j] + 1;
                int insertDistance = table[i][j - 1] + 1;
                int matchDistance = table[i - 1][j - 1];
                if (input.charAt(i) != target.charAt(j)) {
                    matchDistance += 1;
                } else {
                    maxSourceLetterMatchIndex = j;
                }
                int swapDistance;
                if (candidateSwapIndex != null && jSwap != -1) {
                    int iSwap = candidateSwapIndex;
                    int preSwapCost;
                    if (iSwap == 0 && jSwap == 0) {
                        preSwapCost = 0;
                    } else {
                        preSwapCost = table[Math.max(0, iSwap - 1)][Math.max(0, jSwap - 1)];
                    }
                    swapDistance = preSwapCost + (i - iSwap - 1)
                            + (j - jSwap - 1) + 1;
                } else {
                    swapDistance = Integer.MAX_VALUE;
                }
                table[i][j] = Math.min(Math.min(Math
                        .min(deleteDistance, insertDistance), matchDistance), swapDistance);
            }
            sourceIndexByCharacter.put(input.charAt(i), i);
        }
        return table[input.length() - 1][target.length() - 1];
    }

    @Override
    public Iterator<String> iterator() {
        return terms.iterator();
    }

    @Override
    public Object[] toArray() {
        return terms.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return terms.toArray(ts);
    }

    @Override
    public boolean add(String s) {
        s = preProcessToken(s);
        Long score = termScoreCalculator.calculate(s);
        logger.debug("Score for " + s + " : " + score);
        scoreMap.put(score, s);

        return terms.add(s);
    }

    public static String preProcessToken(String s) {
        s = s.trim().toLowerCase();
        return s;
    }

    @Override
    public boolean remove(Object o) {
        return terms.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return terms.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends String> collection) {
        return terms.addAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return terms.retainAll(collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return terms.removeAll(collection);
    }

    @Override
    public void clear() {
        terms.clear();
    }
}
