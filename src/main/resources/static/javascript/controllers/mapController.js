angular.module('smartEina')

    .controller('mapCtrl', ['$scope', '$state', 'map', 'auth', 'leafletData', '$uibModal', function ($scope, $state, map, auth, leafletData,$uibModal) {
        // Miramos si esta loggeado
        auth.checkLogged();
        $scope.userType = auth.getLoggedType();

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
            var idWFS = map.obtenerId($scope.layers.overlays.active.layerParams.layers, latitude, longitude);

            idWFS.then(function (result) {
                var idCompleto = result;
                map.getInfo(idCompleto, getInfoSuccess, getInfoError);

                $scope.addMarker(latitude, longitude);
            });
            //map.getInfo("CRE.1065.00.020", getInfoSuccess, getInfoError);
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
                        "<div class='row'><p><strong>Identificiador:</strong></p><p> {{data.idSelected}} </p></div>" +
                        "<div class='row'><p><strong>Nombre:</strong></p><p> {{data.nombreSelected}} </p></div>" +
                        "<div class='row'><p><strong>Edificio:</strong></p><p> {{data.edificioSelected}} </p></div>" +
                        "<div class='row'><p><strong>Planta:</strong></p><p> {{data.plantaSelected}} </p></div>" +
                        "<div class='row'><p><strong>Tipo de uso:</strong></p><p> {{data.usoSelected}} </p></div>" +
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
            $scope.clearMarkers();
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
            $scope.clearMarkers();
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
            $uibModal.open({
                templateUrl: 'templates/horario.html',
                windowClass: 'center-modal',
                keyboard: false,
                resolve: {
                    horarios: function() {
                        return $scope.horario
                    },
                    idSelected: function() {
                        return $scope.data.idSelected
                    },
                    nombreSelected: function () {
                        return $scope.data.nombreSelected
                    },
                    userType: function () {
                        return $scope.userType
                    },
                    map: function () {
                        return map
                    }
                },
                controller: function ($scope, $uibModalInstance, horarios, idSelected, nombreSelected, userType, map) {
                    $scope.idSelected = idSelected
                    $scope.nombreSelected = nombreSelected
                    $scope.userType = userType
                    $scope.horarios = horarios

                    $scope.horasLunes = $scope.horarios.horasLunes;
                    $scope.horasMartes = $scope.horarios.horasMartes;
                    $scope.horasMiercoles= $scope.horarios.horasMiercoles;
                    $scope.horasJueves = $scope.horarios.horasJueves;
                    $scope.horasViernes = $scope.horarios.horasViernes;
                    $scope.horasCheckear = null;

                    $scope.isAdmin = function() {
                      if ($scope.userType == "Administrador") return true;
                        else return false;
                    };

                    var getInfoError = function() {};

                    var getInfoSuccess = function (espacio) {
                        var elementoActualizar = "";
                        switch($scope.diaActualizar) {
                            case "Lunes": elementoActualizar = 'l' +  $scope.horaActualizar; break;
                            case "Martes": elementoActualizar = 'm' +  $scope.horaActualizar; break;
                            case "Miercoles":elementoActualizar = 'x' +  $scope.horaActualizar; break;
                            case "Jueves": elementoActualizar = 'j' +  $scope.horaActualizar; break;
                            case "Viernes":elementoActualizar = 'v' +  $scope.horaActualizar;  break;
                        }
                        console.log("Success: " + elementoActualizar);
                        $scope.horarios = espacio.horario;

                        // TODO: pendiente de hacer la actualizacion instantanea de la GUI
                    };

                    $scope.diaActualizar = "";
                    $scope.horaActualizar = "";

                    $scope.actualizar = function(dia, hora) {
                        $scope.diaActualizar = dia;
                        $scope.horaActualizar = hora;
                        map.getInfo($scope.idSelected, getInfoSuccess, getInfoError);
                    };

                    $scope.editar = function(dia, hora) {
                        $scope.verEditar(dia,hora,$scope.showActividad(dia, hora),$scope.idSelected, map)
                    };

                    $scope.showActividad = function(dia, hora) {
                        switch(dia) {
                            case "Lunes": $scope.horasCheckear = $scope.horasLunes; break;
                            case "Martes": $scope.horasCheckear = $scope.horasMartes; break;
                            case "Miercoles": $scope.horasCheckear = $scope.horasMiercoles; break;
                            case "Jueves": $scope.horasCheckear = $scope.horasJueves; break;
                            case "Viernes": $scope.horasCheckear = $scope.horasViernes; break;
                        }

                        for (var i=0; i< $scope.horasCheckear.length; i++) {
                            if ($scope.horasCheckear[i].horaDeInicio == hora) {
                                return $scope.horasCheckear[i].actividad;
                            }
                        }
                        return "";

                    };

                    $scope.close = function () {
                        $uibModalInstance.close();
                    };

                    $scope.verEditar = function(dia, hora, actividad, idEspacio,map) {
                        var modalEditar = $uibModal.open({
                            templateUrl: 'templates/editarHorario.html',
                            windowClass: 'center-modal',
                            keyboard: false,
                            resolve: {
                                data: function () {
                                    return {
                                        dia: dia,
                                        hora: hora,
                                        actividad: actividad,
                                        idEspacio: idEspacio,
                                        map: map
                                    }
                                }
                            },
                            controller: function ($scope, $uibModalInstance, data) {
                                $scope.dia = data.dia;
                                $scope.hora = data.hora;
                                $scope.actividad = data.actividad;
                                $scope.idEspacio = data.idEspacio;

                                var map = data.map;

                                var close = function () {
                                    var devolucion = {
                                        dia: $scope.dia,
                                        hora: $scope.hora
                                    };

                                    $uibModalInstance.close(devolucion);
                                };

                                $scope.guardar = function() {
                                    console.log("Actividad a guardar: " + $scope.actividad)
                                    var datos = {
                                        idEspacio: $scope.idEspacio,
                                        dia: $scope.dia,
                                        hora: $scope.hora,
                                        actividad: $scope.actividad
                                    };
                                    map.guardarHora(datos, close)
                                };
                            }
                        });

                        modalEditar.result.then(function (devolucion) {
                            $scope.actualizar(devolucion.dia, devolucion.hora);
                        });
                    }
                }
            })
        }
    }]);
