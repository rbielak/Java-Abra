
create table db_poet(
	a_oid numeric identity,
	primary key (a_oid),
	a_war_vet char(1),
	a_war_hero char(1),
	po_poem varchar(128) null,
	a_sal numeric(38,3) null constraint auth_sal_ge_zero check (a_sal>=0),
	a_lname varchar(40) unique null,
	a_fname varchar(40) null,
	a_b_day datetime null,
	version_number int);

create table db_publisher(
	p_oid numeric identity,
	primary key (p_oid),
	p_name varchar(40) null,
	p_address text null,
	version_number int);

create table db_author(
	a_oid numeric identity,
	primary key (a_oid),
	wadr_street varchar(16),
	wadr_state varchar(2),
	wadr_city varchar(16),
	a_war_vet char(1),
	a_war_hero char(1),
	a_sal numeric(38,3) null constraint auth_sal_ge_zero1 check (a_sal>=0),
	a_lname varchar(40) unique null,
	hadr_street varchar(16),
	hadr_state varchar(2),
	hadr_city varchar(16),
	a_fname varchar(40) null,
	a_b_day datetime null,
	version_number int);

create table db_w_team(
	w_oid numeric identity,
	primary key (w_oid),
	w_master_name varchar(40) null,
	w_apprentice_name varchar(40) null,
	version_number int);

create table db_book(
	b_oid numeric identity,
	primary key (b_oid),
	b_title varchar(80) null,
	b_text text null,
	b_publisher int null,
	b_author int null,
	b_isbn varchar(10) null,
	version_number int);


-- constraints and referential integrity


create index title_i on db_book (b_title);

create index author_i on db_book (b_author);
