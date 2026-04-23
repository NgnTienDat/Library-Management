package com.ou.oulib.mapper;

import com.ou.oulib.dto.response.BorrowRecordDetailResponse;
import com.ou.oulib.dto.response.BorrowRecordResponse;
import com.ou.oulib.entity.BorrowRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BorrowRecordMapper {

    @Mapping(target = "borrowerId", source = "borrower.id")
    @Mapping(target = "barcode", source = "bookCopy.barcode")
    @Mapping(target = "bookTitle", source = "bookCopy.book.title")
    @Mapping(target = "thumbnailUrl", source = "bookCopy.book.thumbnailUrl")
    BorrowRecordResponse toBorrowRecordResponse(BorrowRecord record);

    @Mapping(target = "borrowerId", source = "borrower.id")
    @Mapping(target = "borrowerFullName", source = "borrower.fullName")
    @Mapping(target = "borrowerEmail", source = "borrower.email")
    @Mapping(target = "barcode", source = "bookCopy.barcode")
    @Mapping(target = "borrowDate", source = "createdAt")
    BorrowRecordDetailResponse toBorrowRecordDetailResponse(BorrowRecord record);
}
