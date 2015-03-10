package JSimpleFuzzySet;

import java.util.*;
import java.util.regex.Matcher;

/**
 *  This metric tree implementation is based on edit distance.
 *
 *  Though, theoretically, it has complexity of log n, sometimes, it may get bad due to large amount of overlap in child trees.
 */
public class MetricTree {

    private List<String> input = new ArrayList<>();
    private MetricTreeNode rootNode;
    private int numCompare = 0;

    public void getNeighborTokens(String searchKey, int distanceAllowed) {
        numCompare = 0;
        getNeighborsInternal(searchKey, distanceAllowed, rootNode);
        System.out.println("Comparisons made : " + numCompare);
    }

    private void getNeighborsInternal(String key, int distanceAllowed, MetricTreeNode rootNode) {
        if(rootNode == null) {
            return;
        }

        int currentDistanceMetric = SimpleFuzzySet.calculateEditDistance(key, rootNode.entry);
        numCompare++;
        if(currentDistanceMetric <= distanceAllowed) {
            System.out.println(rootNode.entry);
        }

        numCompare++;
        if(Math.abs(currentDistanceMetric - distanceAllowed) == rootNode.editDistance) {
            getNeighborsInternal(key, distanceAllowed, rootNode.left);
            getNeighborsInternal(key, distanceAllowed, rootNode.right);
        } else if(Math.abs(currentDistanceMetric - distanceAllowed) < rootNode.editDistance) {
            getNeighborsInternal(key, distanceAllowed, rootNode.left);
        } else {
            getNeighborsInternal(key, distanceAllowed, rootNode.right);
        }
    }

    private class MetricTreeNode {
        private String entry;
        private Integer editDistance;
        private MetricTreeNode left;
        private MetricTreeNode right;
        private int level;

        public MetricTreeNode(String token, Integer editDistance, int level) {
            this.entry = token;
            this.editDistance = editDistance;
            this.level = level;
        }
    };

    public MetricTree(List<String> input) {
        this.input.addAll(input);
        constructTree();
    }

    protected void printTree() {
        ArrayDeque<MetricTreeNode> nodes = new ArrayDeque<>();

        nodes.add(this.rootNode);
        int currentLevel = 0;
        System.out.println("");

        while(nodes.peek() != null) {
            MetricTreeNode element = nodes.pop();
            if(currentLevel == element.level) {
                System.out.print(element.entry + " " + element.editDistance + "\t");
            } else {
                System.out.println("");
                System.out.print(element.entry + " " + element.editDistance + "\t");
            }

            if(element.left != null) {
                nodes.add(element.left);
            }

            if(element.right != null) {
                nodes.add(element.right);
            }
            currentLevel = element.level;
        }
    }

    private void constructTree() {
        this.rootNode = constructTree(this.input, 0);
    }

    private MetricTreeNode constructTree(List<String> tokens, int level) {
        if(tokens.size() == 0) {
            return null;
        } else if(tokens.size() == 1) {
            return new MetricTreeNode(tokens.get(0), 0, level);
        } else if(tokens.size() == 2) {
            String token = tokens.get(0);
            String rootToken = tokens.get(1);
            MetricTreeNode rootNode = new MetricTreeNode(rootToken,
                    SimpleFuzzySet.calculateEditDistance(token, rootToken), level);
            MetricTreeNode leftNode = new MetricTreeNode(token, 0, level + 1);
            rootNode.left = leftNode;
            return rootNode;
        }

        System.out.println("Size: " + tokens.size());
        int randomIndex = tokens.size() - 1;
        String randomToken = tokens.get(randomIndex);

        List<Pair<String, Integer>> editDistanceFromNodesList = new ArrayList<>();

        for(String entry : tokens ) {
            if(!randomToken.equalsIgnoreCase(entry)) {

                int editDistance = SimpleFuzzySet.calculateEditDistance(randomToken, entry);
                editDistanceFromNodesList.add(new Pair<>(entry, editDistance));
            }
        }


        Collections.sort(editDistanceFromNodesList, new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> t, Pair<String, Integer> t1) {
                return Integer.compare(t.second, t1.second);
            }
        });

        int sortedSize = editDistanceFromNodesList.size();
        int medianIndex = sortedSize / 2;
        Pair<String, Integer> pair = sortedSize == 1
                ? editDistanceFromNodesList.get(0) : editDistanceFromNodesList.get(medianIndex);

        int medianEditDistance = pair.second;
        MetricTreeNode rootNode = new MetricTreeNode(randomToken, medianEditDistance, level);

        List<String> leftInput = new ArrayList<>();
        List<String> rightInput = new ArrayList<>();

        for(int i = 0; i < sortedSize; i++) {
            if(i <= medianIndex) {
                leftInput.add(editDistanceFromNodesList.get(i).first);
            } else {
                rightInput.add(editDistanceFromNodesList.get(i).first);
            }
        }

        /*for(Pair<String, Integer> distanceTokenPair : editDistanceFromNodesList) {
            if(distanceTokenPair.second <= medianEditDistance) {
                leftInput.add(distanceTokenPair.first);
            } else {
                rightInput.add(distanceTokenPair.first);
            }
        }*/

        MetricTreeNode leftNode = constructTree(leftInput, level + 1);
        MetricTreeNode rightNode = constructTree(rightInput, level + 1);
        rootNode.left = leftNode; rootNode.right = rightNode;
        return rootNode;
    }
}