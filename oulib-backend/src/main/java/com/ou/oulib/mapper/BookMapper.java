package com.ou.oulib.mapper;

import com.ou.oulib.dto.request.BookCreationRequest;
import com.ou.oulib.dto.request.BookUpdateRequest;
import com.ou.oulib.dto.response.BookDetailResponse;
import com.ou.oulib.dto.response.BookResponse;
import com.ou.oulib.entity.Author;
import com.ou.oulib.entity.Book;
import com.ou.oulib.entity.BookCopy;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "availableCopies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toBook(BookCreationRequest bookCreationRequest);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "authorNames", source = "authors", qualifiedByName = "mapAuthorNames")
    BookResponse toBookResponse(Book book);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "authorNames", source = "authors", qualifiedByName = "mapAuthorNames")
    @Mapping(target = "copoies", source = "copies", qualifiedByName = "mapCopyBarcodes")
    BookDetailResponse toBookDetailResponse(Book book);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isbn", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "availableCopies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void partialUpdate(BookUpdateRequest request, @MappingTarget Book book);

    @Named("mapAuthorNames")
    default List<String> mapAuthorNames(List<Author> authors) {
        if (authors == null) return List.of();
        return authors.stream().map(Author::getName).toList();
    }

    @Named("mapCopyBarcodes")
    default List<String> mapCopyBarcodes(List<BookCopy> copies) {
        if (copies == null) return List.of();
        return copies.stream().map(BookCopy::getBarcode).toList();
    }
}
