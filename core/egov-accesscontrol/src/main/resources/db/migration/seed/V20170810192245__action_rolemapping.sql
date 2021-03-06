

insert into service (id,code,name,enabled,contextroot,displayname,ordernumber,parentmodule,tenantId)
values (nextval('SEQ_SERVICE'),'STCREATE','STCreate',true, 'pgr' ,'Create Service Type', 11 ,(select id from service where name ='Masters' and code='MSTR' and tenantId='default') ,'default');

insert into eg_action(id, name,url,servicecode,queryparams,parentmodule,ordernumber,displayname,enabled,createdby,createddate,lastmodifiedby,lastmodifieddate)
values(nextval('SEQ_EG_ACTION'),'Create Service Type','/pgr-master/service/v2/_create','STCREATE',NULL, (select id from service where name ='STCreate' and contextroot='pgr' and tenantid='default'),1,'Create Service Type',false,1,now(),1,now());

insert into eg_roleaction(roleCode,actionid,tenantId)values('SRA',(select id from eg_action where name='Create Service Type'),'default');


