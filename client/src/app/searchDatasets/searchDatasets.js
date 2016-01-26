/* global admBoard, angular */

'use strict';
angular.module('gscDatacat.controllers')
        .
        controller('searchDatasetsCtrl', [
            '$scope',
            '$rootScope',
            '$stateParams',
            function(
                    $scope,
                    $rootScope,
                    $stateParams) {

                console.log($stateParams);

            }]);
