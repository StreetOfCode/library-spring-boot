create sequence author_id_seq start with 1 increment by 1;
create sequence category_id_seq start with 1 increment by 1;
create sequence book_id_seq start with 1 increment by 1;

-- id, name, surname
INSERT INTO author VALUES
(next value for author_id_seq, 'J.K.', 'Rowling'),
(next value for author_id_seq, 'J.R.R.', 'Tolkien'),
(next value for author_id_seq, 'Daniel', 'Kahneman'),
(next value for author_id_seq, 'Robert C.', 'Martin');
-- id, name, description
INSERT INTO category VALUES
(next value for category_id_seq, 'Fantasy', 'Fiction literature'),
(next value for category_id_seq, 'Nonfiction', 'Nonfiction literature');
-- id, name, description, created_at, author_id, category_id
INSERT INTO book VALUES
(next value for book_id_seq, 1, 1, 'Harry Potter', 'Story about young wizard', CURRENT_TIMESTAMP),
(next value for book_id_seq, 2, 1, 'The Fellowship of the ring', 'One Ring to rule them all', CURRENT_TIMESTAMP),
(next value for book_id_seq, 3, 2, 'Thinking, Fast and Slow', 'System 1 and System 2', CURRENT_TIMESTAMP),
(next value for book_id_seq, 4, null, 'Clean Code', 'How to write clean code', CURRENT_TIMESTAMP);


