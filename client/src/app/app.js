'use strict';
angular.module('gscDatacat.controllers')
        .
        controller('appCtrl', [
            '$scope',
            '$rootScope',
            function($scope,
                    $rootScope) {

                $rootScope.data = {};

                $rootScope.data.authUser = null;

                $rootScope.data.loginData = {};

                $rootScope.doLogin = function() {

                    if ($rootScope.data.loginData.userName === 'test@test.com' &&
                            $rootScope.data.loginData.password === 'test') {
                        $rootScope.data.authUser = $rootScope.data.loginData.userName;
                    } else {
                        console.log('Authentication failed');
                    }

                };

                $rootScope.doLogout = function() {

                    $rootScope.data.authUser = null;

                };

                $rootScope.data.dataSourceTypes = [
                    {
                        name: 'Web Map Service',
                        type: 'WMS'
                    },
                    {
                        name: 'Web Feature Service',
                        type: 'WFS'
                    },
                    {
                        name: 'ESRI Shapefile',
                        type: 'Shape'
                    },
                    {
                        name: 'KML file',
                        type: 'KML'
                    },
                    {
                        name: 'GeoJSON file',
                        type: 'GeoJSON'
                    },
                    {
                        name: 'Local database',
                        type: 'DB'
                    }
                ];

            }]);
