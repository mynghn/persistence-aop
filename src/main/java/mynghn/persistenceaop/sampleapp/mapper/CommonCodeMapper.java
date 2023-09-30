package mynghn.persistenceaop.sampleapp.mapper;

import mynghn.persistenceaop.mapper.base.CrudWithHistoryMapper;
import mynghn.persistenceaop.mapper.base.HistoryMapper;
import mynghn.persistenceaop.sampleapp.entity.CommonCode;
import mynghn.persistenceaop.sampleapp.entity.id.CommonCodeId;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonCodeMapper extends CrudWithHistoryMapper<CommonCode, CommonCodeId>,
        HistoryMapper<CommonCode> {

    @Override
    default Class<CommonCode> getEntityType() {
        return CommonCode.class;
    }
}
