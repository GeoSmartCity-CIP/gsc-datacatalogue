/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('createOrganizationCtrl', [
        '$window',
        '$scope',
        '$rootScope',
        'dataSvc',
        function(
            $window,
            $scope,
            $rootScope,
            dataSvc) {

            $scope.organization = {};

            $scope.organizations = [];

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

            $scope.init = function() {
                _loadOrganizations();
            };

            var _loadOrganizations = function() {
                dataSvc.loadOrganizations()
                    .then(function(organizations) {
                        $rootScope.console.log('(Re)loaded organizations');
                        gsc.util.clearExtendArray($scope.organizations, organizations);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            $scope.isUpdate = function() {
                if (jQuery.isNumeric($scope.organization.id)) {
                    return true;
                } else {
                    return false;
                }
            };

            $scope.resetCurrent = function() {
                gsc.util.clearExtendObject($scope.organization, {});
            };

            $scope.edit = function(organizationData) {
                _activateTab(1);
                gsc.util.clearExtendObject($scope.organization, organizationData);
            };

            $scope.delete = function(organizationId) {
                if ($window.confirm('Are you sure you want to delete the organization?')) {
                    gsc.organization.delete(organizationId).then(function(res) {
                        _loadOrganizations();
                        $rootScope.console.log('Deleted organization');
                        $rootScope.console.log(res);
                    });
                }
            };

            $scope.submit = function() {

                _activateTab(0);

                if ($scope.isUpdate()) {
                    gsc.organization.update(
                        $scope.organization.id,
                        $scope.organization.organizationname,
                        $scope.organization.description)
                        .then(function(res) {
                            _loadOrganizations();
                            $rootScope.console.log('Updated organization');
                            $rootScope.console.log(res);
                        });

                } else {
                    gsc.organization.create($scope.organization.organizationname,
                        $scope.organization.description)
                        .then(function(res) {
                            _loadOrganizations();
                            $rootScope.console.log('Added organization');
                            $rootScope.console.log(res);
                        });
                }
            };

        }
    ]);
