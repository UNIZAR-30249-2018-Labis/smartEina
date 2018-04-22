angular.module('smartEina')

    .controller('mapCtrl', ['$scope', '$state', 'map', 'auth', 'leafletData', function ($scope, $state, map, auth, leafletData) {
        // Miramos si esta loggeado
        auth.checkLogged();

        $scope.horario = null;

        var plantaActual = 0;

        $scope.basicLayer = {
            xyz: {
                name: 'OpenStreetMap',
                url: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                type: 'xyz',
                layerOptions: {
                    visible: false,
                    attribution: '',
                    minZoom: 16,
                    "showOnSelector": false

                }
            }
        };

        $scope.definedOverlays = {
            sotano: map.crearCapa("Sótano", 'Labis:s01'),
            planta0: map.crearCapa("Planta 0", 'Labis:p00'),
            planta1: map.crearCapa("Planta 1", 'Labis:p01'),
            planta2: map.crearCapa("Planta 2", 'Labis:p02'),
            planta3: map.crearCapa("Planta 3", 'Labis:p03'),
            planta4: map.crearCapa("Planta 4", 'Labis:p04')
        };

        angular.extend($scope, {
            cps: {
              lat : 41.684106,
              lng :-0.887497,
                zoom: 19
            },
            events: {
                map: {
                    enable: ['click'],
                    logic: 'emit'
                }
            },
            layers: {
                baselayers: {
                    xyz: $scope.basicLayer.xyz
                },
                overlays: {
                    active: $scope.definedOverlays.planta0
                }
            },
            data: {markers: {}},
            controls: {
                bajarNivel: {
                    type: 'bajarPlanta'
                },
                subirNivel: {
                    type: 'subirPlanta'
                }
            }
        });


        $scope.$on('leafletDirectiveMap.click', function(event, args) {
            var leafEvent = args.leafletEvent;
            var latitude = leafEvent.latlng.lat;
            var longitude = leafEvent.latlng.lng;

            console.log(latitude + " - " + longitude);

            //map.obtenerId($scope.layers.overlays.active, latitude, longitude);
            map.getInfo("CRE.1065.00.020", getInfoSuccess, getInfoError);

            $scope.addMarker(latitude, longitude);
        });

        var getInfoError = function () {
            $scope.data.idSelected = "No disponible";
            $scope.data.nombreSelected = "No disponible";
            $scope.data.edificioSelected = "No disponible";
            $scope.data.plantaSelected = "No disponible";
            $scope.data.usoSelected = "No disponible";
            $scope.horario = null;
        };

        var getInfoSuccess = function (espacio) {
            console.log(espacio)
            $scope.data.idSelected = espacio.id;
            $scope.data.nombreSelected = espacio.nombre;
            $scope.data.edificioSelected = espacio.edificio;
            $scope.data.plantaSelected = espacio.planta;
            $scope.data.usoSelected = espacio.tipoDeUso;
            $scope.horario = espacio.horario;
        };

        $scope.addMarker = function(latitude, longitude) {
            $scope.clearMarkers();
            angular.extend($scope.data, { idSelected: "Cargando..."});
            angular.extend($scope.data, { nombreSelected: "Cargando..."});
            angular.extend($scope.data, { edificioSelected: "Cargando..."});
            angular.extend($scope.data, { plantaSelected: "Cargando..."});
            angular.extend($scope.data, { usoSelected: "Cargando..."});

            angular.extend($scope.data, {
                markers: {
                    m: {
                        lat: latitude,
                        lng: longitude,
                        focus: true,
                        getMessageScope: function () { return $scope; },
                        message: "<div class='container-fluid'>" +
                        "<row><p><strong>Identificiador:</strong></p><p> {{data.idSelected}} </p></row>" +
                        "<row><p><strong>Nombre:</strong></p><p> {{data.nombreSelected}} </p></row>" +
                        "<row><p><strong>Edificio:</strong></p><p> {{data.edificioSelected}} </p></row>" +
                        "<row><p><strong>Planta:</strong></p><p> {{data.plantaSelected}} </p></row>" +
                        "<row><p><strong>Tipo de uso:</strong></p><p> {{data.usoSelected}} </p></row>" +
                        "<button type='button' class='btn btn-primary' ng-click='verHorario()'>Ver horario</button></div>",
                        draggable: false,
                        compileMessage: true
                    }
                }
            });
        };

        $scope.clearMarkers = function () {
            $scope.data.markers = {};
        };

        $scope.subirPlanta = function() {
            switch (plantaActual) {
                case -1:
                    //delete $scope.layers.overlays.sotano;
                    plantaActual = 0;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta0;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case 0:
                    //delete $scope.layers.overlays.planta0;
                    plantaActual = 1;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta1;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case 1:
                    //delete $scope.layers.overlays.planta1;
                    plantaActual = 2;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta2;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case 2:
                    //delete $scope.layers.overlays.planta2;
                    plantaActual = 3;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta3;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case 3:
                    //delete $scope.layers.overlays.planta3;
                    plantaActual = 4;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta4;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case 4:break;
            }
            $scope.layers.overlays.active.doRefresh = true;
        };

        $scope.bajarPlanta = function() {
            switch (plantaActual) {
                case -1: break;
                case 0:
                    //delete $scope.layers.overlays.planta0;
                    plantaActual = -1;
                    $scope.layers.overlays.active = $scope.definedOverlays.sotano;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case 1:
                    //delete $scope.layers.overlays.planta1;
                    plantaActual = 0;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta0;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case 2:
                    //delete $scope.layers.overlays.planta2;
                    plantaActual = 1;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta1;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case 3:
                    //delete $scope.layers.overlays.planta3;
                    plantaActual = 2;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta2;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case 4:
                   //delete $scope.layers.overlays.planta4;
                    plantaActual = 3;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta3;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
            }
        };

        $scope.verHorario = function() {

        }
    }]);
