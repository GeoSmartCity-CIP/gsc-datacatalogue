/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
        .
        controller('createDataSourceCtrl', [
            '$scope',
            '$rootScope',
            function(
                    $scope,
                    $rootScope) {

                $scope.dataSource = {};

            }]);
