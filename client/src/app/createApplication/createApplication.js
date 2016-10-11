/* global admBoard, angular, gsc */

'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('createApplicationCtrl', [
        '$window',
        '$scope',
        '$rootScope',
        'dataSvc',
        function(
            $window,
            $scope,
            $rootScope,
            dataSvc) {

            $scope.data = {
                applications: [],
                organizations: [],
                layers: [],
                groupLayers: [],
                currentApplication: {}
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
                _loadApplications();
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

            var _loadApplications = function() {
                dataSvc.loadApplications()
                    .then(function(applications) {
                        gsc.util.clearExtendArray($scope.data.applications, applications);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            var _loadApplication = function(applicationId) {
                return dataSvc.loadApplication(applicationId)
                    .then(function(application) {
                        return application;
                    }, function(errMsg) {
                        $rootScope.console.error(errMsg);
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

            var _loadGroupLayers = function() {
                dataSvc.loadGroupLayers()
                    .then(function(groupLayers) {
                        gsc.util.clearExtendArray($scope.data.groupLayers, groupLayers);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            $scope.addLayer = function(layer) {
                if ($scope.data.currentApplication.layers === undefined) {
                    $scope.data.currentApplication.layers = [];
                }
                $scope.data.currentApplication.layers.push({
                    id: layer.id,
                    idlayer: layer.id,
                    layername: layer.layername
                });
            };

            $scope.removeLayer = function(index) {
                if ($scope.data.currentApplication.layers === undefined) {
                    $scope.data.currentApplication.layers = [];
                }
                if (index >= 0) {
                    $scope.data.currentApplication.layers.splice(index, 1);
                }
            };

            $scope.addGroupLayer = function(groupLayer) {
                if ($scope.data.currentApplication.groups === undefined) {
                    $scope.data.currentApplication.groups = [];
                }
                $scope.data.currentApplication.groups.push({
                    id: groupLayer.id,
                    idgroup: groupLayer.id,
                    groupname: groupLayer.groupname
                });
                $rootScope.console.log($scope.data.currentApplications);
            };

            $scope.removeGroupLayer = function(index) {
                if ($scope.data.currentApplication.groups === undefined) {
                    $scope.data.currentApplication.groups = [];
                }
                if (index >= 0) {
                    $scope.data.currentApplication.groups.splice(index, 1);
                }
            };

            $scope.edit = function(application) {
                _loadApplication(application.idapplication)
                    .then(function(res) {
                        $rootScope.console.todo(
                            'There is no way to retrieve all details of an application in order to edit it');
                        $rootScope.console.log(application);
                        gsc.util.clearExtendObject($scope.data.currentApplication, application);
                        _activateTab(1);
                    }, function(err) {
                        $rootScope.console.debug(err);
                    });
            };

            $scope.delete = function(applicationId) {
                if ($window.confirm('Are you sure you would like to delete the selected layer?')) {
                    return gsc.application.delete(applicationId)
                        .then(function(res) {
                            if (res.status !== 'error') {
                                $rootScope.console.log('Deleted application');
                                _loadApplications();

                            } else {
                                $rootScope.console.log('An error occurred deleting application');
                                $rootScope.console.log(res.description);
                                $rootScope.console.log(res);
                            }
                        }, function(err) {
                            $rootScope.console.log('An error occurred deleting application');
                            $rootScope.console.log(err);
                        });
                }
            };

            $scope.isUpdate = function() {
                if (jQuery.isNumeric($scope.data.currentApplication.idapplication)) {
                    return true;
                } else {
                    return false;
                }
            };

            var _create = function() {
                gsc.application.create($scope.data.currentApplication.applicationname,
                    $scope.data.currentApplication.organization,
                    $scope.data.currentApplication.description,
                    $scope.data.currentApplication.geoserver,
                    $scope.data.currentApplication.srs,
                    $scope.data.currentApplication.maxExtent,
                    $scope.data.currentApplication.uri
                    )
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.log('Created application');
                            _loadApplications();
                            _activateTab(0);
                        } else {
                            $rootScope.console.log('An error occurred while creating application');
                            $rootScope.console.log(res.description);
                            $rootScope.console.log(res);
                        }
                    }, function(err) {
                        $rootScope.console.log('An error occurred while creating application');
                        $rootScope.console.log(err);
                    });
            };

            var _update = function() {
                gsc.application.create($scope.data.currentApplication.applicationname,
                    $scope.data.currentApplication.organization,
                    $scope.data.currentApplication.description,
                    $scope.data.currentApplication.geoserver,
                    $scope.data.currentApplication.srs,
                    $scope.data.currentApplication.maxExtent,
                    $scope.data.currentApplication.uri
                    )
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.log('Updated application');
                            _loadApplications();
                            _activateTab(0);
                        } else {
                            $rootScope.console.log('An error occurred updating application');
                            $rootScope.console.log(res.description);
                            $rootScope.console.log(res);
                        }
                    }, function(err) {
                        $rootScope.console.log('An error occurred updating application');
                        $rootScope.console.log(err);
                    });
            };

            $scope.publishToGeoServer = function(application) {

                gsc.application.publishToGeoServer(application.idapplication)
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.log('Published to GeoServer');
                        } else {
                            $rootScope.console.log('An erroro occurred publishing to GeoServer');
                            $rootScope.console.log(res.description);
                            $rootScope.console.log(res);
                        }
                    }, function(err) {
                        $rootScope.console.log('An erroro occurred publishing to GeoServer');
                        $rootScope.console.log(err);
                    });

            };

            $scope.resetCurrent = function() {
                gsc.util.clearExtendObject($scope.data.currentApplication, {});
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
