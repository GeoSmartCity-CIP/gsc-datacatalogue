/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
    .
    controller('uploadGmlCtrl', [
        '$window',
        '$scope',
        '$rootScope',
        'dataSvc',
        function(
            $window,
            $scope,
            $rootScope,
            dataSvc) {

            $scope.data = {
                gml: {}
            };

            $scope.init = function() {
                $rootScope.console.log('Loading');
            };

            $scope.isUpdate = function() {
                return true;
            };

            $scope.save = function() {

            };

            $scope.resetCurrent = function() {

            };

        }
    ]);
