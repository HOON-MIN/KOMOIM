
create sequence amember_log_seq;

create sequence membernum_seq
increment by 1 start with 1;

create sequence groupnum_seq
increment by 1 start with 1;

create sequence chat_seq
increment by 1 start with 1;

create sequence board_seq
increment by 1 start with 1;

create sequence boardcomm_seq
increment by 1 start with 1;

create sequence grnum_seq;

create sequence gdnum_seq;

create table hobby(
hobbynum number primary key,
hname varchar2(30)
);

insert into hobby values(1,'아웃도어/여행');
insert into hobby values(2,'운동/스포츠');
insert into hobby values(3,'사진/영상');
insert into hobby values(4,'게임/오락');
insert into hobby values(5,'요리/제조');

create table amember (
membernum number primary key,
mid varchar2(30) unique ,
mpwd varchar2(30) not null,
mname varchar2(30) not null,
mloc varchar2(20) not null,
mhobby number not null,
mjumin varchar2(30) not null,
mdate date ,
gender varchar2(30) not null,
constraint amember_gender_ck check(gender = '남' or gender ='여') ,
constraint amember_mhobby_fk foreign key(mhobby) references hobby(hobbynum)
);

create table agroup (
groupnum number primary key,
gname varchar2(100) not null,
ghobby number not null,
gloc varchar2(20) not null,
ginfo varchar2(200) not null,
gdate date ,
CONSTRAINT agroup_ghobby_fk foreign key(ghobby) references hobby(hobbynum)
);

create table ajoin (
membernum number,
groupnum number,
jdate date,
joinmember number,
constraint ajoin_pk_dual primary key(membernum, groupnum),
constraint ajoin_groupnum_fk foreign key(groupnum) references agroup(groupnum),
constraint ajoin_membernum_fk foreign key(membernum) references amember(membernum)
);

create table board (
boardnum number primary key,
membernum number ,
subject varchar2(50) ,
content varchar2(200) ,
groupnum number ,  
bdate date,
wlist number,
constraint board_groupnum_fk foreign key(membernum,groupnum) references ajoin(groupnum,membernum)
);

create table boardcomm(
commnum number primary key,
boardnum number,
membernum number,
content varchar2(200),
cdate date,
constraint BOARDCOMM_MEMBERNUM_FK foreign key(membernum) references amember(membernum),
constraint BOARDCOMM_BOARDNUM__FK foreign key(boardnum) references board(boardnum)
);

create table chat (
cnum number primary key,
groupnum number ,
talk varchar2(200),
cdate date,
constraint chat_groupnum_fk foreign key(groupnum) references agroup(groupnum)
);

-- 가입대기 테이블
create table ajoin_delay(
gdnum number primary key,
membernum number,
groupnum number,
ddate date,
constraint ajoin_delay_fk1 foreign key(membernum) references amember(membernum),
constraint ajoin_delay_fk2 foreign key(groupnum) references agroup(groupnum)
);

-- 트리거 amember 회원가입 탈퇴 수정 테이블
create table amember_log(
lnum number primary key,
tablename varchar2(10),
dml_type varchar2(10),
membernum number,
mid varchar2(50),
ldate date
);

-- 뷰 
create view gview as (select * from agroup);



--그룹별 게시글의 순서 함수
--gnum = groupnum
create or replace function getWlist(gnum number) return number is 
an number ;
begin 
select  max(wlist) into an from board where groupnum = gnum;
if an is null then an:=1;
else an := an + 1;
end if;
return an;
end;
/

--그룹별 가입번호 함수
create or replace function gnum(gnum number) return number is 
an number ;
begin 
select  max(joinmember) into an from ajoin where groupnum = gnum;
if an = null then an:=1;
else an := an + 1;
end if;
return an;
end;
/

--주민번호로 성별 반환 -function
create or replace function usergender(userjumin  varchar2) return varchar2 is
gender varchar2(10);
begin
select  decode(substr(userjumin,instr(userjumin,'-',1)+1,1),1,'남',3,'남','여') 성별 into gender
 from dual;
 return gender;
end;
/

--멤버에서 주민번호 넣어 나이 얻기 함수
create or replace function getage(jumin amember.mjumin%type) return number is
mage varchar2(100);
begin
select to_char(sysdate,'yyyy')-(decode(substr(jumin,8,1),'1','19','2','19','20')||substr(jumin,1,2)+1) into mage
from dual;
return to_number(mage);
end;
/


create or replace function fromjoin(mdate date) return varchar2 is
fj varchar2(100);
begin 
select '가입한지'||to_char(trunc(months_between(sysdate,mdate)/12))||'년'||
to_char(mod(trunc(months_between(sysdate,mdate)),12))||'달'||
to_char(trunc(mod((sysdate - mdate),365.25/12)))||'일 됬습니다' 가입일 into fj from dual;
return fj;
end;
/








-- 그룹에 가입된 멤버의 정보 전체 보기 프로시저
create or replace procedure p1(num in number, cur out SYS_REFCURsor)
is
begin
open cur for
select j.groupnum,j.jdate,m.mname,g.gname,m.mid,j.membernum,usergender(mjumin) gender  FROM amember m
JOIN ajoin j
ON m.membernum=j.membernum
JOIN agroup g
ON g.groupnum=j.groupnum where j.groupnum = num;
end;
/

-- 선호취미순 정렬로 그룹리스트 받아오기
create or replace procedure glist(hnum in number,cur out sys_refcursor) is
begin 
open cur for
select g.*,h.hname from agroup g,hobby h where g.ghobby = h.hobbynum order by decode(ghobby,hnum,0,ghobby);
end;
/

-- 가입한 모임명단 확인
create or replace procedure ckgjlist(gnum in number,cur out sys_refcursor) is

begin 
open cur for
select membernum from ajoin where groupnum = gnum;
end;
/

create or replace procedure ckJoinDelay(gnum in number, cur out sys_refcursor) is

begin 
open cur for
select membernum from ajoin_delay where groupnum =gnum;
end;
/

create or replace procedure ckJoinDelay2(gnum in number, cur out sys_refcursor) is

begin 
open cur for
select membernum from ajoin where groupnum =gnum;
end;
/

create or replace procedure delmoim(mnum number,gnum number)
is
begin 
delete from ajoin where membernum = mnum and groupnum = gnum ;
end; 
/


create or replace procedure userinfo(usernum in number, cur out SYS_REFCURSOR) is
begin
open cur for
select m.* ,decode(substr(mjumin,instr(mjumin,'-',1)+1,1),1,'남',3,'남','여') 성별,
'가입한지'||trunc(months_between(sysdate,mdate)/12)||'년'||
mod(trunc(months_between(sysdate,mdate)),12)||'달'||trunc((sysdate-mdate)/365.25/12)||'일 됬습니다' 가입일 from amember m where membernum=usernum;
end;
/

create or replace procedure userprofile(id in varchar2, cur out sys_refcursor) is
begin 
open cur for
select m.*,fromjoin(m.mdate) fj,h.hname from amember m, hobby h where h.hobbynum = m.mhobby and  mid = id;
end;
/

--패키지
create or replace package join_pkg is
procedure joindelay(mnum in number, grnum in number);
procedure accetjoin(mnum in number, grnum in number);
procedure denyjoin(mnum in number, grnum in number);
end;
/

create or replace package body join_pkg is
procedure joindelay(mnum in number,grnum in number) is
begin 
insert into ajoin_delay values(gdnum_seq.nextVal,mnum,grnum,sysdate);
end;
procedure accetjoin(mnum in number, grnum in number) is
begin
insert into ajoin values(mnum,grnum,sysdate,gnum(grnum));
delete ajoin_delay where membernum = mnum and groupnum = grnum;
end;
procedure denyjoin(mnum in number, grnum in number)is
begin
delete ajoin_delay where membernum = mnum and groupnum = grnum;
end;
end;
/

create or replace procedure gsearch(groupn varchar2 , cur out sys_refcursor) is
begin
open cur for
select g.gname,g.ghobby,g.gloc,g.groupnum,g.ginfo,h.hname from AGroup g, hobby h 
where g.ghobby = h.hobbynum and( gname like '%'||groupn||'%' or ginfo like '%'||groupn||'%');
end; 
/







-- 트리거
create or replace trigger trg_amember_log
after
insert or update or delete on amember
for each row
begin
if inserting then 
insert into amember_log values(amember_log_seq.nextVal,'amember','가입',:new.membernum,:new.mid,sysdate);
elsif updating then
insert into amember_log values(amember_log_seq.nextVal,'amember','변경',:old.membernum,:old.mid,sysdate);
elsif deleting then
insert into amember_log values(amember_log_seq.nextVal,'amember','탈퇴',:old.membernum,:old.mid,sysdate);
end if;
end;
/
--회원탈퇴
create or replace PROCEDURE dropMember(num in number)
IS
BEGIN
delete from boardcomm where membernum = num;
delete from ajoin_delay where membernum = num;
delete from board where membernum = num;
delete from ajoin where membernum = num;
delete from amember where membernum = num;
END;
/
--모임 삭제
create or replace procedure delmoim(mnum number,gnum number)
is
begin 
delete from board where membernum = mnum and groupnum = gnum;
delete from ajoin where membernum = mnum and groupnum = gnum ;
end; 
/


insert into amember values(membernum_seq.nextVal,'tae1','1234','박태진','서울',1,'940206-1055555','2010-10-10',usergender('940206-1055555'));
insert into amember values(membernum_seq.nextVal,'tae2','1234','이태진','서울',1,'941231-2055555',sysdate,usergender('941231-2055555'));
insert into amember values(membernum_seq.nextVal,'tae3','1234','김태진','서울',2,'970523-1255555',sysdate,usergender('970523-1255555'));
insert into amember values(membernum_seq.nextVal,'tae4','1234','최태진','서울',3,'921231-1055555',sysdate,usergender('921231-1055555'));
insert into amember values(membernum_seq.nextVal,'tae5','1234','임태진','서울',4,'021231-3055555',sysdate,usergender('021231-3055555'));
insert into amember values(membernum_seq.nextVal,'tae6','1234','조태진','서울',3,'041231-4055555',sysdate,usergender('041231-4055555'));
insert into amember values(membernum_seq.nextVal,'tae7','1234','구태진','서울',3,'941231-2055555',sysdate,usergender('941231-2055555'));
insert into amember values(membernum_seq.nextVal,'tae8','1234','왕태진','서울',1,'901105-3055555',sysdate,usergender('901105-3055555'));
insert into amember values(membernum_seq.nextVal,'tae9','1234','윤태진','서울',2,'910127-2055555',sysdate,usergender('910127-2055555'));
insert into amember values(membernum_seq.nextVal,'tae10','1234','길태진','서울',4,'991231-1055555',sysdate,usergender('991231-1055555'));
commit;


--group더미
insert into agroup values(groupnum_seq.nextVal,'소모임1',1,'서울','안녕하세요1',sysdate);
insert into agroup values(groupnum_seq.nextVal,'소모임2',2,'서울','안녕하세요2',sysdate);
insert into agroup values(groupnum_seq.nextVal,'소모임3',3,'서울','안녕하세요3',sysdate);
insert into agroup values(groupnum_seq.nextVal,'소모임4',4,'서울','안녕하세요4',sysdate);
insert into agroup values(groupnum_seq.nextVal,'소모임5',5,'서울','안녕하세요5',sysdate);
commit;
--join 더미
insert into ajoin values((select membernum from amember where membernum = 1),(select groupnum from agroup where groupnum =1),sysdate,1);
insert into ajoin values((select membernum from amember where membernum = 3),(select groupnum from agroup where groupnum =1),sysdate,gnum(1));
insert into ajoin values((select membernum from amember where membernum = 4),(select groupnum from agroup where groupnum =2),'2022-04-20',1);
insert into ajoin values((select membernum from amember where membernum = 10),(select groupnum from agroup where groupnum =2),sysdate,gnum(2));
insert into ajoin values((select membernum from amember where membernum = 2),(select groupnum from agroup where groupnum =3),'2022-05-01',1);
insert into ajoin values((select membernum from amember where membernum = 1),(select groupnum from agroup where groupnum =4),sysdate,1);
insert into ajoin values((select membernum from amember where membernum = 3),(select groupnum from agroup where groupnum =4),sysdate,gnum(4));
insert into ajoin values((select membernum from amember where membernum = 4),(select groupnum from agroup where groupnum =5),'2022-04-20',1);
insert into ajoin values((select membernum from amember where membernum = 10),(select groupnum from agroup where groupnum =5),sysdate,gnum(5));
insert into ajoin values((select membernum from amember where membernum = 2),(select groupnum from agroup where groupnum =4),'2022-05-01',gnum(5));

commit;