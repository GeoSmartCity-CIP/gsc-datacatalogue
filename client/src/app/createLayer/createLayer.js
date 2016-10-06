/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('createLayerCtrl', [
        '$window',
        '$scope',
        '$rootScope',
        'dataSvc',
        function(
            $window,
            $scope,
            $rootScope,
            dataSvc) {

            $scope.layer = {};
            $scope.layers = [];
            $scope.dataSets = [];

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
                _loadDataSets();
                _loadLayers();

            };

            $scope.readMetadataFile = function(files) {
                var file = files[0];
                var reader = new FileReader();

                reader.onload = function() {
                    $scope.layer.metadatafile = this.result;
                    if (!$scope.$$phase) {
                        $scope.$apply();
                    }
                };

                reader.readAsText(file);
            };

            $scope.readSldFile = function(files) {
                var file = files[0];
                var reader = new FileReader();

                reader.onload = function() {
                    $scope.layer.sld = this.result;
                    if (!$scope.$$phase) {
                        $scope.$apply();
                    }
                };

                reader.readAsText(file);
            };

            var _loadLayers = function() {

                dataSvc.loadLayers()
                    .then(function(res) {
                        gsc.util.clearExtendArray($scope.layers, res);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            var _edit = function(layerId) {
                _activateTab(1);
                return dataSvc.loadLayer(layerId)
                    .then(function(layer) {
                        gsc.util.clearExtendObject($scope.layer, layer);
                        $rootScope.console.log('Loaded layer for editing');
                    }, function(errMsg) {
                        $rootScope.console.log(
                            'Somethiing went wrong while loading layer for editing');
                        $rootScope.console.log(errMsg);
                    });
            };

            var _delete = function(layerId) {
                if ($window.confirm('Are you sure you would like to delete the selected layer?')) {
                    return gsc.layer.delete(layerId)
                        .then(function(res) {
                            $rootScope.console.log(res);
                            _loadLayers();
                        });
                }
            };

            var _loadDataSets = function() {

                dataSvc.loadDataSets()
                    .then(function(dataset) {
                        gsc.util.clearExtendArray($scope.dataSets, dataset);
                        $rootScope.console.log('Loaded data sets');
                    }, function(errMsg) {
                        $rootScope.console.log('Error loading data sets');
                    });

            };

            var _isUpdate = function() {
                if (jQuery.isNumeric($scope.layer.id)) {
                    return true;
                } else {
                    return false;
                }
            };

            var _create = function() {

                return gsc.layer.create(
                    $scope.layer.layername,
                    $scope.layer.iddataset,
                    $scope.layer.description,
                    $scope.layer.metadatafile,
                    $scope.layer.sld)
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.log('Created new layer');
                            _loadLayers();
                            _activateTab(0);
                        } else {
                            $rootScope.console.log('An error occurred while creating layer');
                            $rootScope.console.log(res);
                        }
                    }, function(err) {
                        $rootScope.console.log('An error occurred while creating layer');
                        $rootScope.console.log(err);
                    });
            };

            var _update = function() {
                return gsc.layer.update(
                    $scope.layer.id,
                    $scope.layer.layername,
                    $scope.layer.iddataset,
                    $scope.layer.description,
                    $scope.layer.metadatafile,
                    $scope.layer.sld)
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.log('Updated existing layer');
                            _loadLayers();
                            _activateTab(0);
                        } else {
                            $rootScope.console.log('An error occurred while updating layer');
                            $rootScope.console.log(res);
                        }
                    }, function(err) {
                        $rootScope.console.log('An error occurred while updating layer');
                        $rootScope.console.log(err);
                    });
            };

            var _reset = function() {
                gsc.util.clearExtendObject($scope.layer);
                return;
            };

            var _save = function() {

                if (_isUpdate()) {
                    _update();
                } else {
                    _create();
                }
            };

            $scope.resetCols = function() {

            };

            $scope.save = _save;

            $scope.edit = _edit;

            $scope.delete = _delete;

            $scope.isUpdate = _isUpdate;

            $scope.reset = _reset;

        }
    ]);
