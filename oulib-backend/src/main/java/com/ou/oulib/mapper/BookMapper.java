package com.ou.oulib.mapper;

import com.ou.oulib.dto.request.BookCreationRequest;
import com.ou.oulib.dto.response.BookResponse;
import com.ou.oulib.entity.Author;
import com.ou.oulib.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    @Mapping(target = "contentKey", ignore = true)
    @Mapping(target = "thumbnailKey", ignore = true)
    @Mapping(target = "availableCopies", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toBook(BookCreationRequest bookCreationRequest);

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "authorNames", source = "authors", qualifiedByName = "mapAuthorNames")
    BookResponse toBookResponse(Book book);

    @Named("mapAuthorNames")
    default List<String> mapAuthorNames(List<Author> authors) {
        if (authors == null) return List.of();
        return authors.stream().map(Author::getName).toList();
    }
}
