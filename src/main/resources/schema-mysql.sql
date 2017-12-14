-- This SQL contains a "create table" that can be used to create a table that JdbcUsersConnectionRepository can persist
-- connection in. It is, however, not to be assumed to be production-ready, all-purpose SQL. It is merely representative
-- of the kind of table that JdbcUsersConnectionRepository works with. The table and column names, as well as the general
-- column types, are what is important. Specific column types and sizes that work may vary across database vendors and
-- the required sizes may vary across API providers.

create table if not exists UserConnection (userId varchar(255) not null,
	providerId varchar(255) not null,
	providerUserId varchar(255),
	rank int not null,
	displayName varchar(255),
	profileUrl varchar(512),
	imageUrl varchar(512),
	accessToken varchar(512) not null,
	secret varchar(512),
	refreshToken varchar(512),
	expireTime bigint,
	primary key (userId, providerId, providerUserId),
	unique index UserConnectionRank (userId, providerId, rank));

create table if not exists award (id bigint auto_increment not null,
    nickname varchar(255),
    phone varchar(11) not null,
    gift tinyint,
    created timestamp not null,
    notified boolean,
    claimed boolean,
    version bigint,
    primary key (id));

create table if not exists job_lock (job varchar(16) not null,
    created timestamp not null default current_timestamp,
    primary key (job));

create table if not exists organization (id bigint auto_increment,
    name varchar(255) not null,
    total_members int,
    submitted_members int,
    version bigint,
    primary key (id));

create table if not exists question_level (id bigint auto_increment not null,
    title varchar(255) not null,
    level tinyint,
    active boolean,
    created timestamp not null default current_timestamp,
    primary key (id));

create table if not exists question (id bigint auto_increment not null,
    title varchar(255) not null,
    answers varchar(255) not null,
    answer varchar(255) not null,
    question_level_id bigint,
    primary key (id),
    foreign key (question_level_id)
        references question_level(id) on delete cascade on update cascade);

create table if not exists quiz (id bigint auto_increment not null,
    level tinyint,
    username varchar(255) not null,
    phone varchar(11) not null,
    organization_id bigint,
    score int,
    created date not null,
    primary key (id));

create table if not exists users (username varchar(255) not null,
    password varchar(255) not null,
    enabled boolean,
    nickname varchar(255),
    primary key (username));

create table if not exists authorities (id bigint auto_increment not null,
    username varchar(255),
    authority varchar(255) not null,
    primary key (id),
    foreign key (username) references users(username));

create table if not exists mp_access_token (id bigint auto_increment not null,
    access_token varchar(512) not null,
    expires_in bigint,
    expires_at bigint,
    version bigint,
    primary key (id));
