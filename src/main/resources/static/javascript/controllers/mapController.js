angular.module('smartEina')

    .controller('mapCtrl', ['$scope', '$state', 'map', 'auth', function ($scope, $state, map, auth) {
        // Miramos si esta loggeado
        auth.checkLogged();

      angular.extend($scope, {
        Barcelona: {
          lat: 41.3825,
          lng: 2.176944,
          zoom: 12
        },
        cps: {
          lat : 41.684106,
          lng :-0.887497,
          zoom : 17
        }
      });

      //map.getInfo()

    }]);