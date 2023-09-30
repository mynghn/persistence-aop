package mynghn.persistenceaop.sampleapp.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum CommonCodeGroup {
    TODO_LIST_TYPE("G01", "TODO_LIST_TYPE"),
    TODO_ITEM_TYPE("G02", "TODO_ITEM_TYPE");

    private final String id;
    private final String name;

    CommonCodeGroup(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
