/* global admBoard, angular */
'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('createRoleCtrl', [
        '$window',
        '$scope',
        '$rootScope',
        '$http',
        'authSvc',
        'dataSvc',
        '$q',
        function(
            $window,
            $scope,
            $rootScope,
            $http,
            authSvc,
            dataSvc,
            $q) {

            $scope.data.roles = [];

            $scope.data.currentRole = {};

            $scope.data.organizations = [];

            $scope.tabs = [{
                    active: true
                }, {
                    active: false
                }];

            var _activateTab = function(tabIndex) {
                for (var i = 0; i < $scope.tabs.length; i++) {
                    if (i == tabIndex) {
                        $scope.tabs[i].active = true;
                    } else {
                        $scope.tabs[i].active = false;
                    }
                }
            };

            $scope.init = function() {

                dataSvc.loadRoles()
                    .then(function(roles) {
                        $scope.data.roles = roles;
                    }, function(err) {
                        console.log(err.statusText);
                    });

                dataSvc.loadOrganizations()
                    .then(function(res) {
                        $scope.data.organizations = res;
                    }, function(err) {
                        console.log(err.statusText);
                    });
            };

            $scope.delete = function(roleId) {
                if ($window.confirm('Are you sure you want to delete the role?')) {
                    gsc.role.delete(roleId)
                        .then(function(res) {
                            if (res.status !== 'error') {
                                $rootScope.console.log('Deleted role');
                                $rootScope.console.log(res);
                                $scope.init();
                            } else {
                                $rootScope.console.log('An error occurred deleting role');
                                $rootScope.console.log(res);
                            }
                        }, function(err) {
                            $rootScope.console.log('An error occurred deleting role');
                            $rootScope.console.log(err);
                        });
                }
            };

            $scope.edit = function(roleData) {
                jQuery.extend($scope.data.currentRole, roleData);
                _activateTab(1);
            };

            $scope.isUpdate = function() {
                if (jQuery.isNumeric($scope.data.currentRole.id)) {
                    return true;
                } else {
                    return false;
                }
            };

            $scope.resetCurrent = function() {
                jQuery.each($scope.data.currentRole, function(key, val) {
                    $scope.data.currentRole[key] = undefined;
                });
            };

            var _create = function() {
                return gsc.role.register(
                    $scope.data.currentRole.rolename,
                    $scope.data.currentRole.organization,
                    $scope.data.currentRole.description
                    ).then(function(res) {
                    if (res.status !== 'error') {
                        $rootScope.console.log('Created role');
                        dataSvc.loadDataSources();
                        $scope.init();
                    } else {
                        $rootScope.console.log('An error occurred creating role');
                        $rootScope.console.log(res);
                    }
                }, function(err) {
                    $rootScope.console.log('An error occurred creating role');
                    $rootScope.console.log(err);
                });
            };

            var _update = function() {
                $rootScope.console.todo(
                    'There is no update function for roles in the server-side API');
            };

            $scope.save = function() {
                _activateTab(0);
                if ($scope.isUpdate()) {
                    _update();
                } else {
                    _create();
                }
            };
        }
    ]);
