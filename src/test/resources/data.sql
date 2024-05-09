-- This file allows us to load static data into the test database before tests are run.

-- Passwords are in the format: Password<UserLetter>123. Unless specified otherwise.
-- Encrypted using https://www.javainuse.com/onlineBcrypt
INSERT INTO local_user (username, email, password, bio, profile_picture, following, followers)
VALUES ('UserA', 'UserA@junit.com','$2a$10$H5DP8/qcsdvuMPZnW16.RurDodbZ5aLGnwwZQeEIyrVTMnHo4L2Ge', 'UserA', 'google.com/userA', 0, 0),
       ('UserB', 'UserB@junit.com','$2a$10$g8w2fuFp1P5s1Qy/VUiu3O0.IAlKIGOdKAqr5TTpWnilyBCLVVmRS', 'UserB', 'google.com/userB', 0, 0),
       ('UserC', 'UserC@junit.com','$2a$10$MsWbttCPPAZNZ491L4sQ4exmjd/PPIteXint5LvLPB9gLcqy7z3c6', 'UserC', 'google.com/userC', 0, 0);

INSERT INTO post(data_url, creation_timestamp, user_id, location, caption, likes_count, comments_count)
VALUES ('www.instagram.com/userA/13235', '2024-04-29 14:17:31.770000', '1', 'New York, USA', 'Hi', 0, 0),
       ('www.instagram.com/userA/1322335', '2024-04-29 14:18:31.770000', '1', '', '', 0, 0),
       ('www.instagram.com/userA/8631', '2024-04-29 14:19:31.770000', '1', 'Los Angeles, USA', '', 0, 0),
       ('www.instagram.com/userB/132235', '2024-04-29 14:14:31.770000', '2', 'Warsaw, Poland', 'Hello', 0, 0),
       ('www.instagram.com/userB/6235', '2024-04-29 14:15:31.770000', '2', '', '', 0, 0);

INSERT INTO comment(post_id, local_user_id, text, creation_timestamp)
VALUES (4, 1, 'I love this picture!', '2024-05-01 14:45:30.770000'),
       (4, 3, 'I hate that(', '2024-05-01 14:45:30.770000'),
       (2, 3, 'Wow, very nice!', '2024-05-01 14:45:30.770000');
