/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('createFunctionCtrl', [
        '$scope',
        '$rootScope',
        'dataSvc',
        'authSvc',
        '$window',
        function(
            $scope,
            $rootScope,
            dataSvc,
            authSvc,
            $window) {

            $scope.data = {
                currentFunction: {},
                functions: [],
                organizations: []
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

            var _loadFunctions = function() {
                $rootScope.console.todo(
                    'The function that lists functions does not return type...');
                dataSvc.loadFunctions()
                    .then(function(functions) {
                        gsc.util.clearExtendArray($scope.data.functions, functions);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            var _loadOrganizations = function() {
                dataSvc.loadOrganizations()
                    .then(function(organizations) {
                        gsc.util.clearExtendArray($scope.data.organizations, organizations);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            $scope.init = function() {
                _loadOrganizations();
                _loadFunctions();
            };

            $scope.save = function() {

                $rootScope.console.todo(
                    'The function web service does not support quotes in strings...');
                if ($scope.isUpdate()) {
                    gsc.function.update($scope.data.currentFunction.idfunction,
                        $scope.data.currentFunction.functionname,
                        $scope.data.currentFunction.organization,
                        $scope.data.currentFunction.type,
                        $scope.data.currentFunction.description)
                        .then(function(res) {
                            if (res.status !== 'error') {
                                _activateTab(0);
                                $rootScope.console.log('Updated function');
                                _loadFunctions();
                            } else {
                                $rootScope.console.log(
                                    'An error occurred while updating function');
                                $rootScope.console.log(res);
                            }
                        }, function(err) {
                            $rootScope.console.log('An error occurred while updating function');
                        });
                } else {
                    gsc.function.create($scope.data.currentFunction.functionname,
                        $scope.data.currentFunction.organization,
                        $scope.data.currentFunction.type,
                        $scope.data.currentFunction.description)
                        .then(function(res) {
                            if (res.status !== 'error') {
                                _activateTab(0);
                                $rootScope.console.log('Created new function');
                                $rootScope.console.log(res);
                                _loadFunctions();
                            } else {
                                $rootScope.console.log(
                                    'An error occurred while creating new function');
                                $rootScope.console.log(res);
                            }
                        }, function(err) {
                            $rootScope.console.log(
                                'An error occurred while creating new function');
                        });
                }
            };

            $scope.delete = function(functionId) {
                if ($window.confirm('Are you sure you would like to delete the function?')) {
                    gsc.function.delete(functionId)
                        .then(function(res) {
                            if (res.status !== 'error') {
                                $rootScope.console.log('Successfully deleted function');
                                _loadFunctions();
                            } else {
                                $rootScope.console.log(
                                    'An error occurred while loading functions');
                                $rootScope.console.log(res);
                            }
                        }, function(err) {
                            $rootScope.console.log('An error occurred while loading functions');
                        });
                }
            };

            $scope.edit = function(functionData) {
                _activateTab(1);
                gsc.util.clearExtendObject($scope.data.currentFunction,
                    functionData);
            };

            $scope.isUpdate = function() {
                if (gsc.util.isNumber($scope.data.currentFunction.idfunction)) {
                    return true;
                } else {
                    return false;
                }
            };

            $scope.resetCurrent = function() {
                gsc.util.clearExtendObject($scope.data.currentFunction, {});
            };

        }]);
