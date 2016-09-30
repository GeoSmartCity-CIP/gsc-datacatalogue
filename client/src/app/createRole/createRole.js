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
                if ($window.confirm('Are you sure you want to delete the data source?')) {
                    gsc.role.delete(roleId)
                        .then(function(res) {
                            $rootScope.console.log('Deleted data source');
                            $rootScope.console.log(res);
                            $scope.init();
                        });
                }
            };

            $scope.edit = function(organizationId) {

                gsc.role.listrole(organizationId, true, true)
                    .then(function(res) {
                        $rootScope.console.log(res);
                        if (res.datasources !== undefined &&
                            jQuery.isArray(res.datasources)) {
                            jQuery.extend($scope.data.currentRole, res.roles[0]);
                            $rootScope.console.log('Loaded data source for editing');
                            _activateTab(1);
                        }
                    });
            };

            $scope.isUpdate = function() {
                if (jQuery.isNumeric($scope.data.currentRole.id)) {
                    return true;
                } else {
                    return false;
                }
            };

            $scope.resetRole = function() {
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
                    dataSvc.loadDataSources();
                    $rootScope.console.log('Inserted new datasource');
                    $rootScope.console.log(res);
                    $scope.init();
                }, function(err) {
                    $rootScope.console.log(err.statusText);
                });
            };

            var _update = function() {
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
