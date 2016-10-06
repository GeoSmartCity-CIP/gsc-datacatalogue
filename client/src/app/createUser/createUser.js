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
                user: {},
                users: [],
                organizations: []
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
                _loadUsers();
                _loadOrganizations();
            };

            var _loadUsers = function() {
                dataSvc.loadUsers()
                    .then(function(users) {
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

            $scope.edit = function(user) {
                console.log(user);
                _activateTab(1);
                gsc.util.clearExtendObject($scope.data.user, user);
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

            $scope.save = function() {
                if ($scope.isUpdate()) {
                    gsc.user.update(
                        $scope.data.user.id,
                        $scope.data.user.email,
                        $scope.data.user.username,
                        $scope.data.user.password,
                        $scope.data.user.confirmpassword,
                        [{
                                id: $scope.data.user.organizationId
                            }])
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
                        [{
                                'organization': $scope.data.user.organizationId
                            }])
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
