/* global angular, gscDatacat */

'use strict';
angular.module('gscDatacat.services')
    .factory('authSvc',
        ['$rootScope',
            '$state',
            '$q',
            function($rootScope,
                $state,
                $q) {

                $rootScope.data = $rootScope.data || {};

                $rootScope.data = {};

                $rootScope.data.authUser = {};

                $rootScope.data.systemFunctions = {
                    'LockUser': 1,
                    'DeleteUser': 2,
                    'AssignUserToRole': 3,
                    'AssignPermissionToRole': 4,
                    'CreateOrganization': 5,
                    'UpdateOrganization': 6,
                    'DeleteOrganization': 7,
                    'MapDefault': 188,
                    'MapVisibility': 187
                };

                var _sampleUser = {
                    iduser: 1,
                    username: 'admin@geosmartcity.eu',
                    password: 'geosmartcity',
                    organizationId: 666,
                    organizations: [{
                            id: 666,
                            organizationname: 'Asplan Viak Internet as'
                        }],
                    roles: [{
                            idrole: 1,
                            functions: [
                                {
                                    idfunction: 1
                                },
                                {
                                    idfunction: 2
                                },
                                {
                                    idfunction: 3
                                },
                                {
                                    idfunction: 4
                                },
                                {
                                    idfunction: 5
                                },
                                {
                                    idfunction: 6
                                },
                                {
                                    idfunction: 7
                                }
                            ]
                        }]
                };

                var _login = function(username, password) {

                    var dfd = $q.defer();

                    if (username === _sampleUser.username &&
                        password === _sampleUser.password) {
                        gsc.util.clearExtendObject($rootScope.data.authUser, _sampleUser);
                        dfd.resolve(gscDatacat.Response.getSuccess($rootScope.data.authUser,
                            'Local login shim succeeded'));
                    } else {
                        gsc.user.login(username, password)
                            .then(function(res) {
                                if (res.iduser !== undefined && gsc.util.isNumber(res.iduser)) {
                                    gsc.util.clearExtendObject($rootScope.data.authUser, {});
                                    $rootScope.data.authUser.username = res.username;
                                    $rootScope.data.authUser.iduser = +res.iduser;
                                    if (res.organizations !== undefined && res.organizations.length > 0) {
                                        $rootScope.data.authUser.organizationId = +res.organizations[0].organization;
                                        $rootScope.data.authUser.organizations = res.organizations;
                                    } else {
                                        $rootScope.data.authUser.organizationId = null;
                                        $rootScope.data.authUser.organizations = [
                                        ];
                                    }
                                    dfd.resolve(gscDatacat.Response.getSuccess(
                                        $rootScope.data.authUser, 'Remote login succeeded'));
                                } else {
                                    dfd.reject(gscDatacat.Response.getError({
                                        parameters: res.request
                                    },
                                        res.description));
                                }
                            }, function(errMsg) {
                                $rootScope.console.debug(errMsg);
                                dfd.reject(gscDatacat.Response.getError({
                                    parameters: {
                                        username: username,
                                        password: password
                                    }
                                }, 'Could not perform login with supplied details'));
                            });
                    }

                    return dfd.promise;
                };

                var _logout = function() {
                    $rootScope.console.log('Log out succeeded');
                    gsc.util.clearExtendObject($rootScope.data.authUser, {});
                    $state.go('app.loginForm');
                    return gscDatacat.Response.getSuccess(null, 'Successfully logged out');
                };

                /**
                 * Checks if user is authenticated
                 *
                 * @returns {gscDatacat.Response}
                 */
                var _isAuth = function() {
                    if ($rootScope.data.authUser.username !== undefined &&
                        $rootScope.data.authUser.username !== null) {
                        return gscDatacat.Response.getSuccess(jQuery.extend({},
                            $rootScope.data.authUser), 'Auhtenticated user exists');
                    } else {
                        return gscDatacat.Response.getError('No authenticated user exists');
                    }
                };

                /**
                 * Check if a user is allowed to perform a specific function or not
                 *
                 * @param {Number} functionId - One of the constants specified by authSvc.systemFunctions
                 * @returns {Boolean}
                 */
                var _userCan = function(functionId) {

                    var u = $rootScope.data.authUser;

                    if (u !== undefined &&
                        u.roles !== undefined &&
                        jQuery.isArray(u.roles)) {

                        for (var i = 0; i < u.roles.length; i++) {
                            var r = u.roles[i];
                            if (jQuery.isArray(r.functions)) {
                                for (var f = 0; f < r.functions.length; f++) {
                                    if (r.functions[f].idfunction === functionId) {
                                        return true;
                                    }
                                }
                            }
                        }

                    }

                    return false;

                };

                return {
                    login: _login,
                    logout: _logout,
                    isAuth: _isAuth,
                    authUsr: $rootScope.data.authUser,
                    sampleUsr: _sampleUser,
                    can: _userCan,
                    systemFunctions: $rootScope.data.systemFunctions,
                    canLockUser: function() {
                        return _userCan($rootScope.data.systemFunctions.LockUser);
                    },
                    canDeleteUser: function() {
                        return _userCan($rootScope.data.systemFunctions.DeleteUser);
                    },
                    canAssignUserToRole: function() {
                        return _userCan($rootScope.data.systemFunctions.AssignUserToRole);
                    },
                    canAssignPermissionToRole: function() {
                        return _userCan($rootScope.data.systemFunctions.AssignPermissionToRole);
                    },
                    canCreateOrganization: function() {
                        return _userCan($rootScope.data.systemFunctions.CreateOrganization);
                    },
                    canUpdateOrganization: function() {
                        return _userCan($rootScope.data.systemFunctions.UpdateOrganization);
                    },
                    canDeleteOrganization: function() {
                        return _userCan($rootScope.data.systemFunctions.DeleteOrganization);
                    }
                };

            }]);
