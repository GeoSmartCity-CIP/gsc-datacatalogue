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
                username: '',
                password: ''
            };

            $scope.login = function() {
                var res = authSvc.login($scope.loginData.username,
                    $scope.loginData.password)
                    .then(function(userData) {
                        $rootScope.console.info('Authenticated');
                        $scope.redirect();
                    }, function(errMsg) {
                        $rootScope.console.error('Not authenticated');
                        $rootScope.console.debug(errMsg);
                    });
            };

            $scope.remind = function() {
                gsc.user.remindPassword($scope.loginData.username, null)
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.info('Authentication succeded');
                        } else {
                            $rootScope.console.usrWarn(res.description);
                        }
                    }, function(err) {
                        $rootScope.console.usrWarn('Authentication failed');
                        $rootScope.console.error(err);
                    });
            };

            $scope.redirect = function() {
                $state.go('app.createDataSource');
            };

        }]);
