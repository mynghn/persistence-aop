package mynghn.persistenceaop.entity.base;

import java.time.LocalDateTime;

public interface UpdateStamp {

    LocalDateTime getLastModifiedAt();
    void setLastModifiedAt(LocalDateTime time);

    String getLastModifiedBy();
    void setLastModifiedBy(String username);
}
