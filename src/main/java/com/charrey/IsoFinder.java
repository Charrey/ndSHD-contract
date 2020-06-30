package com.charrey;

import com.charrey.algorithms.GreatestConstrainedFirst;
import com.charrey.algorithms.UtilityData;
import com.charrey.graph.MyEdge;
import com.charrey.graph.MyGraph;
import com.charrey.graph.Path;
import com.charrey.graph.generation.TestCase;
import com.charrey.matching.EdgeMatching;
import com.charrey.matching.VertexMatching;
import com.charrey.occupation.GlobalOccupation;
import com.charrey.result.*;
import com.charrey.runtimecheck.DomainCheckerException;
import com.charrey.settings.Settings;
import org.jetbrains.annotations.NotNull;
import org.jgrapht.alg.util.Pair;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Logger;


/**
 * Class that finds node disjoint subgraph homeomorphisms
 */
public class IsoFinder {

    private static final Logger LOG = Logger.getLogger("IsoFinder");
    private EdgeMatching edgeMatching;
    private VertexMatching vertexMatching;

    private static void logDomainReduction(@NotNull TestCase testcase, @NotNull UtilityData data, boolean initialNeighbourHoodFiltering, boolean initialGlobalAllDifferent, String name) {
        BigInteger naiveVertexDomainSize = new BigInteger(String.valueOf(testcase.getSourceGraph().vertexSet().size())).pow(testcase.getTargetGraph().vertexSet().size());
        BigInteger vertexDomainSize = Arrays.stream(data.getCompatibility(initialNeighbourHoodFiltering, initialGlobalAllDifferent, name)).reduce(new BigInteger("1"), (i, vs) -> i.multiply(new BigInteger(String.valueOf(vs.length))), BigInteger::multiply);
        NumberFormat formatter = new DecimalFormat("0.###E0", DecimalFormatSymbols.getInstance(Locale.ROOT));
        LOG.info(() -> "Reduced vertex matching domain from " + formatter.format(naiveVertexDomainSize) + " to " + formatter.format(vertexDomainSize));
    }

    private long lastPrint = 0;

    private static boolean allDone(@NotNull MyGraph pattern, @NotNull VertexMatching vertexMatching, @NotNull EdgeMatching edgeMatching) {
        boolean completeV = vertexMatching.getPlacement().size() == pattern.vertexSet().size();
        if (!completeV) {
            return false;
        }
        boolean completeE = !edgeMatching.hasUnmatched();
        if (!completeE) {
            return false;
        }
        LOG.info(() -> "Done, checking...");
        boolean correct = Verifier.isCorrect(pattern, vertexMatching, edgeMatching);
        assert correct;
        return true;
    }

    private void setup(@NotNull TestCase testcase, @NotNull Settings settings, String name) throws DomainCheckerException {
        UtilityData data = new UtilityData(testcase.getSourceGraph(), testcase.getTargetGraph());
        logDomainReduction(testcase, data, settings.initialNeighbourhoodFiltering, settings.initialGlobalAllDifferent, name);

        if (Arrays.stream(data.getCompatibility(settings.initialNeighbourhoodFiltering, settings.initialGlobalAllDifferent, name)).anyMatch(x -> x.length == 0)) {
            throw new DomainCheckerException("Intial domain check failed");
        }
        GlobalOccupation occupation = new GlobalOccupation(data, settings.pruningMethod, settings.initialNeighbourhoodFiltering, settings.initialGlobalAllDifferent, name);
        vertexMatching = new VertexMatching(data, testcase.getSourceGraph(), occupation, settings.initialNeighbourhoodFiltering, settings.initialGlobalAllDifferent, name);
        edgeMatching = new EdgeMatching(vertexMatching, data, testcase.getSourceGraph(), testcase.getTargetGraph(), occupation, settings.pathIteration, settings.refuseLongerPaths);
    }

    private static Map<MyEdge, Path> repairPaths(MyGraph oldSourceGraph, EdgeMatching edgeMatching, int[] vertexMapping) {
        Map<MyEdge, Path> res = new HashMap<>();
        List<LinkedList<Pair<Path, String>>> paths = edgeMatching.getPathsUnsafe();
        for (MyEdge edge : oldSourceGraph.edgeSet()) {
            Integer source = oldSourceGraph.getEdgeSource(edge);
            Integer target = oldSourceGraph.getEdgeTarget(edge);

            boolean found = false;
            for (Pair<Path, String> pathsRightDirection : paths.get(source)) {
                int from = pathsRightDirection.getFirst().first();
                int to = pathsRightDirection.getFirst().last();
                if (oldSourceGraph.isDirected()) {
                    if (from == vertexMapping[source] && to == vertexMapping[target]) {
                        found = true;
                        res.put(edge, pathsRightDirection.getFirst());
                        break;
                    }
                } else {
                    if (Set.of(from, to).equals(Set.of(vertexMapping[source], vertexMapping[target]))) {
                        found = true;
                        res.put(edge, pathsRightDirection.getFirst());
                        break;
                    }
                }
            }
            if (!found) {
                for (Pair<Path, String> pathsReverseDirection : paths.get(target)) {
                    int from = pathsReverseDirection.getFirst().first();
                    int to = pathsReverseDirection.getFirst().last();
                    if (oldSourceGraph.isDirected()) {
                        if (from == vertexMapping[source] && to == vertexMapping[target]) {
                            found = true;
                            res.put(edge, pathsReverseDirection.getFirst());
                            break;
                        }
                    } else {
                        if (Set.of(from, to).equals(Set.of(vertexMapping[source], vertexMapping[target]))) {
                            found = true;
                            res.put(edge, pathsReverseDirection.getFirst());
                            break;
                        }
                    }
                }
            }
            assert found;
        }
        return res;
    }

    private static int[] repairMatching(int[] placement, int[] new_to_old) {
        int[] res = new int[placement.length];
        for (int i = 0; i < placement.length; i++) {
            res[i] = placement[new_to_old[i]];
        }
        return res;
    }

    /**
     * Searches for a node disjoint subgraph homeomorphism.
     *
     * @param testcase the case that contains a source graph and a target graph
     * @param settings settings to be used in the search
     * @param timeout  if the algorithm takes longer than this number of milliseconds, it stops and records a failure.
     * @return a result that provides information on the performance and which homeomorphism was found (if any).
     */
    @NotNull
    public HomeomorphismResult getHomeomorphism(@NotNull TestCase testcase, @NotNull Settings settings, long timeout, String name) {
        MyGraph sourceGraph;
        try {
            sourceGraph = new GreatestConstrainedFirst().apply(testcase.getSourceGraph()).graph;
            setup(testcase, settings, name);
        } catch (DomainCheckerException e) {
            return new CompatibilityFailResult();
        }
        long iterations = 0;
        long initialTime = System.currentTimeMillis();
        while (!allDone(testcase.getSourceGraph(), vertexMatching, edgeMatching)) {
            iterations++;
            if (System.currentTimeMillis() - lastPrint > 1000) {
                //System.out.println(name + " is at " + iterations + " iterations...");
                lastPrint = System.currentTimeMillis();
            }
            if (System.currentTimeMillis() > initialTime + timeout) {
                return new TimeoutResult(iterations);
            }
            LOG.fine(() -> vertexMatching.toString() + "\n" + edgeMatching.toString());
            if (edgeMatching.hasUnmatched()) {
                Path nextpath = edgeMatching.placeNextUnmatched();
                if (nextpath == null) {
                    if (edgeMatching.retry()) {
                        vertexMatching.giveAllowance();
                    } else {
                        vertexMatching.removeLast();
                    }
                }
            } else if (vertexMatching.canPlaceNext()) {
                vertexMatching.placeNext();
            } else if (edgeMatching.retry()) {
                vertexMatching.giveAllowance();
            } else if (vertexMatching.canRetry()) {
                vertexMatching.removeLast();
            } else {
                return new FailResult(iterations);
            }
        }
        if (vertexMatching.getPlacement().size() < testcase.getSourceGraph().vertexSet().size()) {
            return new FailResult(iterations);
        } else {
            int[] vertexMapping = vertexMatching.getPlacement().stream().mapToInt(x -> x).toArray();
            Map<MyEdge, Path> paths = repairPaths(testcase.getSourceGraph(), edgeMatching, vertexMapping);
            return new SuccessResult(vertexMapping, paths, iterations);
        }
    }


}
