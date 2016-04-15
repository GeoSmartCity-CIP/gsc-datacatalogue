/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
controller('createDataSetCtrl', [
    '$window',
    '$scope',
    '$rootScope',
    function(
        $window,
        $scope,
        $rootScope) {

        $scope.data = {};

        $scope.data.dataSourceId = '';

        $scope.dataSources = [];

        $scope.dataOrigin = [];

        $scope.columns = [];

        $scope.dataSet = {};

        $scope.dataSets = [];

        $scope.$on('$stateChangeSuccess', function() {
            _do();
        });

        var _loadDataSets = function() {

            $rootScope.console.log($scope.data.dataSourceId);

            gsc.dataset.list($scope.data.dataSourceId)
                .then(function(res) {
                    if (res.dataset !== undefined && jQuery.isArray(res.dataset)) {
                        $scope.dataSets.length = 0;
                        jQuery.extend($scope.dataSets, res.dataset);
                    }
                    $scope.$apply();
                    $rootScope.console.log('Loaded data sets');
                    $rootScope.console.log(res);
                });

        };

        var _loadDataSources = function() {

            gsc.datasource.list(null, $rootScope.data.loginData.organizationId, null, true)
                .then(function(res) {
                    if (res.datasources !== undefined && jQuery.isArray(res.datasources)) {
                        $scope.dataSources.length = 0;
                        jQuery.extend($scope.dataSources, res.datasources);
                    }
                    $scope.$apply();
                    $rootScope.console.log('Loaded data sources');
                    $rootScope.console.log(res);
                });
        };

        var _do = function() {

            _loadDataSets();
            _loadDataSources();

        };

        var _loadDataOrigin = function() {
            return gsc.datasource.listDataOrigin($scope.dataSet.iddatasource)
                .then(function(res) {
                    $scope.dataOrigin.length = 0;
                    if (res.dataorigin !== undefined && jQuery.isArray(res.dataorigin)) {
                        jQuery.extend($scope.dataOrigin, res.dataorigin);
                        $scope.$apply();
                        $rootScope.console.log('Loaded data origins from data source');
                    }
                    $rootScope.console.log(res);
                });
        };

        var _loadColList = function() {
            return gsc.dataset.listCols($scope.dataSet.iddataset)
                .then(function(res) {
                    if (res.columns !== undefined && jQuery.isArray(res.columns) && res.columns.length > 0) {
                        $scope.columns.length = 0;
                        jQuery.extend($scope.columns, res.columns);
                        $scope.$apply();
                        $rootScope.console.log('Loaded columns');
                    }
                    $rootScope.console.log(res);
                });
        };

        var _updateDataset = function() {
            return gsc.dataset.update(
                    $scope.dataSet.iddataset,
                    $scope.dataSet.datasetname,
                    $scope.dataSet.realname,
                    $scope.dataSet.iddatasource,
                    $scope.dataSet.description,
                    $scope.dataSet.tobeingested,
                    $scope.dataSet.refreshinterval,
                    $scope.dataSet.url
                )
                .then(function(res) {
                    _loadDataSets();
                    $rootScope.console.log('Updated existing dataset');
                    $rootScope.console.log(res);
                });
        };

        var _createDataset = function() {
            return gsc.dataset.create(
                    $scope.dataSet.datasetname,
                    $scope.dataSet.realname,
                    $scope.dataSet.iddatasource,
                    $scope.dataSet.description,
                    $scope.dataSet.tobeingested,
                    $scope.dataSet.refreshinterval,
                    $scope.dataSet.url
                )
                .then(function(res) {
                    _loadDataSets();
                    $rootScope.console.log('Inserted new data set');
                    $rootScope.console.log(res);
                });
        };

        var _loadDataSet = function(dataSetId) {
            return gsc.dataset.list(null, dataSetId, null)
                .then(function(res) {
                    if (res.dataset !== undefined && jQuery.isArray(res.dataset)) {
                        jQuery.extend($scope.dataSet, res.dataset[0]);
                        $scope.$apply();
                        $rootScope.console.log('Loaded data set for editing');
                    }
                    $rootScope.console.log(res);
                });
        };

        $scope.tabs = [{
            active: true
        }, {
            active: false
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

        $scope.filterByDataSource = function() {
            _loadDataSets();
        };

        $scope.delete = function(dataSetId) {
            if ($window.confirm('Are you sure you want to delete the data set?')) {
                gsc.dataset.delete(dataSetId)
                    .then(function(res) {
                        $rootScope.console.log('Deleted data set');
                        $rootScope.console.log(res);
                        _loadDataSets();
                    });
            }
        };

        $scope.edit = function(dataSetId) {
            _activateTab(1);
            _loadDataSet(dataSetId);
            _loadDataOrigin();
        };

        $scope.columnTabDisabled = function() {
            if (jQuery.isNumeric($scope.dataSet.iddataset)) {
                return false;
            } else {
                return true;
            }
        };

        $scope.isUpdate = function() {
            if (jQuery.isNumeric($scope.dataSet.iddataset)) {
                return true;
            } else {
                return false;
            }
        };

        $scope.resetDataSet = function() {
            jQuery.each($scope.dataSet, function(key, val) {
                $scope.dataSet[key] = undefined;
            });
        };

        $scope.selectDataSource = function() {
            _loadDataOrigin();
        };

        $scope.selectDataOrigin = function() {
            _loadColList();
        };

        $scope.updateColumns = function() {
            gsc.dataset.updateCols($scope.dataSet.iddataset,
                    $scope.columns)
                .then(function(res) {
                    $rootScope.console.log('Updated columns');
                    $rootScope.console.log(res);
                });
        };

        $scope.save = function() {
            _activateTab(0);
            if ($scope.isUpdate()) {
                _updateDataset();
            } else {
                _createDataset();
            }
        };

    }
]);
