
angular.module('smartEina')

    .controller('navbarCtrl', ['$scope', '$state', 'auth', function ($scope, $state, auth) {

        $scope.getUsername = function () {
            console.log(auth.getLoggedUsername())
            return auth.getLoggedUsername();
        };

        $scope.getType = function () {
            console.log(auth.getLoggedType())
            return auth.getLoggedType();
        };

        $scope.notBasicUser = function () {
            if(auth.getLoggedType() == 'Administrador' || auth.getLoggedType() == 'Mantenimiento') {
                return true;
            } else return false;
        };

        $scope.logged = function () {
            return auth.isAuthenticated();
        };

        $scope.logout = function () {
            auth.logout();
        };
    }]);