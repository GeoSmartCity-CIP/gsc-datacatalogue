/* global admBoard, angular */
'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('createPermissionCtrl', [
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

            $scope.data = {
                roles: [],
                functions: [],
                applications: [],
                layers: [],
                currentPermission: {},
                currentRole: {}
            };

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
                        gsc.util.clearExtendArray($scope.data.roles, roles);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });

                dataSvc.loadFunctions()
                    .then(function(functions) {
                        gsc.util.clearExtendArray($scope.data.functions, functions);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });

                dataSvc.loadLayers()
                    .then(function(layers) {
                        gsc.util.clearExtendArray($scope.data.layers, layers);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
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

            $scope.assignPermissions = function(roleData) {
                dataSvc.loadPermissions(roleData.id)
                    .then(function(roles) {
                        gsc.util.clearExtendArray($scope.data.roles, roles);
                        jQuery.extend($scope.data.currentRole, roleData);
                        _activateTab(1);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });

            };

            $scope.isUpdate = function() {
                if (jQuery.isNumeric($scope.data.currentRole.id)) {
                    return true;
                } else {
                    return false;
                }
            };

            $scope.resetCurrent = function() {
                gsc.util.clearExtendObject($scope.data.currentRole, {});
            };

            var _create = function() {
                return gsc.role.register(
                    $scope.data.currentRole.rolename,
                    $scope.data.currentRole.organization,
                    $scope.data.currentRole.description
                    ).then(function(res) {
                    if (res.status !== 'error') {
                        dataSvc.loadDataSources();
                        $rootScope.console.log('Created new permission');
                        $scope.init();
                    } else {
                        $rootScope.console.log('An error occurred while creating permission');
                        $rootScope.console.log(res);
                    }
                }, function(err) {
                    $rootScope.console.log('An error occurred while creating permission');
                    $rootScope.console.log(err);
                });
            };

            var _update = function() {
                $rootScope.console.todo(
                    'There is no update function for roles in the server-side API');
            };

            $scope.save = function() {
                if ($scope.isUpdate()) {
                    _update();
                } else {
                    _create();
                }
            };
        }
    ]);
