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

                var _sampleUser = {
                    username: 'admin@geosmartcity.eu',
                    password: 'geosmartcity',
                    organizationId: 666
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
                                $rootScope.console.log(res);
                                if (res.iduser !== undefined && gsc.util.isNumber(res.iduser)) {
                                    gsc.util.clearExtendObject($rootScope.data.authUser, {});
                                    $rootScope.data.authUser.username = res.username;
                                    $rootScope.data.authUser.iduser = +res.iduser;
                                    if (res.organizations.length > 0) {
                                        $rootScope.data.authUser.organizationId = +res.organizations[0].organization;
                                        $rootScope.data.authUser.organizations = res.organizations;
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

                return {
                    login: _login,
                    logout: _logout,
                    isAuth: _isAuth,
                    authUsr: $rootScope.data.authUser,
                    sampleUsr: _sampleUser
                };
            }]);
