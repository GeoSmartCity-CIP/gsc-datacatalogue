/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
controller('createDataSourceCtrl', [
    '$window',
    '$scope',
    '$rootScope',
    '$http',
    function(
        $window,
        $scope,
        $rootScope,
        $http) {

        $scope.dataSource = {};

        $scope.dataSources = [];

        $scope.organizations = [];

        $scope.$on('$stateChangeSuccess', function() {
            _do();
        });

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

        var _loadDataSources = function() {

            gsc.datasource.list(null, $rootScope.data.loginData.organizationId, null, true)
                .then(function(res) {
                    if (gsc.util.isArrayWithContent(res.datasources)) {
                        gsc.util.clearExtendArray($scope.dataSources, res.datasources);
                    } else {}
                    $scope.$apply();
                    $rootScope.console.log('Loaded data sources');
                    $rootScope.console.log(res);
                    $rootScope.console.error('No data sources');
                });
        };

        var _loadOrganizations = function() {
            gsc.organization.list().then(function(res) {
                if (gsc.util.isArrayWithContent(res.organizations)) {
                    gsc.util.clearExtendArray($scope.organizations, res.organizations);
                    $scope.$apply();
                    $rootScope.console.log('Loaded organizations');
                    $rootScope.console.log(res);
                }
            });
        };

        var _do = function() {

            _loadDataSources();
            _loadOrganizations();

        };

        $scope.delete = function(datasourceId) {
            if ($window.confirm('Are you sure you want to delete the data source?')) {
                gsc.datasource.delete(datasourceId)
                    .then(function(res) {
                        $rootScope.console.log('Deleted data source');
                        $rootScope.console.log(res);
                        _loadDataSources();
                    });
            }
        };

        $scope.edit = function(datasourceId) {

            _activateTab(1);

            gsc.datasource.list(datasourceId, null, null, true)
                .then(function(res) {
                    if (res.datasources !== undefined && jQuery.isArray(res.datasources)) {
                        jQuery.extend($scope.dataSource, res.datasources[0]);
                        $scope.$apply();
                        $rootScope.console.log('Loaded data source for editing');
                    }
                    $rootScope.console.log(res);
                });

        };

        $scope.isUpdate = function() {
            if (jQuery.isNumeric($scope.dataSource.id)) {
                return true;
            } else {
                return false;
            }
        };

        var _create = function() {
            return gsc.datasource.create($scope.dataSource.datasourcename,
                    $scope.dataSource.organization,
                    $scope.dataSource.type,
                    $scope.dataSource.description,
                    new Date(),
                    $scope.dataSource.url,
                    $scope.dataSource.userName,
                    $scope.dataSource.password,
                    $scope.dataSource.ipaddress,
                    $scope.dataSource.schema,
                    $scope.dataSource.port,
                    $scope.dataSource.path)
                .then(function(res) {
                    _loadDataSources();
                    $rootScope.console.log('Inserted new datasource');
                    $rootScope.console.log(res);
                });
        };

        $scope.resetDataSource = function() {
            jQuery.each($scope.dataSource, function(key, val) {
                $scope.dataSource[key] = undefined;
            });
        };

        var _update = function() {
            return gsc.datasource.update($scope.dataSource.id,
                    $scope.dataSource.datasourcename,
                    $scope.dataSource.organization,
                    $scope.dataSource.type,
                    $scope.dataSource.description,
                    new Date(),
                    $scope.dataSource.url,
                    $scope.dataSource.username,
                    $scope.dataSource.password,
                    $scope.dataSource.ipaddress,
                    $scope.dataSource.schema,
                    $scope.dataSource.port,
                    $scope.dataSource.path)
                .then(function(res) {
                    _loadDataSources();
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
