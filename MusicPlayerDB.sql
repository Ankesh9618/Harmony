create database musicplayer;
use musicplayer;

create table songs(
	id int primary key auto_increment,
    name nvarchar(50),
    artist nvarchar(50), 		
    duration varchar(10), 					
    fileSong varchar(50)
);
create table playlist(
	id int primary key auto_increment,
    name nvarchar(50)
);
create table detailSong(
	id int primary key auto_increment,
    id_Song int,
    id_playlist int,
    constraint FK_detailSong_songs foreign key(id_Song) references songs(id),
    constraint FK_detailSong_playlist foreign key(id_playlist) references playlist(id)
);

insert into songs( name,fileSong,artist,duration) values("23","23.mp3","artist","4:29");
insert into songs( name,fileSong,artist,duration) values("cant-wait","cant-wait.mp3","artist","3:27");
insert into songs( name,fileSong,artist,duration) values("colors","colors.mp3","artist","3:22");
insert into songs( name,fileSong,artist,duration) values("forces","forces.mp3","artist","4:21");
insert into songs( name,fileSong,artist,duration) values("haru-haru","haru-haru.mp3","artist","3:46");
insert into songs( name,fileSong,artist,duration) values("interstellar","interstellar.mp3","artist","3:11");
insert into songs( name,fileSong,artist,duration) values("catch-my-breath","catch-my-breath.mp3","artist","4:09");
insert into songs( name,fileSong,artist,duration) values("escape","escape.mp3","artist","3:39");
insert into songs( name,fileSong,artist,duration) values("stars","stars.mp3","artist","3:48");
insert into songs( name,fileSong,artist,duration) values("beauty-and-a-beat","beauty-and-a-beat.mp3","artist","3:45");
insert into songs( name,fileSong,artist,duration) values("bunt-every-day-is-a-song","bunt-every-day-is-a-song.mp3","artist","3:48");
insert into songs( name,fileSong,artist,duration) values("faster-car","faster-car.mp3","artist","3:47");
insert into songs( name,fileSong,artist,duration) values("levels","levels.mp3","artist","3:18");
insert into songs( name,fileSong,artist,duration) values("see-you-again","see-you-again.mp3","artist","3:57");
insert into songs( name,fileSong,artist,duration) values("shape-of-you","shape-of-you.mp3","artist","4:23");
insert into songs( name,fileSong,artist,duration) values("suga-suga","suga-suga.mp3","artist","3:59");
insert into songs( name,fileSong,artist,duration) values("young-wild-and-free","young-wild-and-free.mp3","artist","3:27");


insert into playlist(name) values("playlist1");
insert into playlist(name) values("playlist2");
insert into playlist(name) values("playlist3");
insert into playlist(name) values("playlist4");
insert into playlist(name) values("playlist5");

insert into detailSong(id_Song,id_playlist) values(1,2);
insert into detailSong(id_Song,id_playlist) values(3,2);
insert into detailSong(id_Song,id_playlist) values(2,2);
insert into detailSong(id_Song,id_playlist) values(10,2);
insert into detailSong(id_Song,id_playlist) values(7,2);
insert into detailSong(id_Song,id_playlist) values(2,1);
insert into detailSong(id_Song,id_playlist) values(4,1);
insert into detailSong(id_Song,id_playlist) values(5,1);
insert into detailSong(id_Song,id_playlist) values(8,1);
insert into detailSong(id_Song,id_playlist) values(9,1);
insert into detailSong(id_Song,id_playlist) values(1,3);
insert into detailSong(id_Song,id_playlist) values(3,3);
insert into detailSong(id_Song,id_playlist) values(2,3);
insert into detailSong(id_Song,id_playlist) values(4,3);
insert into detailSong(id_Song,id_playlist) values(6,3);
insert into detailSong(id_Song,id_playlist) values(6,4);
insert into detailSong(id_Song,id_playlist) values(1,4);
insert into detailSong(id_Song,id_playlist) values(4,4);
insert into detailSong(id_Song,id_playlist) values(9,4);
insert into detailSong(id_Song,id_playlist) values(12,4);
insert into detailSong(id_Song,id_playlist) values(7,5);
insert into detailSong(id_Song,id_playlist) values(2,5);
insert into detailSong(id_Song,id_playlist) values(4,5);
insert into detailSong(id_Song,id_playlist) values(6,5);
insert into detailSong(id_Song,id_playlist) values(11,5);


select * from songs;
select * from playlist;
select * from detailSong;



