DROP TABLE IF EXISTS PUBLIC.todo_item_history;
DROP TABLE IF EXISTS PUBLIC.todo_item;
DROP TABLE IF EXISTS PUBLIC.todo_list_history_sequence;
DROP TABLE IF EXISTS PUBLIC.todo_list_history;
DROP TABLE IF EXISTS PUBLIC.todo_list;
DROP TABLE IF EXISTS PUBLIC.todo_list_id_sequence;

CREATE TABLE PUBLIC.todo_list
(
    id               char(10) PRIMARY KEY,
    title            varchar(40) NOT NULL,
    is_deleted       boolean     NOT NULL default false,
    created_at       timestamp   NOT NULL default current_timestamp,
    created_by       varchar(20) NOT NULL default 'tester',
    last_modified_at timestamp   NOT NULL default current_timestamp,
    last_modified_by varchar(20) NOT NULL default 'tester'
);

CREATE TABLE PUBLIC.todo_list_id_sequence
(
    date        date    NOT NULL,
    sequence_no integer NOT NULL,
    PRIMARY KEY (date, sequence_no)
);


CREATE TABLE PUBLIC.todo_list_history
(
    todo_list_id        char(10)    NOT NULL,
    history_sequence_no integer     NOT NULL,
    todo_list_title     varchar(40) NOT NULL,
    is_deleted          boolean     NOT NULL default false,
    created_at          timestamp   NOT NULL default current_timestamp,
    created_by          varchar(20) NOT NULL default 'tester',
    last_modified_at    timestamp   NOT NULL default current_timestamp,
    last_modified_by    varchar(20) NOT NULL default 'tester',
    PRIMARY KEY (todo_list_id, history_sequence_no),
    FOREIGN KEY (todo_list_id) REFERENCES PUBLIC.todo_list (id)
);

CREATE TABLE PUBLIC.todo_list_history_sequence
(
    todo_list_id char(10) NOT NULL,
    sequence_no  integer  NOT NULL,
    PRIMARY KEY (todo_list_id, sequence_no),
    FOREIGN KEY (todo_list_id) REFERENCES PUBLIC.todo_list (id)
);

CREATE TABLE PUBLIC.todo_item
(
    id               char(10) PRIMARY KEY,
    title            varchar(40) NOT NULL,
    description      varchar(1000),
    due_date         date,
    todo_list_id     char(10)    NOT NULL,
    is_deleted       boolean     NOT NULL default false,
    created_at       timestamp   NOT NULL default current_timestamp,
    created_by       varchar(20) NOT NULL default 'tester',
    last_modified_at timestamp   NOT NULL default current_timestamp,
    last_modified_by varchar(20) NOT NULL default 'tester',
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
    is_deleted             boolean     NOT NULL default false,
    created_at             timestamp   NOT NULL default current_timestamp,
    created_by             varchar(20) NOT NULL default 'tester',
    last_modified_at       timestamp   NOT NULL default current_timestamp,
    last_modified_by       varchar(20) NOT NULL default 'tester',
    PRIMARY KEY (todo_item_id, history_sequence_no),
    FOREIGN KEY (todo_item_id) REFERENCES PUBLIC.todo_item (id)
);
