/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
controller('createLayerCtrl', [
    '$window',
    '$scope',
    '$rootScope',
    function(
        $window,
        $scope,
        $rootScope) {

        $scope.layer = {};
        $scope.layers = [];
        $scope.dataSets = [];

        $scope.tabs = [{
            active: true
        }, {
            active: false
        }];

        $scope.$on('$stateChangeSuccess', function() {
            _do();
        });

        var _activateTab = function(tabIndex) {
            for (var i = 0; i < $scope.tabs.length; i++) {
                if (i == tabIndex) {
                    $scope.tabs[i].active = true;
                } else {
                    $scope.tabs[i].active = false;
                }
            }
        };

        var _do = function() {
            _loadDataSets();
            _loadLayers();
        };

        var _loadLayers = function() {

            gsc.layer.list()
                .then(function(res) {

                    if (gsc.util.isArrayWithContent(res.layers)) {
                        gsc.util.clearExtendArray($scope.layers, res.layers);
                        //$scope.$apply();
                        $rootScope.console.log('(Re)loaded layers');
                    }

                    $rootScope.console.log(res);

                });

        };

        var _edit = function(layerId) {
            _activateTab(1);
            return gsc.layer.list(null, layerId)
                .then(function(res) {

                    if (gsc.util.isArrayWithContent(res.layers)) {
                        gsc.util.clearExtendObject($scope.layer, res.layers[0]);
                        //$scope.$apply();
                        $rootScope.console.log('Loaded layer for editing');
                    }
                    $rootScope.console.log(res);
                });
        };

        var _delete = function(layerId) {
            if ($window.confirm('Are you sure you would like to delete the selected layer?')) {
                return gsc.layer.delete(layerId).then(function(res) {

                    $rootScope.console.log(res);
                });
            }

        };

        var _loadDataSets = function() {

            gsc.dataset.list()
                .then(function(res) {
                    if (res.dataset !== undefined && jQuery.isArray(res.dataset)) {
                        $scope.dataSets.length = 0;
                        jQuery.extend($scope.dataSets, res.dataset);
                    }
                    //$scope.$apply();
                    $rootScope.console.log('Loaded data sets');
                    $rootScope.console.log(res);
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
                    $rootScope.console.log('Created new layer');
                    _loadLayers();
                    $rootScope.console.log(res);
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
                    $rootScope.console.log('Updated existing layer');
                    _loadLayers();
                    $rootScope.console.log(res);
                });
        };

        var _save = function() {

            if (_isUpdate()) {
                _update();
            } else {
                _create();
            }
            _activateTab(0);

        };

        $scope.save = _save;

        $scope.edit = _edit;

        $scope.delete = _delete;

        $scope.isUpdate = _isUpdate;

    }
]);
