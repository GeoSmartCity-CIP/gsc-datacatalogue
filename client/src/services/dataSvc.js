/* global angular, gscDatacat */

'use strict';
angular.module('gscDatacat.services')
        .factory('dataSvc', [
            '$rootScope',
            '$state',
            'authSvc',
            '$http',
            function ($rootScope,
                    $state,
                    authSvc,
                    $http) {

                this.loadDataSources = function () {
                    return gsc.datasource.list(null, authSvc.authUsr.organizationId, null, true)
                            .then(function (res) {
                                $rootScope.console.log('Loading data sources');
                                $rootScope.console.log(res);
                                if (gsc.util.isArrayWithContent(res.datasources)) {
                                    return res.datasources;
                                } else {
                                    $rootScope.console.error('No data sources');
                                    return null;
                                }
                            });
                };

                this.loadOrganizations = function () {
                    return gsc.organization.list().then(function (res) {
                        $rootScope.console.log('Loading organizations');
                        $rootScope.console.log(res);
                        if (gsc.util.isArrayWithContent(res.organizations)) {
                            return res.organizations;
                        }
                    });
                };

                return this;

            }]);
