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

            self.loadRoles = function() {
                var dfd = $q.defer();
                gsc.role.listrole().then(function(res) {
                    $rootScope.console.log('Loading roles');
                    $rootScope.console.log(res);
                    if (gsc.util.isArrayWithContent(res.roles)) {
                        dfd.resolve(res.roles);
                    }
                }, function(err) {
                    dfd.reject(err.statusText);
                });
                return dfd.promise;
            };

            return self;

        }]);
