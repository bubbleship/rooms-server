CREATE TABLE IF NOT EXISTS users (
   nickname varchar(250) NOT NULL,
   username varchar(250) NOT NULL,
   password varchar(250) NOT NULL,
   role INT NOT NULL,
   signup_date TIMESTAMP NOT NULL,
   PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS room (
   rid INT NOT NULL,
   title varchar(250) NOT NULL,
   is_private BIT NOT NULL,
   password varchar(250),
   owner varchar(250) NOT NULL,
   creation_date TIMESTAMP NOT NULL,
   description VARCHAR(MAX) NOT NULL,
   PRIMARY KEY (rid),
   FOREIGN KEY (owner) REFERENCES users
);

CREATE TABLE IF NOT EXISTS join_user_room (
    username varchar(250) NOT NULL,
    rid INT NOT NULL,
    PRIMARY KEY (username, rid),
    FOREIGN KEY (username) REFERENCES users,
    FOREIGN KEY (rid) REFERENCES room
);

CREATE TABLE IF NOT EXISTS message (
    rid INT NOT NULL,
    index INT NOT NULL,
    type INT NOT NULL,
    sender varchar(250) NOT NULL,
    content VARCHAR(MAX) NOT NULL,
    send_date TIMESTAMP NOT NULL,
    PRIMARY KEY (rid, index),
    FOREIGN KEY (rid) REFERENCES room,
    FOREIGN KEY (sender) REFERENCES users
);