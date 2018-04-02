angular.module('smartEina')

    .controller('loginCtrl', ['$scope', '$state', 'auth', function ($scope, $state, auth) {

        // inputs visual variables
        $scope.userName = "";
        $scope.password = "";

        // feedback handling variables
        $scope.errorMsg = "";
        $scope.error = false;

        // hide the error login message when is true respectively
        $scope.hideError = function () {
            $scope.errorMsg = "";
            $scope.error = false;
        };

        // show the error login message when is false respectively
        var showError = function (error) {
            $scope.errorMsg = error;
            $scope.error = true;
        };

        // send the login form to the auth service
        $scope.logIn = function () {
            var user = $scope.userName;
            var password = $scope.password;
            // Standard 'authorization basic'
            console.log($scope.userName)
            auth.logIn(user, password, showError);
        }

        // go to create a new account
        $scope.goToSignUp = function () {
            $state.go('signup')
        }
    }]);