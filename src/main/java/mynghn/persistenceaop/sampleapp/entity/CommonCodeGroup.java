package mynghn.persistenceaop.sampleapp.entity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mynghn.persistenceaop.entity.base.EntityBase;

@Getter
@SuperBuilder
public class CommonCodeGroup extends EntityBase {

    private final String id;
    private final String name;
}
