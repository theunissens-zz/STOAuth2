CREATE TABLE IF NOT EXISTS client (id serial primary key,
                                  name varchar(64),
                                  clientid varchar(256),
                                  secret varchar(256),
                                  type varchar(64),
                                  url varchar(256),
                                  redirecturl varchar(256),
                                  description text);