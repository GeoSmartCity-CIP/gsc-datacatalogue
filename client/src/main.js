/* global angular */

'use strict';

jQuery.fn.datepicker.defaults.format = 'yyyy-mm-dd';
jQuery.fn.datepicker.defaults.autoclose = true;

/**
 * Declare main application module
 */
angular.module('gscDatacat', [
    'ui.router',
    'ui.bootstrap',
    'gscDatacat.controllers',
    'gscDatacat.directives',
    'gscDatacat.services'])
        .config([
            '$stateProvider',
            '$urlRouterProvider',
            function($stateProvider, $urlRouterProvider) {

                // Setup states
                $stateProvider
                        .state('app', {
                            url: '/app',
                            abstract: true,
                            templateUrl: 'templates/app.html',
                            controller: 'appCtrl'
                        })
                        .state('app.uploadDataset', {
                            url: '/uploadDataset',
                            views: {
                                'content': {
                                    templateUrl: 'templates/uploadDataset/uploadDataset.html',
                                    controller: 'uploadDatasetCtrl'
                                }
                            }
                        })
                        .state('app.searchDatasets', {
                            url: '/searchDatasets/:query',
                            views: {
                                'content': {
                                    templateUrl: 'templates/searchDatasets/searchDatasets.html',
                                    controller: 'searchDatasetsCtrl'
                                }
                            }
                        })
                        .state('app.loginForm', {
                            url: '/loginForm',
                            views: {
                                'content': {
                                    templateUrl: 'templates/loginForm/loginForm.html',
                                    controller: 'loginFormCtrl'
                                }
                            }
                        });
                //                        .state('app.testState', {
                //                            url: '/testState',
                //                            views: {
                //                                'content': {
                //                                    templateUrl: 'templates/testState/testState.html',
                //                                    controller: 'testStateCtrl'
                //                                }
                //                            }
                //                        });

                // if none of the above states are matched, use this as the fallback
                $urlRouterProvider.otherwise('/app/loginForm');
            }]);

/**
 * Declare gscDatacat controllers module
 */
angular.module('gscDatacat.controllers', []);

/**
 * Declare gscDatacat services module
 */
angular.module('gscDatacat.services', []);

/**
 * Declare gscDatacat services module
 */
angular.module('gscDatacat.directives', []);
