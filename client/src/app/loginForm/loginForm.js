/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
        .
        controller('loginFormCtrl', [
            'authSvc',
            '$scope',
            '$rootScope',
            '$state',
            function (
                    authSvc,
                    $scope,
                    $rootScope,
                    $state) {

                $scope.$on('$stateChangeSuccess', function () {
                    if (authSvc.isAuth().success === true) {
                        $rootScope.console.log('Already authenticated, redirecting');
                        $scope.redirect();
                    }
                });

                $scope.authSvc = authSvc;

                $scope.loginData = {
                    username: '' + authSvc.sampleUsr.username,
                    password: '' + authSvc.sampleUsr.password
                };

                $scope.login = function () {
                    var res = authSvc.login($scope.loginData.username, $scope.loginData.password);
                    if (res.success === true) {
                        $rootScope.console.log('Authenticated');
                        $scope.redirect();
                    } else {
                        $rootScope.console.log('Not authenticated');
                    }
                };

                $scope.redirect = function () {
                    $state.go('app.createDataSource');
                };

            }]);
