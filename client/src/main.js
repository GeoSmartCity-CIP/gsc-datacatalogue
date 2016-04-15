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
                        .state('app.createApplication', {
                            url: '/createApplication',
                            views: {
                                'content': {
                                    templateUrl: 'templates/createApplication/createApplication.html',
                                    controller: 'createApplicationCtrl'
                                }
                            }
                        })
                        .state('app.createDataSet', {
                            url: '/createDataSet',
                            views: {
                                'content': {
                                    templateUrl: 'templates/createDataSet/createDataSet.html',
                                    controller: 'createDataSetCtrl'
                                }
                            }
                        })
                        .state('app.createDataSource', {
                            url: '/createDataSource',
                            views: {
                                'content': {
                                    templateUrl: 'templates/createDataSource/createDataSource.html',
                                    controller: 'createDataSourceCtrl'
                                }
                            }
                        })
                        .state('app.createFunction', {
                            url: '/createFunction',
                            views: {
                                'content': {
                                    templateUrl: 'templates/createFunction/createFunction.html',
                                    controller: 'createFunctionCtrl'
                                }
                            }
                        })
                        .state('app.createGroupLayer', {
                            url: '/createGroupLayer',
                            views: {
                                'content': {
                                    templateUrl: 'templates/createGroupLayer/createGroupLayer.html',
                                    controller: 'createGroupLayerCtrl'
                                }
                            }
                        })
                        .state('app.createLayer', {
                            url: '/createLayer',
                            views: {
                                'content': {
                                    templateUrl: 'templates/createLayer/createLayer.html',
                                    controller: 'createLayerCtrl'
                                }
                            }
                        })
                        .state('app.createOrganization', {
                            url: '/createOrganization',
                            views: {
                                'content': {
                                    templateUrl: 'templates/createOrganization/createOrganization.html',
                                    controller: 'createOrganizationCtrl'
                                }
                            }
                        })
                        .state('app.createPermission', {
                            url: '/createPermission',
                            views: {
                                'content': {
                                    templateUrl: 'templates/createPermission/createPermission.html',
                                    controller: 'createPermissionCtrl'
                                }
                            }
                        })
                        .state('app.createRole', {
                            url: '/createRole',
                            views: {
                                'content': {
                                    templateUrl: 'templates/createRole/createRole.html',
                                    controller: 'createRoleCtrl'
                                }
                            }
                        })
                        .state('app.createUser', {
                            url: '/createUser',
                            views: {
                                'content': {
                                    templateUrl: 'templates/createUser/createUser.html',
                                    controller: 'createUserCtrl'
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
