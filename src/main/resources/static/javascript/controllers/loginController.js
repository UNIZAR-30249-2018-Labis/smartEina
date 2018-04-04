angular.module('smartEina')

    .controller('loginCtrl', ['$scope', '$state', 'auth', function ($scope, $state, auth) {

        // inputs visual variables
        $scope.userName = "";
        $scope.password = "";
        
        // send the login form to the auth service
        $scope.logIn = function () {
            var user = $scope.userName;
            var password = $scope.password;
            // Standard 'authorization basic'
            auth.logIn(user, password, showError);
        }

        // go to create a new account
        $scope.goToSignUp = function () {
            $state.go('signup')
        }
    }]);