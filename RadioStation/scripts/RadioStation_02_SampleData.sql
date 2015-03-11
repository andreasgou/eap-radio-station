-- Music Genre
insert into app.MUSICGENRE (genrename) values ('Blues');
insert into app.MUSICGENRE (genrename) values ('Classic Rock');
insert into app.MUSICGENRE (genrename) values ('Gothic');
insert into app.MUSICGENRE (genrename) values ('Greek');

-- Music Production Company
insert into app.musicproductioncompany (name, address, telephone)
    values ('Albert Productions', '9 Rangers Road, Neutral Bay, New South Wales 2089, Australia', '02 9953 6038');

-- Artist
insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace)
    values ('Blues', 'Aretha', 'Franklin', 'Aretha Franklin', 'F', date('1942-03-25'), 'Memphis, Tennessee');
insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Blues', 'Rilley B.', 'King', 'B.B. King', 'M', date('1925-09-16'), 'Itta Bena, MS, USA');
insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Blues', 'Bessie', 'Smith', 'Bessie Smith', 'F', date('1895-04-15'), 'Chattanooga, Tennessee, USA');
insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Blues', 'Ellas Otha', 'Bates', 'Bo Diddley', 'M', date('1928-12-30'), 'McComb, MS, USA');

insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Classic Rock', 'Angus', 'Young', 'Angus Young', 'M', date('1955-03-31'), 'Glasgow, Scotland, UK');
insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Classic Rock', 'Cliff', 'Williams', 'Cliff Williams', 'M', date('1949-12-14'), 'Romford, Essex, England, UK');
insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Classic Rock', 'Brian', 'Johnson', 'Brian Johnson', 'M', date('1947-10-05'), 'Dunston, Gateshead, England, UK');
insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Classic Rock', 'Stevie', 'Young', 'Stevie Young', 'M', date('1956-11-12'), 'Glasgow, Scotland, UK');
insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Classic Rock', 'Chris', 'Slade', 'Chris Slade', 'M', date('1946-10-30'), 'Pontypridd, Wales, UK');

insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Classic Rock', 'David', 'Byrne', 'David Byrne', 'M', date('1952-05-14'), 'Dumbarton, Scotland, UK');

insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Classic Rock', 'Keith Noel', 'Emerson', 'Keith Emerson', 'M', date('1944-11-02'), 'Todmorden, West Riding of Yorkshire, England');
insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Classic Rock', 'Gregory Stuart', 'Lake', 'Greg Lake', 'M', date('1947-11-10'), 'Poole, Dorset, England');
insert into app.artist (genre, firstname, lastname, artisticname, sex, birthday, birthplace) 
    values ('Classic Rock', 'Carl Frederick Kendall', 'Palmer', 'Carl Palmer', 'M', date('1950-03-20'), 'Handsworth, Birmingham England');


-- Music Group
insert into app.musicgroup (name, formationdate) values('AC/DC', date('1942-03-25'));
insert into app.musicgroup (name, formationdate) values('Emerson, Lake & Palmer', date('1970-03-25'));

-- ArtistMusicGroup
-- AC/DC members
insert into app.artistmusicgroup (artist_id, musicgroup_id)
     select a.id, mg.id
       from app.artist a, app.musicgroup mg
      where a.artisticname = 'Angus Young' and mg.name = 'AC/DC';

-- ArtistMusicGroup
-- ELP members
insert into app.artistmusicgroup (artist_id, musicgroup_id)
     select a.id, mg.id
       from app.artist a, app.musicgroup mg
      where a.artisticname = 'Keith Emerson' and mg.name = 'Emerson, Lake & Palmer';
insert into app.artistmusicgroup (artist_id, musicgroup_id)
     select a.id, mg.id
       from app.artist a, app.musicgroup mg
      where a.artisticname = 'Greg Lake' and mg.name = 'Emerson, Lake & Palmer';
insert into app.artistmusicgroup (artist_id, musicgroup_id)
     select a.id, mg.id
       from app.artist a, app.musicgroup mg
      where a.artisticname = 'Carl Palmer' and mg.name = 'Emerson, Lake & Palmer';


-- Album
-- AC/DC - Back in Black
insert into app.album (COMPANY_ID, MUSICGROUP_ID, TITLE, TYPE1, DISKNUMBER, RELEASEDATE, TOTALDISKS)
     select mpc.id as company_id, mg.id as group_id, 'Back In Black', 'CS', 1, date('1980-08-21'), 1
       from app.musicproductioncompany mpc
 cross join app.musicgroup mg
      where mpc.name = 'Albert Productions'
        and mg.name = 'AC/DC';

-- Songs in Album
insert into app.song (ALBUM_ID, TITLE, DURATION, TRACKNR)
     select a.id, 'Hells Bells', minute('0:05:03')*60+second('0:05:03'), 1 from app.album a where a.title = 'Back In Black' UNION
     select a.id, 'Shoot To Thrill', minute('0:05:17')*60+second('0:05:17'), 2 from app.album a where a.title = 'Back In Black' UNION
     select a.id, 'What Do You Do for Money Honey', minute('0:03:35')*60+second('0:03:35'), 3 from app.album a where a.title = 'Back In Black' UNION
     select a.id, 'Given the Dog a Bone', minute('0:03:32')*60+second('0:03:32'), 4 from app.album a where a.title = 'Back In Black' UNION
     select a.id, 'Let Me Put My Love Into You', minute('0:04:15')*60+second('0:04:15'), 5 from app.album a where a.title = 'Back In Black' UNION
     select a.id, 'Back In Black', minute('0:04:08')*60+second('0:04:08'), 6 from app.album a where a.title = 'Back In Black' UNION
     select a.id, 'You Shook Me All Night Long', minute('0:03:31')*60+second('0:03:31'), 7 from app.album a where a.title = 'Back In Black' UNION
     select a.id, 'Have a Drink On Me', minute('0:03:58')*60+second('0:03:58'), 8 from app.album a where a.title = 'Back In Black' UNION
     select a.id, 'Shake a Leg', minute('0:04:05')*60+second('0:04:05'), 9 from app.album a where a.title = 'Back In Black' UNION
     select a.id, 'Rock and Roll Ain''t Noise Pollution', minute('0:04:13')*60+second('0:04:13'), 10 from app.album a where a.title = 'Back In Black';

-- Album
-- David Byrne, Fat Boy Slim - Here Lies Love
insert into app.album (COMPANY_ID, ARTIST_ID, TITLE, TYPE1, DISKNUMBER, RELEASEDATE, TOTALDISKS)
     select mpc.id as company_id, ar.id as group_id, 'Here Lies Love', 'LP', 1, date('2010-04-06'), 1
       from app.musicproductioncompany mpc
 cross join app.artist ar
      where mpc.name = 'Albert Productions'
        and ar.artisticname = 'David Byrne';

-- Songs in Album
insert into app.song (ALBUM_ID, TITLE, DURATION, TRACKNR)
     select a.id, 'Here Lies Love', minute('0:05:52')*60+second('0:05:52'), 1 from app.album a where a.title = 'Here Lies Love' UNION
     select a.id, 'Every Drop of Rain', minute('0:05:34')*60+second('0:05:34'), 2 from app.album a where a.title = 'Here Lies Love';

-- Album
-- ELP album disk 1
insert into app.album (COMPANY_ID, MUSICGROUP_ID, TITLE, TYPE1, DISKNUMBER, RELEASEDATE, TOTALDISKS)
     select mpc.id as company_id, mg.id as group_id, 'Welcome Back My Friends to the Show That Never Ends... Ladies and Gentlemen', 'LP', 1, date('1974-08-19'), 2 
       from app.musicproductioncompany mpc
 cross join app.musicgroup mg
      where mpc.name = 'Albert Productions'
        and mg.name = 'Emerson, Lake & Palmer';
-- ELP album disk 2
insert into app.album (COMPANY_ID, MUSICGROUP_ID, TITLE, TYPE1, DISKNUMBER, RELEASEDATE, TOTALDISKS, PARENTALBUM_ID)
     select mpc.id as company_id, mg.id as group_id, 'Welcome Back My Friends to the Show That Never Ends... Ladies and Gentlemen', 'LP', 2, date('1974-08-19'), 2, al.id 
       from app.musicproductioncompany mpc
 inner join app.album al 
         ON al.title = 'Welcome Back My Friends to the Show That Never Ends... Ladies and Gentlemen'
        AND al.disknumber=1
 cross join app.musicgroup mg
      where mpc.name = 'Albert Productions'
        and mg.name = 'Emerson, Lake & Palmer';

-- Songs in Album
insert into app.song (ALBUM_ID, TITLE, DURATION, TRACKNR)
     select a.id, 'Hoedown', minute('0:04:27')*60+second('0:04:27'), 1 from app.album a 
      where a.title = 'Welcome Back My Friends to the Show That Never Ends... Ladies and Gentlemen' AND DISKNUMBER=1 UNION
     select a.id, 'Jerusalem', minute('0:03:20')*60+second('0:03:20'), 2 from app.album a 
      where a.title = 'Welcome Back My Friends to the Show That Never Ends... Ladies and Gentlemen' AND DISKNUMBER=1 UNION
     select a.id, 'Piano Improvisations', minute('0:11:54')*60+second('0:11:54'), 1 from app.album a 
      where a.title = 'Welcome Back My Friends to the Show That Never Ends... Ladies and Gentlemen' AND DISKNUMBER=2 UNION
     select a.id, 'Take a Pebble (Conclusion)', minute('0:03:14')*60+second('0:03:14'), 2 from app.album a 
      where a.title = 'Welcome Back My Friends to the Show That Never Ends... Ladies and Gentlemen' AND DISKNUMBER=2

 