package com.charrey.settings.pruning.domainfilter;

import com.charrey.graph.MyGraph;
import com.charrey.occupation.GlobalOccupation;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class LabelDegreeFiltering extends FilteringSettings {


    @Override
    public boolean filter(MyGraph sourceGraph, MyGraph targetGraph, int sourceGraphVertex, int targetGraphVertex, GlobalOccupation occupation) {
        return sourceGraph.getLabels(sourceGraphVertex).containsAll(targetGraph.getLabels(targetGraphVertex)) &&
                targetGraph.inDegreeOf(targetGraphVertex) >= sourceGraph.inDegreeOf(sourceGraphVertex) &&
                targetGraph.outDegreeOf(targetGraphVertex) >= sourceGraph.outDegreeOf(sourceGraphVertex);
    }

    @Override
    public TIntSet sourceVerticestoReCheck(MyGraph sourceGraph, MyGraph targetGraph, int sourceGraphVertex, int targetGraphVertex, GlobalOccupation occupation) {
        return new TIntHashSet();
    }

    @Override
    public TIntSet targetVerticestoReCheck(MyGraph sourceGraph, MyGraph targetGraph, int sourceGraphVertex, int targetGraphVertex, GlobalOccupation occupation) {
        return new TIntHashSet();
    }

}