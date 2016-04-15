/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
        .
        controller('createPermissionCtrl', [
            '$scope',
            '$rootScope',
            function(
                    $scope,
                    $rootScope) {

                $scope.dataSource = {};

            }]);
