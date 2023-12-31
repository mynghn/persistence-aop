package mynghn.persistenceaop.sampleapp.entity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mynghn.persistenceaop.entity.base.EntityBase;

@Getter
@SuperBuilder
public class TodoList extends EntityBase {

    private final String id;

    private final String title;

    private final String typeCodeValue;
}
