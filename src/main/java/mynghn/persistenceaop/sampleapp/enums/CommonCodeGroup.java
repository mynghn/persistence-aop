package mynghn.persistenceaop.sampleapp.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum CommonCodeGroup {
    ;

    private final String id;
    private final String name;

    CommonCodeGroup(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
