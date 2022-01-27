mysql telcoServiceDB <db/schema.sql> out.tab -uuber2 -puber 

mysql telcoServiceDB <db/triggers.sql> out.tab -uuber2 -puber 
mysql telcoServiceDB <db/buildDB.sql> out.tab -uuber2 -puber 

mysql telcoServiceDB <db/materializedViewsSQL.sql> out.tab -uuber2 -puber 
