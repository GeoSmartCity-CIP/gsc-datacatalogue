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

                _loadFunctions();
                _loadRoles();
                _loadLayers();
                _loadApplications();
            };

            var _loadFunctions = function() {
                dataSvc.loadFunctions()
                    .then(function(functions) {
                        gsc.util.clearExtendArray($scope.data.functions, functions);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });

            };

            var _loadRoles = function() {
                dataSvc.loadRoles()
                    .then(function(roles) {
                        gsc.util.clearExtendArray($scope.data.roles, roles);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            var _loadLayers = function() {
                dataSvc.loadLayers()
                    .then(function(layers) {
                        gsc.util.clearExtendArray($scope.data.layers, layers);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });

            };

            var _loadApplications = function() {
                dataSvc.loadApplications()
                    .then(function(applications) {
                        gsc.util.clearExtendArray($scope.data.applications, applications);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            var _loadPermissions = function(roleId) {
                return dataSvc.loadPermissions(roleId)
                    .then(function(permissions) {
                        $scope.data.currentRole.functions = $scope.data.currentRole.functions || [
                        ];
                        gsc.util.clearExtendArray($scope.data.currentRole.functions, permissions);
                    }, function(errMsg) {
                        $rootScope.console.debug(errMsg);
                    });
            };

            $scope.assignPermissions = function(roleData) {
                gsc.util.clearExtendObject($scope.data.currentRole, roleData);
                _loadPermissions(roleData.id).then(function(res) {
                    _activateTab(1);
                });
            };

            $scope.addPermission = function() {
                $scope.data.currentRole.functions = $scope.data.currentRole.functions || [];

                var permission = {};

                if (!gsc.util.isNull($scope.data.currentPermission.function)) {
                    permission.idfunction = +$scope.data.currentPermission.function.idfunction;
                    permission.functionname = $scope.data.currentPermission.function.functionname;
                }
                if (!gsc.util.isNull($scope.data.currentPermission.layer)) {
                    permission.idlayer = +$scope.data.currentPermission.layer.id;
                    permission.layername = $scope.data.currentPermission.layer.layername;
                }
                if (!gsc.util.isNull($scope.data.currentPermission.application)) {
                    permission.idapplication = +$scope.data.currentPermission.application.idapplication;
                    permission.applicationname = $scope.data.currentPermission.application.applicationname;
                }

                if (gsc.util.isNumber(permission.idfunction) && (
                    gsc.util.isNumber(permission.idlayer) ||
                    gsc.util.isNumber(permission.idapplication))) {
                    $scope.data.currentRole.functions.push(permission);
                } else {
                    $rootScope.console.usrWarn(
                        'You must specify a function and either a layer or an application to add a new permission');
                }
            };

            $scope.removePermission = function(index) {
                $scope.data.currentRole.functions.splice(index, 1);
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

            var _update = function() {
                gsc.permission.assign(
                    $scope.data.currentRole.id,
                    $scope.data.currentRole.functions)
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.usrInfo('Successfully assigned permissions');
                            _loadRoles();
                        } else {
                            $rootScope.console.error("An error occurred assigning permissions");
                            $rootScope.console.debug(res);
                            $rootScope.console.usrWarn(res.description);
                        }
                    }, function(err) {
                        $rootScope.console.error("An error occurred assigning permissions");
                        $rootScope.console.debug(err);
                    });
            };

            $scope.save = function() {
                if ($scope.isUpdate()) {
                    _update();
                }
            };
        }
    ]);
