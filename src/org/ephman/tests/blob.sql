drop table test_blob;

create table test_blob (
	data BLOB);


create or replace procedure storeBlob (foo IN RAW) 
AS
	tmp BLOB;
	sz binary_integer;
BEGIN
	insert INTO test_blob VALUES (EMPTY_BLOB ()) returning data into tmp;
	sz := utl_raw.length (foo);
	dbms_lob.write (tmp, sz, 1, foo);
END storeBlob;

/
show errors;

create or replace procedure getBlob (foo OUT BLOB) 
AS
BEGIN
	select data into foo from test_blob;
END getBlob;
/


drop table test_raw;

create table test_raw (
	data LONG RAW);


create or replace procedure storeRaw (foo IN RAW) 
AS
BEGIN
	insert INTO test_raw VALUES (foo);
END storeRaw;

/
show errors;

create or replace procedure getRaw (foo OUT RAW) 
AS
BEGIN
	select data into foo from test_raw;
END getRaw;
/
