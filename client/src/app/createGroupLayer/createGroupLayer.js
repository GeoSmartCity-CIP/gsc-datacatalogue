/* global admBoard, angular, gsc */

'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('createGroupLayerCtrl', [
        '$window',
        '$scope',
        '$rootScope',
        'dataSvc',
        'authSvc',
        function(
            $window,
            $scope,
            $rootScope,
            dataSvc,
            authSvc) {

            $scope.data = {
                groupLayers: [],
                layers: [],
                organizations: [],
                currentGroupLayer: {
                    layers: [],
                    groups: []
                }
            };

            $scope.onDropComplete = function(data, evt) {
                $scope.data.currentGroupLayer.layers.push(data);
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

            var _create = function() {

                gsc.grouplayer.create($scope.data.currentGroupLayer.groupname,
                    authSvc.authUsr.organizationId,
                    $scope.data.currentGroupLayer.description
                    )
                    .then(function(createGroupLayerResponse) {
                        if (createGroupLayerResponse.status !== 'error') {
                            $rootScope.console.log('Created group layer');

                            if (gsc.util.isNumber(createGroupLayerResponse.id)) {
                                _assignLayers(createGroupLayerResponse.id)
                                    .then(function() {
                                        _loadGroupLayers();
                                        _activateTab(0);
                                    });
                            } else {
                                $rootScope.console.log('Should have been a number...');
                                _loadGroupLayers();
                                _activateTab(0);
                            }
                        } else {
                            $rootScope.console.log('An error occurred while creating group layer');
                            $rootScope.console.log(createGroupLayerResponse);
                        }
                    }, function(err) {
                        $rootScope.console.log('An error occurred while creating group layer');
                        $rootScope.console.log(err);
                    });
            };

            var _assignLayers = function(groupLayerId) {
                return gsc.grouplayer.assignLayers(groupLayerId,
                    $scope.data.currentGroupLayer.layers)
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.log('Assigned layers to group layer');
                        } else {
                            $rootScope.console.log(
                                'An error occurred while assigning layers to group layer');
                            $rootScope.console.log(res);
                        }
                    }, function(err) {
                        $rootScope.console.log(
                            'An error occurred while assigning layers to group layer');
                        $rootScope.console.log(err);
                    });
            };

            var _update = function() {
                gsc.grouplayer.update($scope.data.currentGroupLayer.id,
                    $scope.data.currentGroupLayer.groupname,
                    authSvc.authUsr.organizationId,
                    $scope.data.currentGroupLayer.description)
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.log('Updated group layer');
                            _assignLayers($scope.data.currentGroupLayer.id)
                                .then(function() {
                                    _loadGroupLayers();
                                    _activateTab(0);
                                }, function(err) {
                                    console.log('Should have been success');
                                    _loadGroupLayers();
                                    _activateTab(0);
                                });
                        } else {
                            $rootScope.console.log('An error occurred while updating group layer');
                            $rootScope.console.log(res);
                        }
                    }, function(err) {
                        $rootScope.console.log('An error occurred while updating group layer');
                        $rootScope.console.log(err);
                    });
            };

            $scope.addLayer = function(layer) {
                if ($scope.data.currentGroupLayer.layers === undefined) {
                    $scope.data.currentGroupLayer.layers = [];
                }
                $scope.data.currentGroupLayer.layers.push({
                    id: layer.id,
                    idlayer: layer.id,
                    layername: layer.layername
                });
            };

            $scope.removeLayer = function(index) {
                if ($scope.data.currentGroupLayer.layers === undefined) {
                    $scope.data.currentGroupLayer.layers = [];
                }
                if (index >= 0) {
                    $scope.data.currentGroupLayer.layers.splice(index, 1);
                }
            };

            $scope.isUpdate = function() {
                if (gsc.util.isNumber($scope.data.currentGroupLayer.id)) {
                    return true;
                } else {
                    return false;
                }
            };

            $scope.save = function() {

                if ($scope.isUpdate()) {
                    _update();
                } else {
                    _create();
                }

            };

            $scope.init = function() {
                _loadOrganizations();
                _loadLayers();
                _loadGroupLayers();
            };

            var _loadOrganizations = function() {
                dataSvc.loadOrganizations()
                    .then(function(organizations) {
                        gsc.util.clearExtendArray($scope.data.organizations, organizations);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            var _loadGroupLayers = function() {
                return dataSvc.loadGroupLayers()
                    .then(function(groupLayers) {
                        gsc.util.clearExtendArray($scope.data.groupLayers, groupLayers);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            var _loadGroupLayer = function(groupLayerId) {
                return dataSvc.loadGroupLayer(groupLayerId)
                    .then(function(groupLayer) {
                        console.log('This is the group layer');
                        console.log(groupLayer);
                        gsc.util.clearExtendObject($scope.data.currentGroupLayer, groupLayer);
                    }, function(errMsg) {
                        console.log('error loading group layer');
                        $rootScope.console.log(errMsg);
                    });
            };

            var _loadLayers = function() {
                return dataSvc.loadLayers()
                    .then(function(layers) {
                        gsc.util.clearExtendArray($scope.data.layers, layers);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            $scope.resetCurrent = function() {
                gsc.util.clearExtendObject($scope.data.currentGroupLayer, {});
            };

            $scope.delete = function(groupLayerId) {
                if ($window.confirm('Are you sure you would like to delete the group layer?')) {
                    gsc.grouplayer.delete(groupLayerId)
                        .then(function(res) {
                            if (res.status !== 'error') {
                                $rootScope.console.log('Deleted group layer');
                                _activateTab(0);
                                _loadGroupLayers();
                            } else {
                                $rootScope.console.log('An error occurred deleting group layer');
                                $rootScope.console.log(res);
                            }
                        }, function(err) {
                            $rootScope.console.log('An error occurred deleting group layer');
                            $rootScope.console.log(err);
                        });
                }
            };

            $scope.edit = function(groupLayerData) {
                _loadGroupLayer(groupLayerData.id)
                    .then(function(groupLayer) {
                        _activateTab(1);
                    }, function(err) {

                    });
            };

        }
    ]);
