import JSimpleFuzzySet.MetricTree;
import JSimpleFuzzySet.SimpleFuzzySet;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nivetha on 5/3/15.
 */
public class ProductDataSetTests {

    @Test
    public void pullDataFromFile() throws IOException {
        FileReader reader = new FileReader("/tmp/fuzzydataset.txt");
        SimpleFuzzySet fuzzySet = new SimpleFuzzySet(1, 0.10);

        BufferedReader bufferedReader = new BufferedReader(reader);
        String currentToken = null;
        while((currentToken = bufferedReader.readLine()) != null) {
            fuzzySet.add(currentToken);
        }

        List<String> fuzzyMatches = fuzzySet.fuzzyMatches("samusng");
        for(String match : fuzzyMatches) {
            System.out.println(match);
        }
    }

    @Test
    public void pullDataFromFileForMetricTree() throws IOException {
        FileReader reader = new FileReader("/tmp/fuzzydataset.txt");
        final Set<String> entries = new HashSet<>();

        BufferedReader bufferedReader = new BufferedReader(reader);
        String currentToken = null;
        while((currentToken = bufferedReader.readLine()) != null) {
            entries.add(currentToken.toLowerCase().trim());
        }

        MetricTree tree = new MetricTree(new ArrayList<String>() {{ addAll(entries); }});
        tree.getNeighborTokens("nexus", 0);
    }
}
