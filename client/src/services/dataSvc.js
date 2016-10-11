/* global angular, gscDatacat */

'use strict';
angular.module('gscDatacat.services')
    .factory('dataSvc', [
        '$rootScope',
        '$state',
        'authSvc',
        '$http',
        '$q',
        function($rootScope,
            $state,
            authSvc,
            $http,
            $q) {

            var self = this;

            self.loadLayers = function() {
                var dfd = $q.defer();
                gsc.layer.list()
                    .then(function(res) {
                        if (res.status !== 'error' &&
                            gsc.util.isArrayWithContent(res.layers)) {
                            dfd.resolve(res.layers);
                            $rootScope.console.log('Loaded layers');
                        } else if (res.status === 'error') {
                            dfd.reject('An error occurred while loading layers');
                            dfd.reject(res.description);
                            $rootScope.console.log(res);
                        } else {
                            dfd.reject('An error occurred while loading layers');
                            $rootScope.console.log(res);
                        }
                    }, function(err) {
                        dfd.resolve(err.statusText);
                    });
                return dfd.promise;
            };

            self.loadLayer = function(layerId) {
                var dfd = $q.defer();
                gsc.layer.list(null, layerId)
                    .then(function(res) {
                        if (res.status !== 'error' &&
                            gsc.util.isArrayWithContent(res.layers)) {
                            $rootScope.console.log(res.layers[0]);
                            dfd.resolve(res.layers[0]);
                        } else if (res.status === 'error') {
                            dfd.reject(res.description);
                        } else {
                            dfd.reject(
                                'No layer data returned for specified id');
                        }
                    }, function(err) {
                        dfd.resolve(err.statusText);
                    });
                return dfd.promise;
            };

            self.loadDataSources = function() {
                var dfd = $q.defer();
                gsc.datasource.list(null, authSvc.authUsr.organizationId, null, true)
                    .then(function(res) {
                        $rootScope.console.log('Loading data sources');
                        if (gsc.util.isArrayWithContent(res.datasources)) {
                            dfd.resolve(res.datasources);
                        } else {
                            $rootScope.console.error('No data sources');
                            dfd.reject('No data sources');
                        }
                    }, function(err) {
                        dfd.reject(err.statusText);
                    });
                return dfd.promise;
            };

            self.loadDataOrigin = function(dataSourceId) {
                var dfd = $q.defer();
                gsc.datasource.listDataOrigin(dataSourceId)
                    .then(function(res) {
                        $rootScope.console.log('Loading data origin');
                        if (res.status !== 'error' &&
                            res.dataorigin !== undefined &&
                            jQuery.isArray(res.dataorigin)) {
                            dfd.resolve(res.dataorigin);
                        } else if (res.status === 'error') {
                            dfd.reject(res.description);
                        } else {
                            dfd.reject('Error during loading of data origin');
                        }
                    }, function(err) {
                        dfd.reject(err.statusText);
                    });

                return dfd.promise;
            };

            self.loadDataSource = function(dataSourceId) {
                var dfd = $q.defer();
                gsc.datasource.list(dataSourceId, null, null, true)
                    .then(function(res) {
                        if (res.datasources.length > 0) {
                            dfd.resolve(res.datasources[0]);
                        } else {
                            dfd.reject('No data sources');
                        }
                    }, function(err) {
                        dfd.reject(err.statusText);
                    });
                return dfd.promise;
            };

            self.loadDataSet = function(dataSetId) {
                var dfd = $q.defer();
                gsc.dataset.list(undefined, +dataSetId, undefined, undefined)
                    .then(function(res) {
                        if (res.status !== 'error' && res.dataset.length > 0) {
                            dfd.resolve(res.dataset[0]);
                        } else if (res.status === 'error') {
                            dfd.reject(res.description);
                        } else {
                            dfd.reject('No data sources');
                        }
                    }, function(err) {
                        dfd.reject(err.statusText);
                    });
                return dfd.promise;
            };

            self.listDataSetColumns = function(dataSetId) {
                var dfd = $q.defer();
                gsc.dataset.listCols(+dataSetId).
                    then(function(res) {
                        console.log(res);
                        if (res.status !== 'error') {
                            dfd.resolve(res.columns);
                        } else {
                            dfd.reject(res.description);
                        }
                    }, function(err) {
                        dfd.reject(err.statusText);
                    });
                return dfd.promise;
            };

            self.loadDataSets = function(dataSourceId) {
                var dfd = $q.defer();
                gsc.dataset.list(dataSourceId,
                    undefined,
                    undefined,
                    authSvc.authUsr.organizationId)
                    .then(function(res) {
                        if (res.dataset.length > 0) {
                            dfd.resolve(res.dataset);
                        } else {
                            dfd.reject('No data sources');
                        }
                    }, function(err) {
                        dfd.reject(err.statusText);
                    });
                return dfd.promise;
            };

            self.loadOrganizations = function() {
                var dfd = $q.defer();

                gsc.organization.list().then(function(res) {
                    $rootScope.console.log('Loading organizations');
                    if (res.status !== 'error' && gsc.util.isArrayWithContent(
                        res.organizations)) {
                        dfd.resolve(res.organizations);
                    } else if (res.status === 'error') {
                        dfd.reject(res.description);
                    } else {
                        dfd.reject('No organizations available');
                    }
                }, function(err) {
                    dfd.reject(err.statusText);
                });

                return dfd.promise;
            };

            self.loadUsers = function() {
                var dfd = $q.defer();

                gsc.user.list(+authSvc.authUsr.organizationId)
                    .then(function(users) {
                        $rootScope.console.log('Loaded users for organization');
                        if (gsc.util.isArrayWithContent(
                            users)) {
                            dfd.resolve(users);
                        } else if (users.status === 'error') {
                            dfd.reject(users.description);
                        } else {
                            dfd.reject('No users returned');
                            $rootScope.console.log(users);
                        }
                    }, function(err) {
                        dfd.reject(err.statusText);
                    });

                return dfd.promise;
            };

            self.loadUser = function(userId) {
                var dfd = $q.defer();

                gsc.user.list(+authSvc.authUsr.organizationId)
                    .then(function(users) {
                        $rootScope.console.info('Loaded users');
                        if (gsc.util.isArrayWithContent(
                            users)) {
                            dfd.resolve(users);
                        } else if (users.status === 'error') {
                            $rootScope.console.error('An error occurred loading users');
                            dfd.reject(users.description);
                            $rootScope.console.debug(users);
                        } else {
                            $rootScope.console.info('No users returned');
                            dfd.reject('No users returned');
                        }
                    }, function(err) {
                        $rootScope.console.error('An error occurred loading users');
                        $rootScope.console.debug(err);
                        dfd.reject(err.statusText);
                    });

                return dfd.promise;
            };

            self.loadRoles = function() {
                var dfd = $q.defer();
                gsc.role.listrole(authSvc.authUsr.organizationId, null, true)
                    .then(function(res) {
                        if (res.status !== 'error' && gsc.util.isArrayWithContent(res.roles)) {
                            $rootScope.console.info('Loaded roles');
                            dfd.resolve(res.roles);
                        } else {
                            $rootScope.console.error('An error occurred loading roles');
                            dfd.reject(res.description);
                        }
                    }, function(err) {
                        $rootScope.console.error('An error occurred loading roles');
                        $rootScope.console.debug(err);
                        dfd.reject(err.statusText);
                    });
                return dfd.promise;
            };

            self.loadPermissions = function(roleId) {
                var dfd = $q.defer();
                gsc.permission.list(roleId)
                    .then(function(res) {
                        if (res.description === undefined &&
                            gsc.util.isArrayWithContent(res)) {
                            $rootScope.console.info('Loaded permissions');
                            dfd.resolve(res);
                        } else if (res.description !== undefined &&
                            res.description === 'No results found.') {
                            $rootScope.console.info('No permissions found for role');
                            dfd.resolve([]);
                        } else {
                            $rootScope.console.error('An error occurred loading permissions');
                            $rootScope.console.debug(res);
                            dfd.reject(res.description);
                        }
                    }, function(err) {
                        $rootScope.console.error('An error occurred loading permissions');
                        $rootScope.console.debug(err);
                        dfd.reject(err.statusText);
                    });
                return dfd.promise;
            };

            self.loadGroupLayers = function() {
                var dfd = $q.defer();
                gsc.grouplayer.list(authSvc.authUsr.organizationId)
                    .then(function(res) {
                        if (res.status !== 'error' &&
                            gsc.util.isArrayWithContent(res.grouplayers)) {
                            $rootScope.console.log('Loaded group layers');
                            dfd.resolve(res.grouplayers);
                        } else {
                            $rootScope.console.log('An error occurred loading group layers');
                            $rootScope.console.log(res);
                            dfd.reject(res.description);
                        }
                    }, function(err) {
                        $rootScope.console.log('An error occurred loading group layers');
                        dfd.reject(err.statusText);
                    });

                return dfd.promise;
            };

            self.loadFunctions = function() {
                var dfd = $q.defer();
                gsc.function.list(authSvc.authUsr.organizationId)
                    .then(function(res) {
                        if (res.status !== 'error' &&
                            gsc.util.isArrayWithContent(res.functions)) {
                            $rootScope.console.log('Loaded functions');
                            dfd.resolve(res.functions);
                        } else {
                            $rootScope.console.log('An error occurred loading functions');
                            dfd.reject(res.description);
                            $rootScope.console.log(res);
                        }
                    }, function(err) {
                        $rootScope.console.log('An error occurred loading functions');
                        dfd.reject(err.statusText);
                    });
                return dfd.promise;
            };

            self.loadApplications = function() {
                var dfd = $q.defer();

                gsc.application.list(authSvc.authUsr.organizationId, undefined, undefined)
                    .then(function(res) {
                        if (res.status !== 'error' && gsc.util.isArrayWithContent(
                            res.applications)) {
                            $rootScope.console.log('Loaded applications');
                            dfd.resolve(res.applications);
                        } else {
                            $rootScope.console.log(
                                'An error occurred loading applications');
                            $rootScope.console.log(res.description);
                            $rootScope.console.log(res);
                            dfd.reject(res.description);
                        }
                    }, function(err) {
                        $rootScope.console.log(
                            'An error occurred loading applications');
                        $rootScope.console.log(err);
                        dfd.reject(err.statusText);
                    });

                return dfd.promise;
            };

            self.loadApplication = function(applicationId) {
                var dfd = $q.defer();

                gsc.application.list(null, null, applicationId)
                    .then(function(res) {
                        if (res.status !== 'error' && gsc.util.isArrayWithContent(
                            res.applications)) {
                            $rootScope.console.log('Loaded application');
                            dfd.resolve(res.applications[0]);
                        } else {
                            $rootScope.console.log(
                                'An error occurred loading application');
                            $rootScope.console.log(res.description);
                            $rootScope.console.log(res);
                            dfd.reject(res.description);
                        }
                    }, function(err) {
                        $rootScope.console.log(
                            'An error occurred loading applications');
                        $rootScope.console.log(err);
                        dfd.reject(err.statusText);
                    });

                return dfd.promise;
            };

            return self;

        }]);
