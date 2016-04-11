/* password = admin (sha1 encryption). Change email address. */
INSERT INTO gscdatacatalogue.gsc_002_user (id,json) VALUES 
(1,'{"email": "insertemail@admin.com", "status": "verified", "password": "d033e22ae348aeb5660fc2140aec35850c4da997", "username": "Admin"}');

INSERT INTO gscdatacatalogue.gsc_003_role (id,json) VALUES (1,
	'{"users": [{"iduser": "1"}], "rolename": "SystemAdmin", "description": "System administrator role"}'
);

INSERT INTO gscdatacatalogue.gsc_004_function (id,json) VALUES 
	(1,'{"type": "datacatalogue", "description": "enables lock/unlock user service", "functionname": "LockUser"}'),
	(2,'{"type": "datacatalogue", "description": "enables delete user service", "functionname": "DeleteUser"}'),
	(3,'{"type": "datacatalogue", "description": "enables assign user to role service", "functionname": "AssignUserToRole"}'),
	(4,'{"type": "datacatalogue", "description": "enables assign permission to role service", "functionname": "AssignPermissionToRole"}'),
	(5,'{"type": "datacatalogue", "description": "enables create organization service", "functionname": "CreateOrganization"}'),
	(6,'{"type": "datacatalogue", "description": "enables update organization service", "functionname": "UpdateOrganization"}'),
	(7,'{"type": "datacatalogue", "description": "enables delete organization service", "functionname": "DeleteOrganization"}');

INSERT INTO gscdatacatalogue.gsc_005_permission (id,json) VALUES (1,'{"name": "System administrator permissions", "idrole": "1", "functions": [{"idfunction": "1"},{"idfunction": "2"},{"idfunction": "3"},{"idfunction": "4"}]}'
);