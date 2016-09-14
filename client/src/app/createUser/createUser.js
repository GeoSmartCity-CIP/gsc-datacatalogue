/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
controller('createUserCtrl', [
    '$window',
    '$scope',
    '$rootScope',
    'authSvc',
    function(
        $window,
        $scope,
        $rootScope,
        authSvc) {

        $scope.user = {};

        $scope.users = [];

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

        var _do = function() {
            _loadOrganizations();
        };

        var _loadOrganizations = function() {
            gsc.organization.list()
                .then(function(res) {
                    if (jQuery.isArray(res.organizations) && res.organizations.length > 0) {
                        $scope.organizations.length = 0;
                        jQuery.extend($scope.organizations, res.organizations);

                        for (var i = 0; i < res.organizations.length; i++) {
                            if (res.organizations[i].id === authSvc.authUsr.organizationId) {
                                $rootScope.console.log(res.organizations[i]);
                            }
                        }

                        //$scope.$apply();
                        $rootScope.console.log('Reloaded organizations');
                    }
                    $rootScope.console.log(res);
                });
        };

        $scope.isUpdate = function() {
            if (jQuery.isNumeric($scope.user.id)) {
                return true;
            } else {
                return false;
            }
        };

        $scope.resetUser = function() {
            jQuery.each($scope.user, function(key, val) {
                $scope.user[key] = undefined;
            });
        };

        $scope.edit = function(userId) {
            _activateTab(1);
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
                gsc.user.update(
                        $scope.organization.id,
                        $scope.organization.organizationname,
                        $scope.organization.description)
                    .then(function(res) {
                        _loadOrganizations();
                        $rootScope.console.log('Updated organization');
                        $rootScope.console.log(res);
                    });

            } else {
                gsc.user.register($scope.user.email,
                        $scope.user.username,
                        $scope.user.password,
                        $scope.user.confirmpassword, [{
                            'organization': $scope.user.organizationId
                        }])
                    .then(function(res) {
                        _loadOrganizations();
                        $rootScope.console.log('Added organization');
                        $rootScope.console.log(res);
                    });
            }
        };

    }
]);
