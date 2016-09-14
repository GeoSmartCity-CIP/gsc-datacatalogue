/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
controller('createOrganizationCtrl', [
    '$window',
    '$scope',
    '$rootScope',
    function(
        $window,
        $scope,
        $rootScope) {

        $scope.organization = {};

        $scope.organizations = [];

        $scope.$on('$stateChangeSuccess', function() {
            _do();
        });

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

        var _do = function() {
            _loadOrganizations();
        };

        var _loadOrganizations = function() {
            gsc.organization.list().then(function(res) {
                if (res.organizations !== undefined && jQuery.isArray(res.organizations)) {
                    $scope.organizations.length = 0;
                    jQuery.extend($scope.organizations, res.organizations);
                    //$scope.$apply();
                } else {
                    $rootScope.console.log('organizations is not an array or is undefined');
                }
                $rootScope.console.log('reloaded organizations');
            });
        };

        $scope.isUpdate = function() {
            if (jQuery.isNumeric($scope.organization.id)) {
                return true;
            } else {
                return false;
            }
        };

        $scope.resetOrganization = function() {
            jQuery.each($scope.organization, function(key, val) {
                $scope.organization[key] = undefined;
            });
        };

        $scope.edit = function(organizationName) {
            _activateTab(1);
            gsc.organization.list(organizationName)
                .then(function(res) {
                    if (jQuery.isArray(res.organizations) && res.organizations.length > 0) {
                        jQuery.extend($scope.organization, res.organizations[0]);
                        //$scope.$apply();
                        $rootScope.console.log('Loaded organization for editing');
                    }
                    $rootScope.console.log(res);
                });
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
