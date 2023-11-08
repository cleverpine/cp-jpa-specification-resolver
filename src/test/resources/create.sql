CREATE TABLE movies (
                        id BIGINT NOT NULL,
                        title VARCHAR(255) NOT NULL,
                        genre_id BIGINT NOT NULL,
                        PRIMARY KEY (id)
);

CREATE TABLE genres (
                        id BIGINT NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id)
);

CREATE TABLE actors (
                        id BIGINT NOT NULL,
                        first_name VARCHAR(255) NOT NULL,
                        last_name VARCHAR(255) NOT NULL,
                        PRIMARY KEY (id)
);

CREATE TABLE movies_actors (
                               movie_id BIGINT NOT NULL,
                               actor_id BIGINT NOT NULL,
                               PRIMARY KEY (movie_id, actor_id)
);

ALTER TABLE movies
    ADD FOREIGN KEY (genre_id) REFERENCES genres(id);

ALTER TABLE movies_actors
    ADD FOREIGN KEY (movie_id) REFERENCES movies(id);

ALTER TABLE movies_actors
    ADD FOREIGN KEY (actor_id) REFERENCES actors(id);
