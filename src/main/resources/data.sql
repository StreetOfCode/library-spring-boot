create sequence author_id_seq start with 1 increment by 1;
create sequence category_id_seq start with 1 increment by 1;
create sequence book_id_seq start with 1 increment by 1;

-- id, name, surname
INSERT INTO author VALUES
(author_id_seq.nextval, 'J.K.', 'Rowling'),
(author_id_seq.nextval, 'J.R.R.', 'Tolkien'),
(author_id_seq.nextval, 'Daniel', 'Kahneman'),
(author_id_seq.nextval, 'Robert C.', 'Martin');
-- id, name, description
INSERT INTO category VALUES
(category_id_seq.nextval, 'Fantasy', 'Fiction literature'),
(category_id_seq.nextval, 'Nonfiction', 'Nonfiction literature');
-- id, name, description, created_at, author_id, category_id
INSERT INTO book VALUES
(book_id_seq.nextval, 1, 1, 'Harry Potter', 'Story about young wizard', CURRENT_TIMESTAMP),
(book_id_seq.nextval, 2, 1, 'The Fellowship of the ring', 'One Ring to rule them all', CURRENT_TIMESTAMP),
(book_id_seq.nextval, 3, 2, 'Thinking, Fast and Slow', 'System 1 and System 2', CURRENT_TIMESTAMP),
(book_id_seq.nextval, 4, null, 'Clean Code', 'How to write clean code', CURRENT_TIMESTAMP);


