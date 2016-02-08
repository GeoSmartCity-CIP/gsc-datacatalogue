/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
        .
        controller('uploadDatasetCtrl', [
            '$scope',
            '$rootScope',
            function(
                    $scope,
                    $rootScope) {

                $scope.uploadData = {};

            }]);
