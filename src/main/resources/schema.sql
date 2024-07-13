CREATE TABLE IF NOT EXISTS users (
   uid INT NOT NULL,
   nickname varchar(250) NOT NULL,
   username varchar(250) NOT NULL,
   password varchar(250) NOT NULL,
   user_role INT NOT NULL,
   signup_date TIMESTAMP NOT NULL,
   PRIMARY KEY (uid)
);

CREATE TABLE IF NOT EXISTS room (
   rid INT NOT NULL,
   title varchar(250) NOT NULL,
   is_private BIT NOT NULL,
   password varchar(250),
   owner INT NOT NULL,
   signup_date TIMESTAMP NOT NULL,
   PRIMARY KEY (rid),
   FOREIGN KEY (owner) REFERENCES users
);

CREATE TABLE IF NOT EXISTS join_user_room (
    uid INT NOT NULL,
    rid INT NOT NULL,
    PRIMARY KEY (uid, rid),
    FOREIGN KEY (uid) REFERENCES users,
    FOREIGN KEY (rid) REFERENCES room
)