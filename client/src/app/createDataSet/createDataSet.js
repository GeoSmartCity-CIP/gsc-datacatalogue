/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('createDataSetCtrl', [
        '$window',
        '$scope',
        '$rootScope',
        'authSvc',
        'dataSvc',
        function(
            $window,
            $scope,
            $rootScope,
            authSvc,
            dataSvc) {

            $scope.data = {};

            $scope.data.dataSourceId = '';

            $scope.dataSources = [];

            $scope.dataOrigin = [];

            $scope.columns = [];

            $scope.dataSet = {};

            $scope.dataSets = [];

            var _loadDataSets = function() {
                dataSvc.loadDataSets($scope.data.dataSourceId)
                    .then(function(dataSets) {
                        $scope.dataSets.length = 0;
                        jQuery.extend($scope.dataSets, dataSets);
                        $rootScope.console.log('Loaded data sets');
                    }, function(err) {
                        $rootScope.console.log('Error loading data sets');
                        $rootScope.console.log(err.statusText);
                    });
            };

            var _loadDataSources = function() {

                dataSvc.loadDataSources()
                    .then(function(datasources) {
                        $scope.dataSources.length = 0;
                        jQuery.extend($scope.dataSources, datasources);
                        $rootScope.console.log('Loaded data sources');
                    }, function(err) {
                        $rootScope.console.log(err.statusText);
                    });
            };

            $scope.init = function() {

                _loadDataSets();
                _loadDataSources();

            };

            var _loadDataOrigin = function() {
                return dataSvc.loadDataOrigin($scope.dataSet.iddatasource)
                    .then(function(dataorigin) {
                        $scope.dataOrigin.length = 0;
                        jQuery.extend($scope.dataOrigin, dataorigin);
                        $rootScope.console.log('Loaded data origins from data source');
                    }, function(errorMessage) {
                        $rootScope.console.log(errorMessage);
                    });
            };

            var _loadColList = function() {
                dataSvc.listDataSetColumns($scope.dataSet.iddataset)
                    .then(function(columns) {
                        $scope.columns.length = 0;
                        jQuery.extend($scope.columns, columns);
                        $rootScope.console.log('Loaded columns');
                        $rootScope.console.log(columns);
                    }, function(errorMessage) {
                        $rootScope.console.log(errorMessage);
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
                    }, function(errorMessage) {
                        $rootScope.console.log(errorMessage);
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
                        if (res.status !== 'error') {
                            _loadDataSets();
                            $rootScope.console.log('Inserted new data set');
                        } else {
                            $rootScope.console.log('Error inserting new data set');
                            $rootScope.console.log(res);
                        }
                    }, function(err) {
                        $rootScope.console.log(
                            'An error occurred during creation of new data set');
                        $rootScope.console.log(err.statusText);
                    });
            };

            var _loadDataSet = function(dataSetId) {
                return dataSvc.loadDataSet(dataSetId)
                    .then(function(res) {
                        jQuery.extend($scope.dataSet, res);
                        _loadDataOrigin();
                        $rootScope.console.log('Loaded data set and data origin for editing');
                        $rootScope.console.log(res);
                    }, function(errorMessage) {
                        $rootScope.console.log(errorMessage);
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
