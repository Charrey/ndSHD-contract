package com.charrey.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.*;

public class Vertex implements Serializable, Comparable<Vertex> {

    private static int counter = 0;
    private final int counterValue;
    private Graph<Vertex, DefaultEdge> graph;
    private int data;
    protected Map<String, Set<Attribute>> attributes = new HashMap<>();

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public Vertex(int data) {
        this.data = data;
        counterValue = ++counter;
    }

    public void setGraph(Graph<Vertex, DefaultEdge> graph) {
        if (this.graph == null) {
            this.graph = graph;
        } else {
            throw new RuntimeException("Graph already set");
        }
    }

    public Vertex addLabel(String label) {
        attributes.putIfAbsent("label", new HashSet<>());
        attributes.get("label").add(new DefaultAttribute<>(label, AttributeType.STRING));
        return this;
    }

    @Override
    public String toString() {
        return "[" + data + "]";
    }

    public Set<Attribute> getLabels() {
        return attributes.getOrDefault("label", Collections.emptySet());
    }

    public void setData(int newData) {
        data = newData;
    }

    public Graph<Vertex, DefaultEdge> getGraph() {
        return graph;
    }

    @Override
    public int compareTo(@Nonnull Vertex o) {
        return Integer.compare(data, o.data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex that = (Vertex) o;
        return counterValue == that.counterValue;
    }

    @Override
    public int hashCode() {
        return counterValue;
    }

    public int data() {
        return data;
    }
}
