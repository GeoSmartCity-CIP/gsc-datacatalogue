/* global admBoard, angular */
'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('createRoleMembershipCtrl', [
        '$window',
        '$scope',
        '$rootScope',
        '$http',
        'authSvc',
        'dataSvc',
        '$q',
        function(
            $window,
            $scope,
            $rootScope,
            $http,
            authSvc,
            dataSvc,
            $q) {

            $scope.data = {
                roles: [],
                users: [],
                currentRole: {}
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
                _loadRoles();
                _loadUsers();
            };

            var _loadUsers = function() {
                dataSvc.loadUsers()
                    .then(function(users) {
                        gsc.util.clearExtendArray($scope.data.users, users);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            var _loadRoles = function() {
                dataSvc.loadRoles()
                    .then(function(roles) {
                        gsc.util.clearExtendArray($scope.data.roles, roles);
                    }, function(errMsg) {
                        $rootScope.console.log(errMsg);
                    });
            };

            $scope.addMembersToRole = function(roleData) {
                gsc.util.clearExtendObject($scope.data.currentRole, roleData);
                _activateTab(1);
            };

            $scope.addMember = function(userData) {

                $scope.data.currentRole.users = $scope.data.currentRole.users || [];

                var currentMembers = $scope.data.currentRole.users;
                var exists = false;
                for (var i = 0; i < currentMembers.length; i++) {
                    if (currentMembers[i].iduser == userData.iduser) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    currentMembers.push({
                        iduser: +userData.id,
                        username: userData.username
                    });
                }
            };

            $scope.removeMember = function(userData) {

                $scope.data.currentRole.users = $scope.data.currentRole.users || [];

                var currentMembers = $scope.data.currentRole.users;

                var indexToRemove = -1;

                for (var i = 0; i < currentMembers.length; i++) {
                    if (currentMembers[i].iduser === userData.iduser) {
                        indexToRemove = i;
                        break;
                    }
                }

                if (indexToRemove > -1) {
                    currentMembers.splice(indexToRemove, 1);
                }

            };

            $scope.save = function() {
                gsc.role.assignrole(
                    $scope.data.currentRole.id,
                    gsc.util.cleanJsonObjects($scope.data.currentRole.users))
                    .then(function(res) {
                        if (res.status !== 'error') {
                            $rootScope.console.usrInfo('Successfully set members');
                            _loadRoles();
                        } else {
                            $rootScope.console.error('An error occurred setting members');
                            $rootScope.console.debug(res);
                            $rootScope.console.usrWarn(res.description);
                        }
                    }, function(err) {
                        $rootScope.console.error('An error occurred setting members');
                        $rootScope.console.debug(err);
                    });
            };
        }
    ]);
