-- id, name, surname
INSERT INTO author VALUES
(1, 'J.K.', 'Rowling'),
(2, 'J.R.R.', 'Tolkien'),
(3, 'Daniel', 'Kahneman'),
(4, 'Robert C.', 'Martin');
-- id, name, description
INSERT INTO category VALUES
(1, 'Fantasy', 'Fiction literature'),
(2, 'Nonfiction', 'Nonfiction literature');
-- id, name, description, created_at, author_id, category_id
INSERT INTO book VALUES
(1, 'Harry Potter', 'Story about young wizard', CURRENT_TIMESTAMP, 1, 1),
(2, 'The Fellowship of the ring', 'One Ring to rule them all', CURRENT_TIMESTAMP, 2, 1),
(3, 'Thinking, Fast and Slow', 'System 1 and System 2', CURRENT_TIMESTAMP, 3, 2),
(4, 'Clean Code', 'How to write clean code', CURRENT_TIMESTAMP, 4, null);


