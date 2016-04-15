/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
        .
        controller('createFunctionCtrl', [
            '$scope',
            '$rootScope',
            function(
                    $scope,
                    $rootScope) {

                $scope.dataSource = {};

            }]);
