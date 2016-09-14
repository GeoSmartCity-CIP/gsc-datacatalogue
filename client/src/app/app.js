'use strict';

angular.module('gscDatacat.controllers')
        .
        controller('appCtrl', [
            '$scope',
            '$rootScope',
            '$state',
            'authSvc',
            function ($scope,
                    $rootScope,
                    $state,
                    authSvc) {

                $scope.authSvc = authSvc;

                $rootScope.log = [];

                $rootScope.console = {};

                $rootScope.console.lastError = '';

                $scope.errorMessage = $rootScope.console.lastError;

                $rootScope.console.clear = function () {
                    $rootScope.log.length = 0;
                };

                $rootScope.truncateLog = function () {
                    if ($rootScope.log.length > 10) {
                        $rootScope.console.clear();
                    }
                };

                $rootScope.console.log = function (message) {
                    console.log(message);
                    $rootScope.truncateLog();
                    $rootScope.log.push('Log: ' + message.toString());
                };

                $rootScope.console.debug = function (debugMessage) {
                    $rootScope.truncateLog();
                    $rootScope.log.push('Debug: ' + debugMessage.toString());
                };

                $rootScope.console.error = function (errorMessage) {
                    $rootScope.truncateLog();
                    $rootScope.log.push('Error: ' + errorMessage.toString());
                    $rootScope.console.lastError = errorMessage;
                    console.log(errorMessage);
                };

                $rootScope.data.dataSourceTypes = [{
                        name: 'ESRI Shapefile',
                        type: 'Shape'
                    }, {
                        name: 'PostgreSQL/PostGIS database',
                        type: 'PostGIS'
                    }, {
                        name: 'Web Map Service',
                        type: 'WMS'
                    }, {
                        name: 'Web Feature Service',
                        type: 'WFS'
                    }, {
                        name: 'KML file',
                        type: 'KML'
                    }, {
                        name: 'GeoJSON file',
                        type: 'GeoJSON'
                    }];

            }
        ]);
