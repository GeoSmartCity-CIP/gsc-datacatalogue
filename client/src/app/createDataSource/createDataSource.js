/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .controller('createDataSourceCtrl', [
        '$window',
        '$scope',
        '$rootScope',
        '$http',
        'authSvc',
        'dataSvc',
        '$state',
        function(
            $window,
            $scope,
            $rootScope,
            $http,
            authSvc,
            dataSvc,
            $state) {

            $scope.data.currentDataSource = {};
            $scope.data.dataSources = [];
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

                dataSvc.loadOrganizations()
                    .then(function(res) {
                        gsc.util.clearExtendArray($scope.data.organizations, res);
                    });
                    
                dataSvc.loadDataSources()
                    .then(function(res) {
                        gsc.util.clearExtendArray($scope.data.dataSources, res);
                    });
            };

            $scope.delete = function(datasourceId) {
                if ($window.confirm('Are you sure you want to delete the data source?')) {
                    gsc.datasource.delete(datasourceId)
                        .then(function(res) {
                            $rootScope.console.log('Deleted data source');
                            $rootScope.console.log(res);
                            $scope.init();
                        });
                }
            };

            $scope.edit = function(datasourceId) {
                _activateTab(1);
                dataSvc.loadDataSource(datasourceId)
                    .then(function(dataSource) {
                        console.log(dataSource);
                        jQuery.extend($scope.data.currentDataSource, dataSource);
                        $rootScope.console.log('Loaded data source for editing');
                    }, function(err) {
                        $rootScope.console.log(err);
                    });
            };

            $scope.isUpdate = function() {
                if (jQuery.isNumeric($scope.data.currentDataSource.id)) {
                    return true;
                } else {
                    return false;
                }
            };
            var _create = function() {
                return gsc.datasource.create($scope.data.currentDataSource.datasourcename,
                    $scope.data.currentDataSource.organization,
                    $scope.data.currentDataSource.type,
                    $scope.data.currentDataSource.description,
                    new Date(),
                    $scope.data.currentDataSource.url,
                    $scope.data.currentDataSource.userName,
                    $scope.data.currentDataSource.password,
                    $scope.data.currentDataSource.ipaddress,
                    $scope.data.currentDataSource.schema,
                    $scope.data.currentDataSource.port,
                    $scope.data.currentDataSource.path)
                    .then(function(res) {
                        $scope.init();
                        $rootScope.console.log('Inserted new datasource');
                        $rootScope.console.log(res);
                    });
            };
            $scope.resetDataSource = function() {
                jQuery.each($scope.data.currentDataSource, function(key, val) {
                    $scope.data.currentDataSource[key] = undefined;
                });
            };
            var _update = function() {
                return gsc.datasource.update($scope.data.currentDataSource.id,
                    $scope.data.currentDataSource.datasourcename,
                    $scope.data.currentDataSource.organization,
                    $scope.data.currentDataSource.type,
                    $scope.data.currentDataSource.description,
                    new Date(),
                    $scope.data.currentDataSource.url,
                    $scope.data.currentDataSource.username,
                    $scope.data.currentDataSource.password,
                    $scope.data.currentDataSource.ipaddress,
                    $scope.data.currentDataSource.schema,
                    $scope.data.currentDataSource.port,
                    $scope.data.currentDataSource.path)
                    .then(function(res) {
                        $scope.init();
                        $rootScope.console.log('Updated existing datasource');
                        $rootScope.console.log(res);
                    });
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
