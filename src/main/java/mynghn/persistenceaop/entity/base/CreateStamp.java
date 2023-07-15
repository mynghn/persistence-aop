package mynghn.persistenceaop.entity.base;

import java.time.LocalDateTime;

public interface CreateStamp {

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime time);

    String getCreatedBy();
    void setCreatedBy(String username);
}
