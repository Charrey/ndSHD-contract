package com.charrey;

import com.charrey.graph.Path;
import com.charrey.graph.Vertex;
import com.charrey.graph.generation.RandomTestCaseGenerator;
import com.charrey.matching.EdgeMatching;
import com.charrey.matching.VertexMatching;
import com.charrey.util.Util;
import com.charrey.util.UtilityData;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;


public class IsoFinder {

    private static final Logger LOG = Logger.getLogger("IsoFinder");

    public static Optional<Homeomorphism> getHomeomorphism(RandomTestCaseGenerator.TestCase testcase) {
        UtilityData data = new UtilityData(testcase.source.getGraph(), testcase.target.getGraph());
        if (Arrays.stream(data.getCompatibility()).anyMatch(x -> x.length == 0)) {
            return Optional.empty();
        }
        Occupation occupation         = new Occupation(testcase.target.getGraph().vertexSet().size());
        VertexMatching vertexMatching = new VertexMatching(data, testcase.source, occupation);
        EdgeMatching edgeMatching     = new EdgeMatching(vertexMatching, data, testcase.source, testcase.target, occupation);
        boolean exhausedAllPaths = false;
        while (!allDone(testcase.source.getGraph(), testcase.target.getGraph(), vertexMatching, edgeMatching)) {
            LOG.fine(vertexMatching::toString);
            LOG.fine(edgeMatching::toString);
            if (exhausedAllPaths) {
                exhausedAllPaths = false;
                vertexMatching.removeLast();
                continue;
            }

            if (edgeMatching.hasUnmatched()) {
                Path nextpath = edgeMatching.placeNextUnmatched();
                if (nextpath == null) {
                    exhausedAllPaths = true;
                }
            } else if (vertexMatching.canPlaceNext()) {
                vertexMatching.placeNext();
            } else if (edgeMatching.retry()) {
                vertexMatching.giveAllowance();
            } else if (vertexMatching.canRetry()) {
                vertexMatching.removeLast();
            } else {
                return Optional.empty();
            }
        }
        if (vertexMatching.getPlacementUnsafe().size() < testcase.source.getGraph().vertexSet().size()) {
            return Optional.empty();
        } else {
            return Optional.of(new Homeomorphism(vertexMatching, edgeMatching));
        }
    }



    private static boolean allDone(Graph<Vertex, DefaultEdge> pattern, Graph<Vertex, DefaultEdge> target, VertexMatching vertexMatching, EdgeMatching edgeMatching) {
        boolean completeV = vertexMatching.getPlacementUnsafe().size() == pattern.vertexSet().size();
        if (!completeV) {
            return false;
        }
        boolean completeE = !edgeMatching.hasUnmatched();
        if (!completeE) {
            return false;
        }
        System.out.println("Done, checking...");
        assert  Util.isCorrect(pattern, target, vertexMatching, edgeMatching);
        return true;
    }


}
