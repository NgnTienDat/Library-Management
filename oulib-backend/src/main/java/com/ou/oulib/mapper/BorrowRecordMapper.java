package com.ou.oulib.mapper;

import com.ou.oulib.dto.response.BorrowRecordResponse;
import com.ou.oulib.entity.BorrowRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BorrowRecordMapper {

    @Mapping(target = "barcode", source = "bookCopy.barcode")
    BorrowRecordResponse toBorrowRecordResponse(BorrowRecord record);
}
