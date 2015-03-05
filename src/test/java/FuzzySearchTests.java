import JSimpleFuzzySet.SimpleFuzzySet;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FuzzySearchTests {

    @Test
    public void basicFuzzyTests() {
        SimpleFuzzySet fuzzySearch = new SimpleFuzzySet();

        fuzzySearch.add("blue");
        boolean contains = fuzzySearch.contains("blue");
        Assert.assertEquals(contains, true);
    }

    @Test
    public void fuzzySimpleTests() {
        SimpleFuzzySet search = new SimpleFuzzySet();

        search.add("blue");
        List<String> fuzzyMatches = search.fuzzyMatches("blue");
        Assert.assertEquals(fuzzyMatches.size(), 1);
    }

    @Test
    public void fuzzyTests() {
        SimpleFuzzySet search = new SimpleFuzzySet(2, 0.10);

        search.add("Lenova");
        search.add("Nexus");
        search.add("Nexs");
        search.add("Samsung");
        search.add("Intex");
        search.add("BHM1100");
        search.add("BHM1200");
        search.add("HP");
        search.add("GB");

        List<String> fuzzyMatches = search.fuzzyMatches("index");
        junit.framework.Assert.assertTrue(fuzzyMatches.size() > 0);

        fuzzyMatches = search.fuzzyMatches("nexs");
        junit.framework.Assert.assertTrue(fuzzyMatches.size() > 0);

        for(String match : fuzzyMatches) {
            System.out.println(match);
        }

    }

    @Test
    public void editDistanceTests() {
        int i = SimpleFuzzySet.calculateEditDistance("Karthik", "Karthi");
        System.out.println(i);
    }
}