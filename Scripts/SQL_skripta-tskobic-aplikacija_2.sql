/*minimalno 20 aerodroma za preuzimanje (EBBR, EDDF, EDDM, EGGP, EGLL, EIDW, 
EPWA, GCLP, HEGN, LDZA, LEBL, LEPA, LFPG, EDDS, LIPZ, LOWW, LTBJ, LZLH, 
LJLJ, OMDB).*/
select * from aerodromi_praceni ap;

/*minimalno 100.000 preuzetih polazaka s aerodroma*/
select COUNT(*) from aerodromi_polasci ap;

/*minimalno 100.000 preuzetih dolazaka na aerodrome*/
select COUNT(*) from aerodromi_dolasci ad;

/*minimalno 15 dana u cijelosti za koje su preuzeti podaci polazaka i dolazaka*/
select COUNT(*) as broj_datuma from (select * from (select date_format(DATE_ADD(from_unixtime(0), interval ap.firstSeen second), '%Y-%m-%d') as datum
from aerodromi_polasci ap
union all
select date_format(DATE_ADD(from_unixtime(0), interval ad.lastSeen second), '%Y-%m-%d') as datum
from aerodromi_dolasci ad order by datum asc) as preuzimanja group by datum) as datumi

/*broj preuzetih podataka po danima za sve aerodrome */
select datum, SUM(broj_preuzimanja) as broj_preuzimanja from (select date_format(DATE_ADD(from_unixtime(0), interval ap.firstSeen second), '%Y-%m-%d') as datum,
COUNT(date_format(DATE_ADD(from_unixtime(0), interval ap.firstSeen second), '%Y-%m-%d')) as broj_preuzimanja 
from aerodromi_polasci ap group by datum
union all
select date_format(DATE_ADD(from_unixtime(0), interval ad.lastSeen second), '%Y-%m-%d') as datum,
COUNT(date_format(DATE_ADD(from_unixtime(0), interval ad.lastSeen second), '%Y-%m-%d')) as broj_preuzimanja 
from aerodromi_dolasci ad group by datum order by datum asc) as preuzimanja group by datum

/*broj preuzetih podataka po danima za sve aerodrome pojedinačno */
select icao, datum, SUM(broj_preuzimanja) as broj_preuzimanja from (select ap.estDepartureAirport as icao, date_format(DATE_ADD(from_unixtime(0), interval ap.firstSeen second), '%Y-%m-%d') as datum,
COUNT(date_format(DATE_ADD(from_unixtime(0), interval ap.firstSeen second), '%Y-%m-%d')) as broj_preuzimanja 
from aerodromi_polasci ap group by icao, datum
union all
select ad.estArrivalAirport as icao, date_format(DATE_ADD(from_unixtime(0), interval ad.lastSeen second), '%Y-%m-%d') as datum,
COUNT(date_format(DATE_ADD(from_unixtime(0), interval ad.lastSeen second), '%Y-%m-%d')) as broj_preuzimanja 
from aerodromi_dolasci ad group by icao, datum order by datum asc) as preuzimanja group by icao, datum order by datum asc

/*broj preuzetih podataka po danima za odabrane aerodrome*/
select icao, datum, SUM(broj_preuzimanja) as broj_preuzimanja from (select ap.estDepartureAirport as icao, date_format(DATE_ADD(from_unixtime(0), interval ap.firstSeen second), '%Y-%m-%d') as datum,
COUNT(date_format(DATE_ADD(from_unixtime(0), interval ap.firstSeen second), '%Y-%m-%d')) as broj_preuzimanja 
from aerodromi_polasci ap where ap.estDepartureAirport = 'OMDB' group by icao, datum
union all
select ad.estArrivalAirport as icao, date_format(DATE_ADD(from_unixtime(0), interval ad.lastSeen second), '%Y-%m-%d') as datum,
COUNT(date_format(DATE_ADD(from_unixtime(0), interval ad.lastSeen second), '%Y-%m-%d')) as broj_preuzimanja 
from aerodromi_dolasci ad where ad.estArrivalAirport = 'OMDB' group by icao, datum order by datum asc) as preuzimanja group by icao, datum

