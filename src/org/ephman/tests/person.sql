-- to test oracle bindings

drop TABLE t_person;
drop SEQUENCE person_seq;
drop TYPE O_PERSON;


/*
create or replace TYPE O_MOVIE AS OBJECT (
	name  varchar2(40));

/*/



/

create or replace TYPE O_PERSON AS OBJECT (
	person_oid int,
	age number,
	name varchar2(40),
	siblings int,
	favorite_movie O_MOVIE);

/

create SEQUENCE person_seq;

create TABLE t_person (
	person_oid int,
	constraint person_pk primary key (person_oid) ,
	age number,
	name varchar2(40), 
	siblings int,
	favorite_movie varchar2(40));


create or replace FUNCTION storePerson (p_person IN O_PERSON)
RETURN int
AS
   o_oid int;
BEGIN
	insert into t_person VALUES (
		person_seq.nextval,
		p_person.age,
		p_person.name,
		p_person.siblings,
		p_person.favorite_movie.name) returning person_oid into o_oid;
	return o_oid;

END storePerson;
/
create or replace FUNCTION getPerson (o_oid IN int)
RETURN O_PERSON
AS
   p_person O_PERSON;
BEGIN
	p_person := O_PERSON (NULL,  NULL, NULL, NULL, O_MOVIE (NULL, NULL));
	select p.person_oid, p.age, p.name, p.siblings, p.favorite_movie
	  INTO p_person.person_oid, p_person.age, p_person.name,
		p_person.siblings, p_person.favorite_movie.name
	  FROM t_person p WHERE p.person_oid=o_oid ;
	return p_person;
END getPerson ;
/


