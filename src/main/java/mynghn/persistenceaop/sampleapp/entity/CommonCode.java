package mynghn.persistenceaop.sampleapp.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import mynghn.persistenceaop.entity.base.EntityBase;

@Getter
@ToString
@SuperBuilder
public class CommonCode extends EntityBase {

    private final String groupId;
    private final String value;
    private final String name;
}
