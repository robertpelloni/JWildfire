package org.jwildfire.create.tina.dance;

public class FlamePropertyItem {
    private final String name;
    private final Object data;
    private final boolean leaf;

    public FlamePropertyItem(String name, Object data, boolean leaf) {
        this.name = name;
        this.data = data;
        this.leaf = leaf;
    }

    public String getName() {
        return name;
    }

    public Object getData() {
        return data;
    }

    public boolean isLeaf() {
        return leaf;
    }

    @Override
    public String toString() {
        return name;
    }
}
