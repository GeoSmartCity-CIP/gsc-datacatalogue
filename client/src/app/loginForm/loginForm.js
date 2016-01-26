/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
        .
        controller('loginFormCtrl', [
            '$scope',
            '$rootScope',
            function(
                    $scope,
                    $rootScope) {

                $scope.loginData = {};

                $scope.doLogin = function() {

                    if ($scope.loginData.userName === 'test@test.com' && $scope.loginData.password === 'test') {
                        $rootScope.authUser = $scope.loginData.userName;
                    } else {
                        console.log('Authentication failed');
                    }

                };

                $scope.doLogout = function() {

                    $rootScope.authUser = null;

                };

            }]);
