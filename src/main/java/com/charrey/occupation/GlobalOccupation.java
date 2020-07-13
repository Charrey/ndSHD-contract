package com.charrey.occupation;

import com.charrey.algorithms.UtilityData;
import com.charrey.pruning.AllDifferentPruner;
import com.charrey.pruning.NoPruner;
import com.charrey.pruning.Pruner;
import com.charrey.pruning.ZeroDomainPruner;
import com.charrey.runtimecheck.DomainCheckerException;
import com.charrey.settings.Settings;
import com.charrey.settings.pruning.PruningApplicationConstants;
import com.charrey.settings.pruning.PruningConstants;
import com.charrey.settings.pruning.domainfilter.FilteringSettings;
import gnu.trove.TCollections;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that tracks for an entire homeomorphism source which target graph vertices are used up for which goal.
 * This class also throws exceptions when usage of a vertex would result in a dead end in the search.
 */
public class GlobalOccupation extends AbstractOccupation {

    @NotNull
    private final TIntSet routingBits;
    @NotNull
    private final TIntSet vertexBits;
    @NotNull
    private final Pruner domainChecker;
    private final UtilityData data;

    /**
     * Instantiates a new occupation tracker for a homeomorphism search.
     *
     * @param data     utility data class for cached computations
     * @param settings settings for this homeomorphism search
     */
    public GlobalOccupation(@NotNull UtilityData data, @NotNull Settings settings, String name) {
        this(data, settings.pruningMethod, settings.filtering, settings.whenToApply, name);
    }

    /**
     * Instantiates a new Global occupation.
     *
     * @param data         the data
     * @param runTimeCheck the run time check
     */
    public GlobalOccupation(@NotNull UtilityData data, int runTimeCheck, FilteringSettings filteringSettings, PruningApplicationConstants whenToApply, String name) {
        switch (runTimeCheck) {
            case PruningConstants.NONE:
                domainChecker = new NoPruner();
                break;
            case PruningConstants.EMPTY_DOMAIN:
                domainChecker = new ZeroDomainPruner(data, filteringSettings, name, whenToApply == PruningApplicationConstants.CACHED);
                break;
            case PruningConstants.ALL_DIFFERENT:
                if (whenToApply == PruningApplicationConstants.SERIAL) {
                    throw new IllegalArgumentException("AllDifferent cannot be run serially without caching. Choose CACHED execution or PARALLEL. Note that PARALLEL uses quadratic space.");
                }
                domainChecker = new AllDifferentPruner(data, filteringSettings, name);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        this.routingBits = new TIntHashSet();
        this.vertexBits = new TIntHashSet();
        this.data = data;
    }

    /**
     * Provides a new transaction that may be used to occupy vertices used for routing using a commit system
     *
     * @return the transaction
     */
    public OccupationTransaction getTransaction() {
        return new OccupationTransaction(new TIntHashSet(routingBits), new TIntHashSet(vertexBits), domainChecker.copy(), data, this);
    }


    /**
     * Occupies a vertex in the target graph and marks it as being used as 'intermediate' vertex.
     *
     * @param vertexPlacementSize the number of source graph vertices placed
     * @param vertex              the vertex being occupied for routing purposes
     */
    void occupyRoutingWithoutCheck(int vertexPlacementSize, int vertex) {
        assert !routingBits.contains(vertex);
        routingBits.add(vertex);
        domainChecker.afterOccupyEdgeWithoutCheck(vertexPlacementSize, vertex);
    }


    void occupyRoutingAndCheck(int vertexPlacementSize, int vertex) throws DomainCheckerException {
        assert !routingBits.contains(vertex);
        routingBits.add(vertex);
        domainChecker.afterOccupyEdge(vertexPlacementSize, vertex);
    }


    /**
     * Occupies vertex for vertex-on-vertex matching.
     *
     * @param source the source graph vertex
     * @param target the target graph vertex
     * @throws DomainCheckerException thrown when this occupation would result in a dead end in the search. If this is thrown, this class remains unchanged.
     */
    public void occupyVertex(Integer source, Integer target) throws DomainCheckerException {
        assert !routingBits.contains(target);
        assert !vertexBits.contains(target);
        vertexBits.add(target);
        try {
            domainChecker.beforeOccupyVertex(source, target);
        } catch (DomainCheckerException e) {
            vertexBits.remove(target);
            throw e;
        }
    }


    /**
     * Unregister a vertex that was initially marked as used as intermediate vertex.
     *
     * @param vertexPlacementSize the number of source graph vertices placed
     * @param vertex              the vertex that is being unregistered
     * @throws IllegalArgumentException thrown when the vertex was not occupied for routing
     */
    void releaseRouting(int vertexPlacementSize, int vertex) {
        assert isOccupiedRouting(vertex);
        if (!isOccupiedRouting(vertex)) {
            throw new IllegalArgumentException("Cannot release a vertex that was never occupied (for routing purposes): " + vertex);
        }
        routingBits.remove(vertex);
        domainChecker.afterReleaseEdge(vertexPlacementSize, vertex);
    }

    /**
     * Unregister a vertex that was initially marked as used for vertex-on-vertex matching
     *
     * @param vertexPlacementSize the number of source graph vertices placed
     * @param vertex              the vertex that is being unregistered
     * @throws IllegalArgumentException thrown when the vertex was not occupied for vertex-on-vertex matching
     */
    public void releaseVertex(int vertexPlacementSize, int vertex) {
        if (!isOccupiedVertex(vertex)) {
            throw new IllegalArgumentException("Cannot release a vertex that was never occupied (for vertex-on-vertex purposes): " + vertex);
        }
        assert vertexBits.contains(vertex);
        vertexBits.remove(vertex);
        domainChecker.afterReleaseVertex(vertexPlacementSize, vertex);
    }

    /**
     * Returns whether some target graph vertex is currently occupied as intermediate vertex
     *
     * @param vertex the vertex to query
     * @return whether in the current matching this vertex is used as intermediate vertex
     */
    public boolean isOccupiedRouting(int vertex) {
        return routingBits.contains(vertex);
    }

    /**
     * Returns whether some target graph vertex is currently occupied as vertex-on-vertex matching vertex
     *
     * @param vertex the vertex to query
     * @return whether in the current matching this vertex is used for vertex-on-vertex matching
     */
    public boolean isOccupiedVertex(Integer vertex) {
        return vertexBits.contains(vertex);
    }

    public boolean isOccupied(int vertex) {
        return isOccupiedRouting(vertex) || isOccupiedVertex(vertex);
    }

    @Override
    public String toString() {
        TIntSet myList = new TIntHashSet();
        myList.addAll(IntStream.range(0, routingBits.size()).filter(this.routingBits::contains).boxed().collect(Collectors.toSet()));
        myList.addAll(this.vertexBits);
        //assert myList.stream().allMatch(this::isOccupied);
        TIntList res = new TIntArrayList(myList);
        res.sort();
        return res.toString();

    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlobalOccupation that = (GlobalOccupation) o;
        return routingBits.equals(that.routingBits) &&
                vertexBits.equals(that.vertexBits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routingBits, vertexBits);
    }

    /**
     * Returns the set of target graph vertices that have been marked as being used as intermediate vertex.
     *
     * @return the set of vertices being used as intermediate vertex.
     */
    public TIntSet getRoutingOccupied() {
        return TCollections.unmodifiableSet(this.routingBits);
    }


}
