package mynghn.persistenceaop.entity.base;

public interface SoftDeleteEntity {

    Boolean getIsDeleted();

    void setIsDeleted(Boolean isDeleted);
}
