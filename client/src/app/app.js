'use strict';
angular.module('gscDatacat.controllers')
        .
        controller('appCtrl', [
            '$scope',
            '$rootScope',
            function($scope,
                    $rootScope) {

                $rootScope.authUser = null;

            }]);
