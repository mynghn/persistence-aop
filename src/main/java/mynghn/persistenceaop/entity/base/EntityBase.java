package mynghn.persistenceaop.entity.base;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public abstract class EntityBase implements CreateStamp, UpdateStamp, SoftDeleteEntity {

    protected Boolean isDeleted;

    protected LocalDateTime createdAt;
    protected String createdBy;

    protected LocalDateTime lastModifiedAt;
    protected String lastModifiedBy;
}
