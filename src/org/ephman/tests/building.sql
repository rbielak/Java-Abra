drop TABLE t_building;
drop TABLE t_movie;
drop SEQUENCE building_seq;
drop SEQUENCE movie_seq;


drop TYPE O_BUILDING;
drop TYPE MV_ARRAY;

create or replace package curs 
AS
	TYPE REFCURSOR IS REF CURSOR;
END;
/


create or replace TYPE STR_ARRAY AS VARRAY (20) OF varchar2(40);

/
create or replace TYPE INT_ARRAY AS VARRAY (20) OF NUMBER;
/

create or replace TYPE O_MOVIE AS OBJECT (
	movie_oid int,
	name  varchar2(40));
/

create or replace TYPE MV_ARRAY AS TABLE OF O_MOVIE;
/
create TABLE tab_movie OF O_MOVIE;


create or replace TYPE O_BUILDING AS OBJECT (
	building_oid int,
	name varchar2(40),
	occupants STR_ARRAY,
	movies MV_ARRAY,
	shifts INT_ARRAY);


/

-- done with types now tables..

create SEQUENCE building_seq;
create TABLE t_building (
	building_oid int,
	constraint b_pk primary key (building_oid),
	name varchar2(40),
	occupants STR_ARRAY,
	movies MV_ARRAY,
	shifts INT_ARRAY
	)
	nested TABLE movies STORE AS mv_table;


create SEQUENCE movie_seq;
create TABLE t_movie (
	movie_oid int,
	constraint m_pk primary key (movie_oid),
	name varchar2(40));


-- now procs

create or replace PROCEDURE storeMovie (p_movie IN OUT O_MOVIE)
AS
BEGIN
	insert INTO t_movie VALUES (movie_seq.nextval, p_movie.name)
		returning movie_oid into p_movie.movie_oid;

END storeMovie;

/
create or replace PROCEDURE storeBuilding (p_building IN O_BUILDING, o_oid OUT int)
AS

BEGIN
	/*tmp := p_building.movie;
	storeMovie (tmp); --inout use oid */
	insert into t_building VALUES (building_seq.nextval, p_building.name, 
	  p_building.occupants, p_building.movies, p_building.shifts)
		returning building_oid into o_oid;
END storeBuilding;

/

/*create or replace type REFCURSOR is REF CURSOR return; */


create or replace FUNCTION getBuildingNames 
RETURN curs.REFCURSOR
AS
	c curs.REFCURSOR;
BEGIN
 	OPEN c FOR select name from t_building;		
	return c;

END getBuildingNames;
/ 
show errors

create or replace view obv OF O_BUILDING WITH OBJECT OID (building_oid) as 
	select t.building_oid, t.name, t.occupants, t.movies, t.shifts
		FROM t_building t;

create or replace FUNCTION getBuildings 
RETURN curs.REFCURSOR
AS
	c curs.REFCURSOR;
BEGIN
 	OPEN c FOR select * from obv;

	-- c.close;
	return c;

END getBuildings;
/ 



show errors;

create or replace PROCEDURE getBuilding (param_oid IN int, p_building OUT O_BUILDING)
AS
BEGIN
	p_building := O_BUILDING (NULL, NULL, NULL, NULL, NULL);
	  select t.building_oid, t.name, t.occupants, t.movies, t.shifts
		INTO p_building.building_oid, p_building.name, p_building.occupants,
			p_building.movies, p_building.shifts
		FROM t_building t 
		WHERE t.building_oid = param_oid;
	/* and t.movie_oid=m.movie_oid; */
	  
END getBuilding;

/

