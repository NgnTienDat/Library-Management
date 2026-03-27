# Add Book

```mermaid
sequenceDiagram
    autonumber
    actor Librarian
    participant UI
    participant Controller
    participant MySQL
    participant Cloudinary

    Librarian->>UI: Submit new book form (metadata + optional thumbnail)
    UI->>Controller: POST /api/v1/books
    Controller->>Controller: Validate role and core input rules
    Controller->>MySQL: Check ISBN/barcode uniqueness and references

    alt validation fails
        Controller-->>UI: Error response
        UI-->>Librarian: Show validation/business error
    else validation passes
        Controller->>MySQL: Create book and copy records

        alt thumbnail provided
            Controller->>Cloudinary: Upload thumbnail
            alt upload success
                Cloudinary-->>Controller: Image URL
                Controller->>MySQL: Save thumbnail URL
                Controller-->>UI: 201 Created
                UI-->>Librarian: Show created book
            else upload failure
                Cloudinary-->>Controller: Upload error
                Controller->>MySQL: Rollback/delete created book data
                Controller-->>UI: Error response
                UI-->>Librarian: Show failure
            end
        else no thumbnail
            Controller-->>UI: 201 Created
            UI-->>Librarian: Show created book
        end
    end
```

## Business Description

This operation allows a librarian to create a new book title with physical copy barcodes.
The flow validates copy counts, ISBN/barcode uniqueness, category existence, and author references before persisting data.
If an author id is absent, a new author is created; then the book and its copies are saved in MySQL in one transaction.
When a thumbnail is provided, the system uploads it to Cloudinary and stores the URL; on upload failure, the new database record is rolled back by deleting the inserted book.

 