package unit.iterator;

import com.charrey.Occupation;
import com.charrey.graph.Path;
import com.charrey.graph.Vertex;
import com.charrey.graph.generation.MyGraph;
import com.charrey.graph.generation.succeed.RandomSucceedDirectedTestCaseGenerator;
import com.charrey.pathiterators.PathIterator;
import com.charrey.settings.PathIterationStrategy;
import com.charrey.settings.RunTimeCheck;
import com.charrey.settings.Settings;
import com.charrey.util.Util;
import com.charrey.algorithms.UtilityData;
import com.charrey.runtimecheck.DomainCheckerException;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.Graphs;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

class CompleteTest extends PathIteratorTest {

    private final Random random = new Random(102038);
    private static final int differentGraphSizes = 250;
    private static final int trials = 10;
    private static final Settings settings = new Settings(
            true,
            true,
            true,
            RunTimeCheck.NONE,
            PathIterationStrategy.YEN,
            new Random(300));

    @Test
    void testIterators() throws DomainCheckerException {
        final long seed = 1923;
        long counter = -1;
        RandomSucceedDirectedTestCaseGenerator gen = new RandomSucceedDirectedTestCaseGenerator(2, 1, 0, 0, seed);
        for (int i = 0; i < differentGraphSizes; i++) {
            gen.makeHarder();
            gen.init(trials, false);
            while (gen.hasNext()) {
                MyGraph targetGraph = gen.getNext().sourceGraph;
                MyGraph sourceGraph = new MyGraph(true);
                sourceGraph.addEdge(sourceGraph.addVertex(), sourceGraph.addVertex());
                UtilityData data = new UtilityData(sourceGraph, targetGraph);
                Vertex tail = Util.selectRandom(targetGraph.vertexSet(), x -> true, random);
                Vertex head = Util.selectRandom(targetGraph.vertexSet(), x -> x != tail, random);
                if (Graphs.neighborSetOf(targetGraph, tail).contains(head)) {
                    continue;
                }
                counter++;
                System.out.print(counter % 100 == 0 ? counter + "/" + differentGraphSizes * trials + "\n" : "");
                Map<Integer, Set<Path>> pathCount = new HashMap<>(); //s
                for (int strategy : List.of(PathIterationStrategy.DFS_ARBITRARY, PathIterationStrategy.DFS_GREEDY, PathIterationStrategy.CONTROL_POINT, PathIterationStrategy.YEN)) {
                    settings.pathIteration = strategy;
                    pathCount.put(strategy, new HashSet<>());
                    Occupation occupation = new Occupation(data, targetGraph.vertexSet().size(), settings);
                    occupation.occupyVertex(0, tail);
                    occupation.occupyVertex(1, head);
                    PathIterator iterator = PathIterator.get(targetGraph, data, tail, head, occupation, () -> 2, settings);
                    Path path;
                    while ((path = iterator.next()) != null) {
                        assert path.asList().size() == new HashSet<>(path.asList()).size();
                        pathCount.get(strategy).add(new Path(path));
                    }
                }
                assert new HashSet<>(pathCount.values()).size() == 1 : counter + "\n" + myMaptoString(pathCount) + "for:\n" + targetGraph.toString();
            }
        }
    }

    @NotNull
    private String myMaptoString(@NotNull Map<Integer, Set<Path>> pathCount) {
        StringBuilder sb = new StringBuilder();
        Set<Path> common = new HashSet<>();
        pathCount.values().forEach(common::addAll);
        new HashSet<>(common).forEach(path -> {
            if (pathCount.entrySet().stream().anyMatch(x -> !x.getValue().contains(path))) {
                common.remove(path);
            }
        });
        for (Map.Entry<Integer, Set<Path>> entry : pathCount.entrySet()) {
            List<Path> extra = entry.getValue().stream().filter(x -> !common.contains(x)).sorted().collect(Collectors.toList());
            sb.append("Option ").append(entry.getKey()).append(":\t").append(common.size()).append(" common and ").append(extra.isEmpty() ? "nothing else" : extra).append("\n");
        }
        return sb.toString();
    }
}
