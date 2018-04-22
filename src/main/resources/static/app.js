angular.module('smartEina', ['ui.router' , 'leaflet-directive', 'ui.bootstrap'])

    .config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider

            .state('login', {
                url: "/login",
                templateUrl: "templates/login.html",
                controller: "loginCtrl"
            })

            .state('signup', {
                url: "/signUp",
                templateUrl: "templates/signup.html",
                controller: "signUpCtrl"
            })

            .state('map', {
                url: "/map",
                templateUrl: "templates/map.html",
                controller: "mapCtrl"
            });

        $urlRouterProvider.otherwise('login');
    });