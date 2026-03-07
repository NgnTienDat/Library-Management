package com.ou.oulib.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ErrorCode {
    // App: 1XXX
    UNCATEGORIZED_ERROR(1000, "Uncategorized Error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_FOUND(1001, "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTED(1002, "User already exists", HttpStatus.CONFLICT),
    UNAUTHENTICATED(1003, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1004, "You dont have permission", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED(1006, "Your account is locked", HttpStatus.BAD_REQUEST),
    INVALID_YEAR_FORMAT(1009, "Invalid year", HttpStatus.BAD_REQUEST),
    TOO_MANY_REQUESTS(1012, "Too many requests", HttpStatus.TOO_MANY_REQUESTS),
    FILE_TOO_LARGE(1024, "File size is too large!", HttpStatus.BAD_REQUEST),
    BOOK_ALREADY_EXISTS(1027, "Book with this ISBN already exists", HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND(1028, "Category not found", HttpStatus.NOT_FOUND),
    AUTHOR_NOT_FOUND(1029, "Author not found", HttpStatus.NOT_FOUND),
    BOOK_NOT_FOUND(1030, "Book not found", HttpStatus.NOT_FOUND),
    BORROW_QUOTA_EXCEEDED(1031, "You have reached your borrowing limit", HttpStatus.BAD_REQUEST),
    NO_AVAILABLE_COPIES(1032, "No available copies for this book", HttpStatus.BAD_REQUEST),
    ALREADY_BORROWING(1033, "You are already borrowing this book", HttpStatus.CONFLICT),
    BORROW_RECORD_NOT_FOUND(1034, "No active borrow record found for this book", HttpStatus.NOT_FOUND),
    BORROW_EXPIRED(1035, "Your borrow period has expired", HttpStatus.FORBIDDEN),
    BOOK_CONTENT_NOT_AVAILABLE(1036, "This book has no digital content available", HttpStatus.NOT_FOUND),
    PERMISSION_DENIED(1037, "You do not have permission to perform this action", HttpStatus.FORBIDDEN),
    COPY_IDS_COUNT_MISMATCH(1038, "The number of copy IDs does not match the total copies", HttpStatus.BAD_REQUEST),
    BARCODE_ALREADY_EXISTS(1039, "One or more barcodes already exist", HttpStatus.CONFLICT),
    USER_HAS_OVERDUE_BOOK(1040, "You have overdue books. Please return them before borrowing new ones.", HttpStatus.FORBIDDEN),
    USER_INACTIVE(1041, "User account is inactivated", HttpStatus.FORBIDDEN),

    // Validation: 2XXX
    INVALID_MESSAGE_KEY(2001, "Invalid Message Key", HttpStatus.BAD_REQUEST),
    NOT_BLANK(2002, "Cannot blank this field", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(2003, "Invalid email address" , HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(2004, "Invalid password", HttpStatus.BAD_REQUEST),
    INVALID_NAME(2005, "Invalid name account", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_TYPE(2006, "Invalid image type", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(2007, "Invalid file type", HttpStatus.BAD_REQUEST),
    INVALID_TOTAL_COPIES(2008, "Total copies must be greater than 0", HttpStatus.BAD_REQUEST),
    INVALID_AUTHOR_NAME(2009, "Invalid author name", HttpStatus.BAD_REQUEST),
    ;

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
