package com.ou.oulib.specification;

import com.ou.oulib.dto.request.BookFilterRequest;
import com.ou.oulib.entity.Author;
import com.ou.oulib.entity.Book;
import com.ou.oulib.entity.Category;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class BookSpecification {

    private BookSpecification() {
    }

    public static Specification<Book> build(BookFilterRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.distinct(true);

            // Khai báo Join dùng chung
            Join<Book, Category> categoryJoin = null;
            Join<Book, Author> authorJoin = null;

            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String pattern = "%" + request.getKeyword().toLowerCase() + "%";

                // Khởi tạo Join khi cần
                categoryJoin = root.join("category", JoinType.LEFT);
                authorJoin = root.join("authors", JoinType.LEFT);

                Predicate keywordPredicate = cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(categoryJoin.get("name")), pattern),
                        cb.like(cb.lower(authorJoin.get("name")), pattern)
                );
                predicates.add(keywordPredicate);
            }

            if (request.getCategoryId() != null && !request.getCategoryId().isBlank()) {
                predicates.add(cb.equal(root.get("category").get("id"), request.getCategoryId()));
            }

            if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
                if (authorJoin == null) {
                    authorJoin = root.join("authors", JoinType.LEFT);
                }
                predicates.add(authorJoin.get("id").in(request.getAuthorIds()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


//    public static Specification<Book> build(BookFilterRequest request) {
//        return (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>(); // danh sach các điều kiện tìm kiếm
//            query.distinct(true); // tránh trùng lặp do join nhiều-nhiều với tác giả
//
//            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
//                String pattern = "%" + request.getKeyword().toLowerCase() + "%";
//                Join<Book, Category> categoryJoin = root.join("category", JoinType.LEFT); // join với bảng category để tìm kiếm theo tên thể loại
//                Join<Book, Author> authorJoin = root.join("authors", JoinType.LEFT); // join với bảng author để tìm kiếm theo tên tác giả
//
//                Predicate keywordPredicate = cb.or(
//                        cb.like(cb.lower(root.get("title")), pattern), // tìm kiếm theo tiêu đề sách
//                        cb.like(cb.lower(categoryJoin.get("name")), pattern), // tìm kiếm theo tên thể loại
//                        cb.like(cb.lower(authorJoin.get("name")), pattern) // tìm kiếm theo tên tác giả
//                );
//                predicates.add(keywordPredicate); // thêm điều kiện tìm kiếm theo từ khóa vào danh sách điều kiện
//            }
//
//            if (request.getCategoryId() != null && !request.getCategoryId().isBlank()) {
//                // nếu có categoryId trong request, thêm điều kiện tìm kiếm theo categoryId
//                predicates.add(cb.equal(root.get("category").get("id"), request.getCategoryId()));
//            }
//
//            if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
//                // nếu có authorIds trong request, thêm điều kiện tìm kiếm theo danh sách authorIds
//                Join<Book, Author> authorJoin = root.join("authors", JoinType.LEFT);
//                predicates.add(authorJoin.get("id").in(request.getAuthorIds()));
//            }
//
//            // kết hợp tất cả các điều kiện tìm kiếm bằng AND và trả về
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//    }
}
