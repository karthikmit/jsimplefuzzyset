import JSimpleFuzzySet.MetricTree;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by nivetha on 9/3/15.
 */
public class MetricTreeTests {

    @Test
    public void MetricTreeBasicsTests() {
        final List<String> tokens = new ArrayList<String>() {{
            add("goat");
            add("oyster");
            add("roster");
            add("hippo");
            add("toad");
            add("hamster");
            add("mouse");
            add("chicken");
            add("rooster");
            add("karthik");
            add("nivetha");
            add("diyon");
            add("prasanth");
            add("nexus");
            add("samsung");
            add("samsng");
            add("sheep");
            add("ant");
            add("horse");
            add("keyboard");
            add("led");
            add("string");
            add("clipboard");
        }};

        MetricTree tree = new MetricTree(new ArrayList<String>() {{
            addAll(tokens);
        }});

        //tree.printTree();
        //System.out.println("");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Neighbors calculation starts, ");
        tree.getNeighborTokens("cippo", 1);
    }
}
