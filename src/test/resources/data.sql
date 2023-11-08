INSERT INTO genres (id, name)
VALUES
    ( 1, 'Comedy' ),
    ( 2, 'Horror' ),
    ( 3, 'Action' );

INSERT INTO movies(id, title, genre_id)
VALUES
    ( 1, 'Fast and Furious', 3 ),
    ( 2, 'IT', 2 ),
    ( 3, 'Deadpool', 1 ),
    ( 4, 'Deadpool 2', 1 );

INSERT INTO actors(id, first_name, last_name)
VALUES
    ( 1, 'Vin', 'Diesel' ),
    ( 2, 'Dwayne', 'Johnson' ),
    ( 3, 'Jason', 'Statham' ),
    ( 4, 'Kurt', 'Russel' ),
    ( 5, 'Jaeden', 'Martell' ),
    ( 6, 'Wyatt', 'Oleff' ),
    ( 7, 'Ryan', 'Reynolds' ),
    ( 8, 'Morena', 'Baccarin' );

INSERT INTO movies_actors(movie_id, actor_id)
VALUES
    ( 1, 1 ),
    ( 1, 2 ),
    ( 1, 3 ),
    ( 1, 4 ),
    ( 2, 5 ),
    ( 2, 6 ),
    ( 3, 7 ),
    ( 3, 8 ),
    ( 4, 7 ),
    ( 4, 8 );