'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('appCtrl', [
        '$scope',
        '$rootScope',
        '$state',
        'authSvc',
        '$timeout',
        function($scope,
            $rootScope,
            $state,
            authSvc,
            $timeout) {

            $scope.$on('$stateChangeSuccess', function() {
                if (authSvc.isAuth().success !== true &&
                    $state.current.name !== 'app.loginForm') {
                    $state.go('app.loginForm');
                }
            });
            $scope.authSvc = authSvc;


            $scope.redirect = function() {
                $state.go('app.createDataSource', {}, {
                    reload: true
                });
            };

            $rootScope.data.messages = {
                warning: '',
                info: ''
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

            $rootScope.data.functionTypes = [
                'application',
                'data catalogue',
                'layers'
            ];

            $rootScope.log = [];

            $rootScope.console = {
                clear: function() {
                    $rootScope.log.length = 0;
                },
                truncateLog: function() {
                    if ($rootScope.log.length > 50) {
                        $rootScope.console.clear();
                    }
                },
                writeLine: function(msg, typ) {
                    var logFunc;
                    if (typ === 'error') {
                        logFunc = console.error;
                    } else if (typ === 'debug') {
                        logFunc = console.debug;
                    } else if (typ === 'info') {
                        logFunc = console.info;
                    } else {
                        logFunc = console.log;
                    }

                    if (msg !== undefined) {
                        logFunc(msg);
                        $rootScope.console.truncateLog();
                        $rootScope.log.push(msg.toString());
                    } else {
                        logFunc('Log function invoked with undefined object');
                    }
                },
                log: function(msg) {
                    if (msg === undefined) {
                        msg = '<no message specified>';
                    }
                    $rootScope.console.writeLine('Log: ' + msg.toString());
                },
                info: function(msg) {
                    if (msg === undefined) {
                        msg = '<no message specified>';
                    }
                    $rootScope.console.writeLine('Info: ' + msg.toString(), 'info');
                },
                todo: function(msg) {
                    if (msg === undefined) {
                        msg = '<no message specified>';
                    }
                    $rootScope.console.writeLine('To do:  *** ' + msg.toString() + ' ***', 'info');
                },
                debug: function(msg) {
                    if (msg === undefined) {
                        msg = '<no message specified>';
                    }
                    if (typeof msg !== 'string' &&
                        typeof msg !== 'number' &&
                        typeof msg !== 'boolean') {
                        msg = JSON.stringify(msg);
                    }

                    $rootScope.console.writeLine('Debug: !!! ' + msg + ' !!!', 'debug');
                },
                error: function(msg) {
                    if (msg === undefined) {
                        msg = '<no message specified>';
                    }
                    $rootScope.console.writeLine('Error: !!!' + msg.toString() + ' !!!', 'error');
                },
                usrWarn: function(msg) {
                    if (msg === undefined) {
                        msg = '<no message specified>';
                    }
                    $rootScope.data.messages.warning = msg.toString();
                    if ($scope.$root.$$phase !== '$apply' && $scope.$root.$$phase !== '$digest') {
                        $scope.$apply();
                    }
                    $timeout(function() {
                        $rootScope.data.messages.warning = '';
                    }, 5000);
                },
                usrInfo: function(msg) {
                    if (msg === undefined) {
                        msg = '<no message specified>';
                    }
                    $rootScope.data.messages.info = msg.toString();
                    if ($scope.$root.$$phase !== '$apply' && $scope.$root.$$phase !== '$digest') {
                        $scope.$apply();
                    }
                    $timeout(function() {
                        $rootScope.data.messages.info = '';
                    }, 5000);
                }
            };
        }
    ]);
