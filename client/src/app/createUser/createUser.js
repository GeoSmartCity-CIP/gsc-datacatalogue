/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('createUserCtrl', [
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

            $scope.data = {
                user: {
                    organizations: []
                },
                users: [],
                organizations: []
            };
            
            $scope.authSvc = authSvc;

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
                _loadUsers();
                _loadOrganizations();
            };

            var _loadUsers = function() {
                dataSvc.loadUsers()
                    .then(function(users) {
                        console.log(users);
                        gsc.util.clearExtendArray($scope.data.users, users);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            var _loadOrganizations = function() {
                dataSvc.loadOrganizations()
                    .then(function(organizations) {
                        gsc.util.clearExtendArray($scope.data.organizations, organizations);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            $scope.isUpdate = function() {
                if (jQuery.isNumeric($scope.data.user.id)) {
                    return true;
                } else {
                    return false;
                }
            };

            $scope.resetUser = function() {
                gsc.util.clearExtendObject($scope.data.user, {});
            };

            $scope.edit = function(userData) {
                gsc.util.clearExtendObject($scope.data.user, userData);
                _activateTab(1);
            };

            $scope.delete = function(userId) {
                if ($window.confirm('Are you sure you want to delete the organization?')) {
                    gsc.user.delete(userId).then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.log('Deleted user');
                            _loadUsers();
                        } else {
                            console.log(res.description);
                        }
                    });
                }
            };

            $scope.addOrganization = function(organizationData) {
                if (gsc.util.isNull($scope.data.user.organizations)) {
                    $scope.data.user.organizations = [];
                }

                var exists = false;

                for (var i = 0; i < $scope.data.user.organizations.length; i++) {
                    if ($scope.data.user.organizations[i].id === organizationData.id) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    $scope.data.user.organizations.push({
                        id: organizationData.id,
                        organization: organizationData.id,
                        organizationname: organizationData.organizationname
                    });
                } else {
                    $rootScope.console.usrInfo(
                        'User is already part of ' + organizationData.organizationname);
                }
            };

            $scope.removeOrganization = function(organizationData) {
                console.log(organizationData.id);
                console.log($scope.data.user.organizations);
                if (gsc.util.isNull($scope.data.user.organizations)) {
                    $scope.data.user.organizations = [];
                }
                var indexToRemove = -1;
                for (var i = 0; i < $scope.data.user.organizations.length; i++) {
                    if (+$scope.data.user.organizations[i].id === +organizationData.id) {
                        indexToRemove = i;
                        break;
                    }
                }

                console.log(indexToRemove);
                if (indexToRemove >= 0) {
                    $scope.data.user.organizations.splice(indexToRemove, 1);
                }
            };

            $scope.save = function() {
                if ($scope.isUpdate()) {
                    gsc.user.update(
                        $scope.data.user.id,
                        $scope.data.user.email,
                        $scope.data.user.username,
                        gsc.util.cleanJsonObjects($scope.data.user.organizations))
                        .then(function(res) {
                            if (res.status !== 'error') {
                                _activateTab(0);
                                _loadUsers();
                                $rootScope.console.log('Updated existing user');
                                $rootScope.console.log(res);
                            } else {
                                $rootScope.console.log(
                                    res.description);
                            }
                        }, function(errMsg) {
                            $rootScope.console.log(errMsg);
                        });
                } else {
                    gsc.user.register($scope.data.user.email,
                        $scope.data.user.username,
                        $scope.data.user.password,
                        $scope.data.user.confirmpassword,
                        gsc.util.cleanJsonObjects($scope.data.user.organizations))
                        .then(function(res) {
                            if (res.status !== 'error') {
                                _activateTab(0);
                                _loadUsers();
                                $rootScope.console.log('Created new users');
                                $rootScope.console.log(res);
                            } else {
                                $rootScope.console.log(res.description);
                            }
                        }, function(errMsg) {
                            $rootScope.console.log(errMsg);
                        });
                }
            };

        }
    ]);
