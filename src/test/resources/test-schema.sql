DROP TABLE IF EXISTS PUBLIC.todo_item_history;
DROP TABLE IF EXISTS PUBLIC.todo_item;
DROP TABLE IF EXISTS PUBLIC.todo_list_history;
DROP TABLE IF EXISTS PUBLIC.todo_list;

CREATE TABLE PUBLIC.todo_list
(
    id    char(10) PRIMARY KEY,
    title varchar(40) NOT NULL
);

CREATE TABLE PUBLIC.todo_list_history
(
    todo_list_id        char(10)    NOT NULL,
    history_sequence_no integer     NOT NULL,
    todo_list_title     varchar(40) NOT NULL,
    PRIMARY KEY (todo_list_id, history_sequence_no),
    FOREIGN KEY (todo_list_id) REFERENCES PUBLIC.todo_list (id)
);

CREATE TABLE PUBLIC.todo_item
(
    id           char(10) PRIMARY KEY,
    title        varchar(40) NOT NULL,
    description  varchar(1000),
    due_date     date,
    todo_list_id char(10)    NOT NULL,
    FOREIGN KEY (todo_list_id) REFERENCES PUBLIC.todo_list (id)
);

CREATE TABLE IF NOT EXISTS PUBLIC.todo_item_history
(
    todo_item_id           char(10)    NOT NULL,
    history_sequence_no    integer     NOT NULL,
    todo_item_title        varchar(40) NOT NULL,
    todo_item_description  varchar(1000),
    todo_item_due_date     date,
    todo_item_todo_list_id char(10)    NOT NULL,
    PRIMARY KEY (todo_item_id, history_sequence_no),
    FOREIGN KEY (todo_item_id) REFERENCES PUBLIC.todo_item (id)
);
