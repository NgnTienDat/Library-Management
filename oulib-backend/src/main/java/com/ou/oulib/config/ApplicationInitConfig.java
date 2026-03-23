package com.ou.oulib.config;


import com.ou.oulib.entity.Author;
import com.ou.oulib.entity.Book;
import com.ou.oulib.entity.BookCopy;
import com.ou.oulib.entity.Category;
import com.ou.oulib.entity.User;
import com.ou.oulib.enums.BookCopyStatus;
import com.ou.oulib.enums.UserRole;
import com.ou.oulib.repository.AuthorRepository;
import com.ou.oulib.repository.BookRepository;
import com.ou.oulib.repository.CategoryRepository;
import com.ou.oulib.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Profile("api")
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    static final int REQUIRED_CATEGORIES = 5;
    static final int REQUIRED_AUTHORS = 10;
    static final int REQUIRED_BOOKS = 10;
    static final int COPIES_PER_BOOK = 5;

    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${app.admin.password}")
    String adminPassword;

    @NonFinal
    @Value("${app.admin.email}")
    String adminEmail;

    @NonFinal
    @Value("${app.librarian.password}")
    String librarianPassword;

    @NonFinal
    @Value("${app.librarian.email}")
    String librarianEmail;

        private static final List<String> CATEGORY_NAMES = List.of(
            "Fiction",
            "Science",
            "Technology",
            "History",
            "Business"
        );

        private static final List<AuthorSeed> AUTHOR_SEEDS = List.of(
            new AuthorSeed("George Orwell", "Known for dystopian and political fiction"),
            new AuthorSeed("Jane Austen", "Classic novelist of social commentary"),
            new AuthorSeed("Haruki Murakami", "Contemporary literary fiction"),
            new AuthorSeed("Isaac Asimov", "Science fiction and popular science writer"),
            new AuthorSeed("Carl Sagan", "Astronomy communicator and author"),
            new AuthorSeed("Donald Knuth", "Computer science pioneer"),
            new AuthorSeed("Martin Fowler", "Software architecture and refactoring expert"),
            new AuthorSeed("Yuval Noah Harari", "Historian and futurist"),
            new AuthorSeed("Peter Drucker", "Modern management thinker"),
            new AuthorSeed("Nassim Nicholas Taleb", "Risk and uncertainty researcher")
        );

        private static final List<BookSeed> BOOK_SEEDS = List.of(
            new BookSeed("9780000000001", "The Last Lantern", "A story about hope in uncertain times.", "Northwind Press", 320, "https://dummyimage.com/200x300/cccccc/000000&text=Book+1"),
            new BookSeed("9780000000002", "Quantum Gardens", "An accessible journey through modern science.", "Blue Orbit", 280, "https://dummyimage.com/200x300/cccccc/000000&text=Book+2"),
            new BookSeed("9780000000003", "Refactoring Rivers", "Practical lessons for maintainable software.", "CodeCraft House", 410, "https://dummyimage.com/200x300/cccccc/000000&text=Book+3"),
            new BookSeed("9780000000004", "Empire of Maps", "How historical maps shaped civilizations.", "Atlas Line", 350, "https://dummyimage.com/200x300/cccccc/000000&text=Book+4"),
            new BookSeed("9780000000005", "The Lean Library", "Business strategy for knowledge institutions.", "Crescent Publishing", 290, "https://dummyimage.com/200x300/cccccc/000000&text=Book+5"),
            new BookSeed("9780000000006", "Signal and Story", "Narratives behind scientific breakthroughs.", "Blue Orbit", 305, "https://dummyimage.com/200x300/cccccc/000000&text=Book+6"),
            new BookSeed("9780000000007", "Machines and Meaning", "Technology, ethics, and society.", "CodeCraft House", 360, "https://dummyimage.com/200x300/cccccc/000000&text=Book+7"),
            new BookSeed("9780000000008", "Chronicles of Trade", "The evolution of global business systems.", "Mercury Books", 332, "https://dummyimage.com/200x300/cccccc/000000&text=Book+8"),
            new BookSeed("9780000000009", "City of Paper Stars", "A literary fiction set in a changing city.", "Northwind Press", 274, "https://dummyimage.com/200x300/cccccc/000000&text=Book+9"),
            new BookSeed("9780000000010", "Pragmatic Histories", "Patterns from history for modern decisions.", "Atlas Line", 388, "https://dummyimage.com/200x300/cccccc/000000&text=Book+10")
        );

    @Bean
    ApplicationRunner applicationRunner(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            AuthorRepository authorRepository,
            BookRepository bookRepository
    ) {
        log.info("Initializing application.....");
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .role(UserRole.SYSADMIN)
                        .email(adminEmail)
                        .fullName("System Admin")
                        .password(passwordEncoder.encode(adminPassword))
                        .build();

                userRepository.save(admin);
                log.info("Created admin user");
            }

            if (!userRepository.existsByEmail(librarianEmail)) {
                User librarian = User.builder()
                        .role(UserRole.LIBRARIAN)
                        .email(librarianEmail)
                        .fullName("Librarian")
                        .password(passwordEncoder.encode(librarianPassword))
                        .build();

                userRepository.save(librarian);
                log.info("Created librarian user");
            }

            List<Category> categories = seedCategories(categoryRepository);
            List<Author> authors = seedAuthors(authorRepository);
            seedBooks(bookRepository, categories, authors);
        };
    }

    private List<Category> seedCategories(CategoryRepository categoryRepository) {
        long categoryCount = categoryRepository.count();
        if (categoryCount >= REQUIRED_CATEGORIES) {
            log.info("Skipping category initialization. Existing count: {}", categoryCount);
            return categoryRepository.findAll();
        }

        int created = 0;
        for (String name : CATEGORY_NAMES) {
            if (categoryRepository.findByName(name).isPresent()) {
                continue;
            }

            categoryRepository.save(Category.builder().name(name).build());
            created++;
        }

        log.info("Category initialization complete. Created: {}, Existing before: {}", created, categoryCount);
        return categoryRepository.findAll();
    }

    private List<Author> seedAuthors(AuthorRepository authorRepository) {
        long authorCount = authorRepository.count();
        if (authorCount >= REQUIRED_AUTHORS) {
            log.info("Skipping author initialization. Existing count: {}", authorCount);
            return authorRepository.findAll();
        }

        int created = 0;
        for (AuthorSeed authorSeed : AUTHOR_SEEDS) {
            if (authorRepository.findByName(authorSeed.name()).isPresent()) {
                continue;
            }

            authorRepository.save(
                    Author.builder()
                            .name(authorSeed.name())
                            .note(authorSeed.note())
                            .build()
            );
            created++;
        }

        log.info("Author initialization complete. Created: {}, Existing before: {}", created, authorCount);
        return authorRepository.findAll();
    }

    private void seedBooks(BookRepository bookRepository, List<Category> categories, List<Author> authors) {
        long bookCount = bookRepository.count();
        if (bookCount >= REQUIRED_BOOKS) {
            log.info("Skipping book initialization. Existing count: {}", bookCount);
            return;
        }

        if (categories.isEmpty()) {
            log.warn("Skipping book initialization because no categories are available");
            return;
        }

        if (authors.isEmpty()) {
            log.warn("Skipping book initialization because no authors are available");
            return;
        }

        int created = 0;
        int existingBefore = (int) bookCount;

        for (int i = 0; i < BOOK_SEEDS.size(); i++) {
            BookSeed bookSeed = BOOK_SEEDS.get(i);
            if (bookRepository.existsByIsbn(bookSeed.isbn())) {
                continue;
            }

            Book book = Book.builder()
                    .isbn(bookSeed.isbn())
                    .title(bookSeed.title())
                    .description(bookSeed.description())
                    .publisher(bookSeed.publisher())
                    .numberOfPages(bookSeed.numberOfPages())
                    .thumbnailUrl(bookSeed.thumbnailUrl())
                    .category(randomCategory(categories))
                    .authors(randomAuthors(authors))
                    .build();

            book.initializeCopies(COPIES_PER_BOOK);
            for (int copyIndex = 1; copyIndex <= COPIES_PER_BOOK; copyIndex++) {
                BookCopy copy = BookCopy.builder()
                        .barcode(buildBarcode(i + 1, copyIndex))
                        .status(BookCopyStatus.AVAILABLE)
                        .build();
                book.addCopy(copy);
            }

            bookRepository.save(book);
            created++;
        }

        log.info("Book initialization complete. Created: {}, Existing before: {}", created, existingBefore);
    }

    private Category randomCategory(List<Category> categories) {
        int index = ThreadLocalRandom.current().nextInt(categories.size());
        return categories.get(index);
    }

    private List<Author> randomAuthors(List<Author> authors) {
        int maxAuthors = Math.min(3, authors.size());
        int amount = ThreadLocalRandom.current().nextInt(1, maxAuthors + 1);

        List<Author> shuffled = new ArrayList<>(authors);
        Collections.shuffle(shuffled);
        return new ArrayList<>(shuffled.subList(0, amount));
    }

    private String buildBarcode(int bookIndex, int copyIndex) {
        return String.format("BOOK-%02d-COPY-%02d", bookIndex, copyIndex);
    }

    private record AuthorSeed(String name, String note) {
    }

    private record BookSeed(
            String isbn,
            String title,
            String description,
            String publisher,
            Integer numberOfPages,
            String thumbnailUrl
    ) {
    }
}
