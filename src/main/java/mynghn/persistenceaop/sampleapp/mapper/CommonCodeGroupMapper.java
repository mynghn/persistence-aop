package mynghn.persistenceaop.sampleapp.mapper;

import mynghn.persistenceaop.mapper.base.CrudWithHistoryMapper;
import mynghn.persistenceaop.mapper.base.HistoryMapper;
import mynghn.persistenceaop.sampleapp.entity.CommonCodeGroup;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonCodeGroupMapper extends CrudWithHistoryMapper<CommonCodeGroup, String>,
        HistoryMapper<CommonCodeGroup> {

    @Override
    default Class<CommonCodeGroup> getEntityType() {
        return CommonCodeGroup.class;
    }
}
