CREATE TABLE
    IF NOT EXISTS _user (
        id BIGSERIAL PRIMARY KEY,
        username VARCHAR(30) NOT NULL,
        full_name VARCHAR(30),
        description VARCHAR(3000),
        is_profile_image_exist BOOLEAN NOT NULL DEFAULT false,
        role varchar(60) NOT NULL DEFAULT 'user' check (role in ('USER', 'ADMIN')),
        CONSTRAINT user_username_unique UNIQUE (username)
    );

-- auth.id upper bound on size is the max size for a cookie, which is 4K (4096 bytes)
CREATE TABLE
    IF NOT EXISTS auth (
        id varchar(4096) PRIMARY KEY,
        user_id BIGINT REFERENCES _user (id) ON DELETE CASCADE
    );

create table
    IF NOT EXISTS post (
        id BIGSERIAL PRIMARY KEY,
        creation_date_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        update_date_time TIMESTAMPTZ,
        description varchar(3000),
        user_id BIGINT NOT NULL REFERENCES _user (id) ON DELETE CASCADE,
        CONSTRAINT valid_update_date_time CHECK (creation_date_time <= update_date_time)
    );

create table
    IF NOT EXISTS _like (
        post_id BIGINT NOT NULL REFERENCES post (id) ON DELETE CASCADE,
        user_id BIGINT NOT NULL REFERENCES _user (id) ON DELETE CASCADE,
        PRIMARY KEY (user_id, post_id),
        CONSTRAINT user_post_unique UNIQUE (user_id, post_id)
    );

create table
    IF NOT EXISTS following (
        follower_id BIGINT NOT NULL REFERENCES _user (id) ON DELETE CASCADE,
        following_id BIGINT NOT NULL REFERENCES _user (id) ON DELETE CASCADE,
        PRIMARY KEY (follower_id, following_id),
        CONSTRAINT follower_following_unique UNIQUE (follower_id, following_id)
    );
