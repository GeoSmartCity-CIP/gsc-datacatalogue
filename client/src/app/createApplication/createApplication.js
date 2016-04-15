/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
        .
        controller('createApplicationCtrl', [
            '$scope',
            '$rootScope',
            function(
                    $scope,
                    $rootScope) {

                $scope.dataSource = {};

            }]);
