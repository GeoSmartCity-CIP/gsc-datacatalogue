/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('loginFormCtrl', [
        'authSvc',
        '$scope',
        '$rootScope',
        '$state',
        function(
            authSvc,
            $scope,
            $rootScope,
            $state) {

            $scope.$on('$stateChangeSuccess', function() {
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

            $scope.login = function() {
                var res = authSvc.login($scope.loginData.username,
                    $scope.loginData.password)
                    .then(function(userData) {
                        $rootScope.console.log('Authenticated');
                        $scope.redirect();
                    }, function(errMsg) {
                        $rootScope.console.log('Not authenticated');
                        $rootScope.console.log(errMsg);
                    });
            };

            $scope.remind = function() {
                gsc.user.remindPassword($scope.loginData.username, null)
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.log('Authentication succeded');
                        } else {
                            $rootScope.console.log('Authentication failed');
                            $rootScope.console.log(res.description);
                        }
                    }, function(errMsg) {
                        $rootScope.console.log('Authentication failed');
                        $rootScope.console.log(errMsg);
                    });
            };

            $scope.redirect = function() {
                $state.go('app.createDataSource');
            };

        }]);
