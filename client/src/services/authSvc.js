/* global angular, gscDatacat */

'use strict';
angular.module('gscDatacat.services')
        .factory('authSvc', ['$rootScope', '$state',
            function ($rootScope, $state) {

                $rootScope.data = $rootScope.data || {};

                $rootScope.data = {};

                $rootScope.data.authUser = {};

                var _sampleUser = {
                    username: 'admin@geosmartcity.eu',
                    password: 'geosmartcity',
                    organizationId: 666
                };

                var _login = function (username, password) {

                    if (username === _sampleUser.username &&
                            password === _sampleUser.password) {
                        gsc.util.clearExtendObject($rootScope.data.authUser, _sampleUser);
                        return  gscDatacat.Response.getSuccess($rootScope.data.authUser,
                                "Login succeeded");
                    } else {
                        return gscDatacat.Response.getError({
                            parameters: {
                                username: username,
                                password: password
                            }
                        }, "Could not login with supplied details");
                    }
                };

                var _logout = function () {
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
                var _isAuth = function () {
                    if ($rootScope.data.authUser.username !== undefined &&
                            $rootScope.data.authUser.username !== null) {
                        return gscDatacat.Response.getSuccess(jQuery.extend({}, $rootScope.data.authUser), "Auhtenticated user exists");
                    } else {
                        return gscDatacat.Response.getError("No authenticated user exists");
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
