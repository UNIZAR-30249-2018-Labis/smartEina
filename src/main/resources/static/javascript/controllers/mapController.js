angular.module('smartEina')

    .controller('mapCtrl', ['$scope', '$state', 'map', 'auth', function ($scope, $state, map, auth) {
        // Miramos si esta loggeado
        auth.checkLogged();
    }]);