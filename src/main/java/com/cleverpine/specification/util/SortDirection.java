package com.cleverpine.specification.util;

public enum SortDirection {

    ASC,
    DESC;

    public boolean isAscending() {
        return this.equals(ASC);
    }

    public boolean isDescending() {
        return this.equals(DESC);
    }

}
