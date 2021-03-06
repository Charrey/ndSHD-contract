package unit.iterator;

import com.charrey.algorithms.UtilityData;
import com.charrey.graph.MyGraph;
import com.charrey.graph.Path;
import com.charrey.graph.generation.succeed.RandomSucceedDirectedTestCaseGenerator;
import com.charrey.matching.VertexMatching;
import com.charrey.occupation.GlobalOccupation;
import com.charrey.pathiterators.PathIterator;
import com.charrey.pathiterators.PathIteratorFactory;
import com.charrey.pruning.DomainCheckerException;
import com.charrey.pruning.serial.PartialMatching;
import com.charrey.settings.Settings;
import com.charrey.settings.SettingsBuilder;
import com.charrey.util.Util;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well512a;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

class CompleteTest extends PathIteratorTest {

    private final RandomGenerator random = new Well512a(102038);
    private static final int differentGraphSizes = 300;
    private static final int trials = 10;

    @NotNull
    private static String myMaptoString(@NotNull Map<String, Set<String>> pathCount) {
        StringBuilder sb = new StringBuilder();
        Set<String> common = new HashSet<>();
        pathCount.values().forEach(common::addAll);
        new HashSet<>(common).forEach(path -> {
            if (pathCount.entrySet().stream().anyMatch(x -> !x.getValue().contains(path))) {
                common.remove(path);
            }
        });
        for (Map.Entry<String, Set<String>> entry : pathCount.entrySet()) {
            List<String> extra = entry.getValue().stream().filter(x -> !common.contains(x)).sorted().collect(Collectors.toList());
            sb.append("Option ").append(entry.getKey()).append(":\t").append(common.size()).append(" common and ").append(extra.isEmpty() ? "nothing else" : extra).append("\n");
        }
        return sb.toString();
    }

    @Test
    void testIterators() {
        List<Pair<String, Settings>> settingsToTry = new LinkedList<>();
        settingsToTry.add(new Pair<>("kpath          ", new SettingsBuilder().withKPathRouting().get()));
        settingsToTry.add(new Pair<>("cached dfs     ", new SettingsBuilder().withCachedDFSRouting().get()));
        settingsToTry.add(new Pair<>("inplace dfs    ", new SettingsBuilder().withInplaceDFSRouting().get()));
        settingsToTry.add(new Pair<>("old greedy dfs ", new SettingsBuilder().withInplaceOldGreedyDFSRouting().get()));
        settingsToTry.add(new Pair<>("new greedy dfs ", new SettingsBuilder().withInplaceNewGreedyDFSRouting().get()));
        settingsToTry.add(new Pair<>("CP             ", new SettingsBuilder().withControlPointRouting().get()));


        settingsToTry = settingsToTry.stream().map(x -> new Pair<>(x.getFirst(), new SettingsBuilder(x.getSecond()).allowingLongerPaths().withoutPruning().get())).collect(Collectors.toList());

        final long seed = 1923;
        long counter = -1;
        RandomSucceedDirectedTestCaseGenerator gen = new RandomSucceedDirectedTestCaseGenerator(2, 1, 0, 0, seed);
        for (int i = 0; i < differentGraphSizes; i++) {
            gen.makeHarder();
            gen.init(trials);
            while (gen.hasNext()) {
                MyGraph targetGraph = gen.getNext().getSourceGraph();
                MyGraph sourceGraph = new MyGraph(true);
                sourceGraph.addEdge(sourceGraph.addVertex(), sourceGraph.addVertex());
                UtilityData data = new UtilityData(sourceGraph, targetGraph);
                int tail = Util.selectRandom(targetGraph.vertexSet(), x -> true, random);
                int head = Util.selectRandom(targetGraph.vertexSet(), x -> x != tail, random);
                counter++;
                if (Graphs.neighborSetOf(targetGraph, tail).contains(head)) {
                    continue;
                }
                if (counter < 183) {
                    continue;
                }
                System.out.print(counter % 100 == 0 ? counter + "/" + differentGraphSizes * trials + "\n" : "");
                Map<String, Set<String>> pathCount = new HashMap<>(); //s
                for (Pair<String, Settings> settings : settingsToTry) {
                    if (!(settings.getFirst().contains("cached dfs") || settings.getFirst().contains("kpath"))) {
                        continue;
                    }

                    pathCount.put(settings.getFirst(), new HashSet<>());
                    GlobalOccupation occupation = new GlobalOccupation(data, settings.getSecond());

                    List<Integer> vertexOccupation;
                    occupation.init(new VertexMatching(sourceGraph, targetGraph, occupation, settings.getSecond()));
                    try {
                        occupation.occupyVertex(0, tail, new PartialMatching());
                        vertexOccupation = new ArrayList<>();
                        vertexOccupation.add(tail);
                        occupation.occupyVertex(1, head, new PartialMatching(vertexOccupation));
                    } catch (DomainCheckerException e) {
                        continue;
                    }
                    PathIterator iterator = PathIteratorFactory.get(targetGraph, data, tail, head, occupation, () -> 2, settings.getSecond(), () -> {
                        List<Integer> vertexMatching = new ArrayList<>();
                        vertexMatching.add(tail);
                        vertexMatching.add(head);
                        return new PartialMatching(vertexMatching);
                    }, Long.MAX_VALUE, 0, -1, -1);
                    Path path;
                    try {
                        while ((path = iterator.next()) != null) {
                            //System.out.println(settings.getFirst() + " " + path);
                            assert path.asList().size() == new TIntHashSet(path.asList()).size();
                            pathCount.get(settings.getFirst()).add(new Path(path).toString());
                        }
                    } catch (Exception e) {
                        System.err.println(counter);
                        fail();
                        throw e;
                    }
                }
                assert new HashSet<>(pathCount.values()).size() == 1 : counter + "\n" + myMaptoString(pathCount) + "for:\n" + targetGraph.toString();
            }
        }
    }
}
