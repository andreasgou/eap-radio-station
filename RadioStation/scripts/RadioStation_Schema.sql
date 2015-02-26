DROP TABLE PLAYLIST_SONG;
DROP TABLE SONG;
DROP TABLE PLAYLIST;
DROP TABLE ALBUM;
DROP TABLE MUSICPRODUCTIONCOMPANY;
DROP TABLE ARTISTMUSICGROUP;
DROP TABLE MUSICGROUP;
DROP TABLE ARTIST;
DROP TABLE MUSICGENRE;


CREATE TABLE MUSICGENRE(
    GENRENAME   VARCHAR(50), 

    PRIMARY KEY (GENRENAME)
);

CREATE TABLE ARTIST( 
    ID              INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
    GENRE           VARCHAR(50) NOT NULL,
    FIRSTNAME       VARCHAR(255) NOT NULL,
    LASTNAME        VARCHAR(255) NOT NULL,
    ARTISTICNAME    VARCHAR(255) NOT NULL,
    SEX             CHAR(1),
    BIRTHDAY        DATE,
    BIRTHPLACE      VARCHAR(255),

    FOREIGN KEY(GENRE) REFERENCES MUSICGENRE(GENRENAME)
);

CREATE TABLE MUSICGROUP(
    ID              INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
    NAME            VARCHAR(255) NOT NULL,
    FORMATIONDATE   DATE
);

CREATE TABLE ARTISTMUSICGROUP(
    ARTIST_ID       INTEGER  GENERATED BY DEFAULT AS IDENTITY , 
    MUSICGROUP_ID   INTEGER NOT NULL, 

    PRIMARY KEY (ARTIST_ID, MUSICGROUP_ID),
    FOREIGN KEY (ARTIST_ID) REFERENCES  ARTIST(ID),
    FOREIGN KEY (MUSICGROUP_ID) REFERENCES  MUSICGROUP(ID)
);

CREATE TABLE MUSICPRODUCTIONCOMPANY(
    ID           INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
    NAME         VARCHAR(255) NOT NULL,
    ADDRESS      VARCHAR(255),
    TELEPHONE    INTEGER
);

CREATE TABLE ALBUM(
    ID              INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ,
    COMPANY_ID      INTEGER NOT NULL,
    ARTIST_ID       INTEGER,
    MUSICGROUP_ID   INTEGER,
    TITLE           VARCHAR(255) NOT NULL,
    TYPE1           VARCHAR(20),
    DISKNUMBER      SMALLINT,

    FOREIGN KEY (COMPANY_ID) REFERENCES  MUSICPRODUCTIONCOMPANY(ID),
    FOREIGN KEY (ARTIST_ID) REFERENCES  ARTIST(ID),
    FOREIGN KEY (MUSICGROUP_ID) REFERENCES  MUSICGROUP(ID)
);

CREATE TABLE PLAYLIST(
    ID      INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    NAME    VARCHAR(255) 
);

CREATE TABLE SONG(
    ID          INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ,
    TITLE       VARCHAR(255),
    DURATION    INTEGER,
    TRACKNR     SMALLINT,
    ALBUM_ID    INTEGER,

    FOREIGN KEY (ALBUM_ID) REFERENCES  ALBUM(ID)
);

CREATE TABLE PLAYLIST_SONG(
    PLAYLIST_ID INTEGER  NOT NULL ,
    SONG_ID     INTEGER NOT NULL,

    PRIMARY KEY(PLAYLIST_ID,SONG_ID),
    FOREIGN KEY(PLAYLIST_ID) REFERENCES PLAYLIST(ID),
    FOREIGN KEY(SONG_ID) REFERENCES SONG(ID)
);
