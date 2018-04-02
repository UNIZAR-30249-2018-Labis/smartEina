angular.module('smartEina')

    .controller('signUpCtrl', ['$scope', '$state', 'auth', function ($scope, $state, auth) {

        // inputs visual variables
        $scope.userName = "";
        $scope.email = "";
        $scope.password = "";
        $scope.rePassword = "";

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

        $scope.signUp = function () {
            // check if the both passwords match
            if ($scope.password !== $scope.rePassword) {
                showError('Invalid passwords');
            } else {
                var userObject = {
                    user: $scope.userName,
                    email: $scope.email,
                    pass: $scope.password,
                    repass: $scope.rePassword

                };
                auth.signUp(userObject, showSuccess, showError);
            }
        }
    }]);