angular.module('smartEina')

    .controller('mapCtrl', ['$scope', '$state', 'map', 'auth', 'leafletData', '$uibModal', function ($scope, $state, map, auth, leafletData,$uibModal) {
        // Miramos si esta loggeado
        auth.checkLogged();
        $scope.userType = auth.getLoggedType();
        $scope.userName = auth.getLoggedUsername();

        $scope.msgErrorBusqueda = "";
        $scope.errorEnBusqueda = false;
        $scope.idEspacioBusqueda ="";

        $scope.horario = null;

        var plantaActual = 0;
        var latitude = 0;
        var longitude = 0;

        $scope.misIncidenciasActive = false;
        $scope.incidenciasGeneralesActive = false;
        $scope.administrarIncidenciasActive = false;
        $scope.mantenimientoGeneralesActive = false;
        $scope.mantenimientoAsignadasActive = false;
        $scope.busquedaActive = false;

        var hideErrorBusquedaMessage = function() {
            $scope.msgErrorBusqueda = "";
            $scope.errorEnBusqueda = false;
        };

        var callBackBusquedaEspacioExito = function(data) {
            console.log(data);
            console.log(data[0]);
            console.log(data[1]);
            console.log(data[2]);
            $scope.irPlanta(data[2]);
            angular.extend($scope, {
                cps: {
                    lat: data[1],
                    lng: data[2],
                    zoom: 22
                }
            });
            var idWFS = map.obtenerId($scope.layers.overlays.active.layerParams.layers, data[1], data[2]);

            idWFS.then(function (result) {
                var idCompleto = result;
                map.getInfo(idCompleto, getInfoSuccess, getInfoError);

                $scope.addMarker(data[1], data[2]);
            });
        };

        var callBackBusquedaEspacioFracaso = function() {
            $scope.msgErrorBusqueda = "El espacio buscado no existe.";
            $scope.errorEnBusqueda = true;
        };

        $scope.buscarEspacio = function(idBusqueda) {
            console.log(idBusqueda);
            if (idBusqueda == "") {
                $scope.msgErrorBusqueda = "El campo del id del espacio no puede estar en blanco.";
                $scope.errorEnBusqueda = true;
            } else {
                map.obtenerDatosGeograficosEspacio(idBusqueda, callBackBusquedaEspacioExito, callBackBusquedaEspacioFracaso);
            }
        };

        $scope.switchMisIncidenciasActive = function() {
          if ( $scope.misIncidenciasActive == false) {
              $scope.misIncidenciasActive = true;
          } else  $scope.misIncidenciasActive = false;
        };

        $scope.switchIncidenciasGeneralesActive = function() {
            if ( $scope.incidenciasGeneralesActive == false) {
                $scope.incidenciasGeneralesActive = true;
            } else  $scope.incidenciasGeneralesActive = false;
        };

        $scope.switchAdministrarIncidenciasActive = function() {
            if ( $scope.administrarIncidenciasActive == false) {
                $scope.administrarIncidenciasActive = true;
            } else  $scope.administrarIncidenciasActive = false;
        };

        $scope.switchIncidenciasGeneralesMantenimientoActive = function() {
            if ($scope.mantenimientoGeneralesActive == false) {
                $scope.mantenimientoGeneralesActive = true;
            } else $scope.mantenimientoGeneralesActive = false;
        };

        $scope.switchIncidenciasAsignadasMantenimientoActive = function() {
            if ($scope.mantenimientoAsignadasActive == false) {
                $scope.mantenimientoAsignadasActive = true;
            } else $scope.mantenimientoAsignadasActive = false;
        };

        $scope.switchBusquedaActive = function() {
            if ($scope.busquedaActive == false) {
                $scope.busquedaActive = true;
            } else $scope.busquedaActive = false;
        };

        $scope.incidenciasActivas = [];

        $scope.incidenciasAsignadas = [];

        $scope.markersActivas = {};

        $scope.allIncidencias = {
            "estado": "",
            "creadas": creadas = [],
            "aceptadas": aceptadas = [],
        };

        $scope.incidenciasUserBasico = {
            "estado": "",
            "creadas": creadas = [],
            "aceptadas": aceptadas = [],
            "completadas": completadas = [],
            "rechazadas": rechazadas = []
        };

        $scope.markersUserBasico = {
            "creadas": creadas = {},
            "aceptadas": aceptadas = {},
            "modificacion": modificacion = {},
            "completadas": completadas = {},
            "rechazadas": rechazadas = {}
        };

        var leafIcon =  {
            iconUrl: 'http://icons.iconarchive.com/icons/paomedia/small-n-flat/1024/wrench-icon.png',
                iconSize:     [60, 100], // size of the icon
                iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
                popupAnchor:  [-3, -76] // point from which the popup should open relative to the iconAnchor
        };

        var crearMarkerIncidenciasActivas = function(incidencia) {
            var m = {
                lat: incidencia.localizacion.y,
                lng: incidencia.localizacion.x,
                focus: false,
                draggable: false,
                icon: leafIcon
            };

            $scope.markersActivas.push(m);
        };

        var crearMarkerUserBasico = function(incidencia, tipo) {
            var m = {
                lat: incidencia.localizacion.y,
                lng: incidencia.localizacion.x,
                focus: false,
                draggable: false,
                icon: {
                    type: 'div',
                    iconSize: [230, 0],
                    html: '<span class="glyphicon glyphicon-wrench"></span>',
                    popupAnchor:  [0, 0]
                }
            };

            switch(tipo) {
                case "creadas": $scope.markersUserBasico.creadas.push(m); break;
                case "aceptadas": $scope.markersUserBasico.aceptadas.push(m); break;
                case "modificacion": $scope.markersUserBasico.modificacion.push(m); break;
                case "completadas": $scope.markersUserBasico.completadas.push(m); break;
                case "rechazadas": $scope.markersUserBasico.rechazadas.push(m); break;
            }
        };


        var llenarIncidenciasUserBasico = function(data) {
            $scope.incidenciasUserBasico = {
                "estado": "",
                "creadas": creadas = [],
                "aceptadas": aceptadas = [],
                "modificacion": modificacion = [],
                "completadas": completadas = [],
                "rechazadas": rechazadas = []
            };

            $scope.markersUserBasico = {
                "creadas": creadas = [],
                "aceptadas": aceptadas = [],
                "modificacion": modificacion = [],
                "completadas": completadas = [],
                "rechazadas": rechazadas = []
            };


            for (var i=0; i< data.length; i++) {
                var incidencia = data[i];
                switch (incidencia.estado) {
                    case "PENDIENTE":
                        $scope.incidenciasUserBasico.creadas.push(incidencia);
                        //crearMarkerUserBasico(incidencia, "creadas");
                        break;
                    case "INCOMPLETA":
                        $scope.incidenciasUserBasico.modificacion.push(incidencia);
                        //crearMarkerUserBasico(incidencia, "modificacion");
                        break;
                    case "ACEPTADA":
                        $scope.incidenciasUserBasico.aceptadas.push(incidencia);
                        //crearMarkerUserBasico(incidencia, "aceptadas");
                        break;
                    case "ASIGNADA":
                        $scope.incidenciasUserBasico.aceptadas.push(incidencia);
                        //crearMarkerUserBasico(incidencia, "aceptadas");
                        break;
                    case "COMPLETADA":
                        $scope.incidenciasUserBasico.completadas.push(incidencia);
                        //crearMarkerUserBasico(incidencia, "completadas");
                        break;
                    case "RECHAZADA":
                        $scope.incidenciasUserBasico.rechazadas.push(incidencia);
                        //crearMarkerUserBasico(incidencia, "rechazadas");
                        break;
                }
            }
        };

        var getInfoError = function () {
            $scope.data.idSelected = "No disponible";
            $scope.data.nombreSelected = "No disponible";
            $scope.data.edificioSelected = "No disponible";
            $scope.data.plantaSelected = "No disponible";
            $scope.data.usoSelected = "No disponible";
            $scope.horario = null;
        };

        var getInfoSuccess = function (espacio) {
            $scope.data.idSelected = espacio.id;
            $scope.data.nombreSelected = espacio.nombre;
            $scope.data.edificioSelected = espacio.edificio;
            $scope.data.plantaSelected = espacio.planta;
            $scope.data.usoSelected = espacio.tipoDeUso;
            $scope.horario = espacio.horario;
        };

        var llenarIncidenciasActivas = function(data) {
            console.log(data)
            $scope.incidenciasActivas = [];
            for (var i = 0; i< data.length; i++) {
                $scope.incidenciasActivas.push(data[i]);
            }
        };

        var llenarIncidenciasActivasAdmin = function(data) {
            $scope.allIncidencias.aceptadas = [];
            for (var i=0; i< data.length; i++) {
                $scope.allIncidencias.aceptadas.push(data[i]);
            }
        };

        var llenarIncidenciasCreadasAdmin = function(data) {
            $scope.allIncidencias.creadas = [];
            for (var i=0; i< data.length; i++) {
                $scope.allIncidencias.creadas.push(data[i]);
            }
        };

        var llenarIncidenciasAsignadas = function (data) {
            $scope.incidenciasAsignadas = [];
            for (var i=0; i< data.length; i++) {
                $scope.incidenciasAsignadas.push(data[i]);
            }
        };

        var obtenerIncidencias = function() {
            if ($scope.userType == 'Basico') {
                map.obtenerIncidenciasDeUsuario($scope.userName, llenarIncidenciasUserBasico);
                map.obtenerIncidenciasActivas(llenarIncidenciasActivas);
            } else if ($scope.userType == 'Administrador') {
                map.obtenerIncidenciasActivas(llenarIncidenciasActivasAdmin);
                map.obtenerIncidenciasCreadas(llenarIncidenciasCreadasAdmin);
            } else if ($scope.userType == 'Mantenimiento') {
                map.obtenerIncidenciasActivasMantenimiento(llenarIncidenciasActivas);
                map.obtenerIncidenciasAsignadas($scope.userName, llenarIncidenciasAsignadas);
            }
        };

        obtenerIncidencias();

        $scope.verIncidencia = function(incidencia) {
            var modalVerIncidencia = $uibModal.open({
                templateUrl: 'templates/editarIncidencia.html',
                animation: true,
                windowClass: 'modal',
                controller: 'modalEditarIncidenciaCtrl',
                keyboard: false,
                resolve: {
                    data: function () {
                        return {
                            modo: 'Ver',
                            idUsuario: incidencia.idUsuario,
                            idEspacio: incidencia.localizacion.idEspacio,
                            map: map,
                            planta: incidencia.localizacion.planta,
                            latitude:incidencia.localizacion.y,
                            longitude: incidencia.localizacion.x,
                            titulo: incidencia.titulo,
                            descripcion: incidencia.desc,
                            idIncidencia: incidencia.id
                        }
                    }
                },
            });

            modalVerIncidencia.result.then(function () {
                obtenerIncidencias();
            })
        };

        $scope.finalizarIncidencia = function(incidencia) {
          map.finalizarIncidencia(incidencia.id, obtenerIncidencias);
        };

        $scope.desasignarIncidencia = function(incidencia) {
            map.desAsignarIncidencia(incidencia.id, $scope.userName, obtenerIncidencias)
        };

        $scope.asignarIncidencia = function(incidencia) {
            var modalAsignarMantenimiento = $uibModal.open({
                templateUrl: 'templates/horarioAsignar.html',
                animation: true,
                windowClass: 'modal',
                keyboard: false,
                controller: 'modalAsignarMantenimientoCtrl',
                resolve: {
                    incidencia: function () {
                        return incidencia
                    },
                    idTrabajador: function () {
                        return $scope.userName
                    },
                    map: function () {
                        return map
                    }
                }
            });

            modalAsignarMantenimiento.result.then(function () {
                obtenerIncidencias();
            });
        };

        $scope.editarIncidenciaIncompleta = function(incidencia) {
            var modalEditarIncidenciaIncompleta = $uibModal.open({
                templateUrl: 'templates/editarIncidencia.html',
                animation: true,
                windowClass: 'modal',
                controller: 'modalEditarIncidenciaCtrl',
                keyboard: false,
                resolve: {
                    data: function () {
                        return {
                            modo: 'EditarIncompleta',
                            idUsuario: incidencia.idUsuario,
                            idEspacio: incidencia.localizacion.idEspacio,
                            map: map,
                            planta: incidencia.localizacion.planta,
                            latitude:incidencia.localizacion.y,
                            longitude: incidencia.localizacion.x,
                            titulo: incidencia.titulo,
                            descripcion: incidencia.desc,
                            idIncidencia: incidencia.id
                        }
                    }
                },
            });

            modalEditarIncidenciaIncompleta.result.then(function () {
                obtenerIncidencias();
            })
        };

        $scope.editarIncidencia = function(incidencia) {
            var modalEditarIncidencia = $uibModal.open({
                templateUrl: 'templates/editarIncidencia.html',
                animation: true,
                windowClass: 'modal',
                controller: 'modalEditarIncidenciaCtrl',
                keyboard: false,
                resolve: {
                    data: function () {
                        return {
                            modo: 'Editar',
                            idUsuario: incidencia.idUsuario,
                            idEspacio: incidencia.localizacion.idEspacio,
                            map: map,
                            planta: incidencia.localizacion.planta,
                            latitude:incidencia.localizacion.y,
                            longitude: incidencia.localizacion.x,
                            titulo: incidencia.titulo,
                            descripcion: incidencia.desc,
                            idIncidencia: incidencia.id
                        }
                    }
                },
            });

            modalEditarIncidencia.result.then(function () {
                obtenerIncidencias();
            })
        };

        $scope.centrarIncidencia = function(incidencia) {
            $scope.irPlanta(incidencia.localizacion.planta);
            angular.extend($scope, {
                cps: {
                    lat: incidencia.localizacion.y,
                    lng: incidencia.localizacion.x,
                    zoom: 22
                }
            });
            var idWFS = map.obtenerId($scope.layers.overlays.active.layerParams.layers, incidencia.localizacion.y, incidencia.localizacion.x);

            idWFS.then(function (result) {
                var idCompleto = result;
                map.getInfo(idCompleto, getInfoSuccess, getInfoError);

                $scope.addMarker(incidencia.localizacion.y, incidencia.localizacion.x);
            });
        };

        $scope.centrar = function() {
            angular.extend($scope, {
                cps: {
                    lat: 41.684106,
                    lng: -0.887497,
                    zoom: 17
                }
            });
        };

        $scope.aceptarIncidencia = function(incidencia) {
            map.aceptarIncidencia(incidencia.id, obtenerIncidencias)
        };

        $scope.rechazarIncidencia = function(incidencia) {
            map.rechazarIncidencia(incidencia.id, obtenerIncidencias)
        };

        $scope.pedirModificarIncidencia = function(incidencia) {
            map.pedirModificarIncidencia(incidencia.id, obtenerIncidencias)
        };

        $scope.verIncidencias = function() {
            var modalIncidencias = $uibModal.open({
                templateUrl: 'templates/incidencias.html',
                animation: true,
                windowClass: 'modal',
                keyboard: false,
                controller: 'modalIncidenciasCtrl',
                resolve: {
                    userType: function() {
                        return $scope.userType
                    },
                    idSelected: function () {
                        return $scope.data.idSelected
                    },
                    plantaActual: function () {
                        return plantaActual
                    },
                    userName: function () {
                        return $scope.userName
                    },
                    map: function () {
                        return map
                    },
                    uibModal: function () {
                        return $uibModal
                    },
                    latitude: function() {
                        return latitude
                    },
                    longitude: function() {
                        return longitude
                    },
                    uibModal: function() {
                        return $uibModal
                    }
                }
            });

            modalIncidencias.result.then(function () {
                obtenerIncidencias();
            })

        };

        $scope.basicLayer = {
            xyz: {
                name: 'OpenStreetMap',
                url: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
                type: 'xyz',
                layerOptions: {
                    visible: false,
                    attribution: '',
                    "showOnSelector": false,
                    minZoom: 10,
                    maxZoom: 21
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
                lat: 41.684106,
                lng: -0.887497,
                zoom: 17
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

        $scope.$on('leafletDirectiveMap.click', function (event, args) {
            var leafEvent = args.leafletEvent;
            latitude = leafEvent.latlng.lat;
            longitude = leafEvent.latlng.lng;
            var idWFS = map.obtenerId($scope.layers.overlays.active.layerParams.layers, latitude, longitude);

            idWFS.then(function (result) {
                var idCompleto = result;
                map.getInfo(idCompleto, getInfoSuccess, getInfoError);

                $scope.addMarker(latitude, longitude);
            });
        });



        $scope.addMarker = function (latitude, longitude) {
            $scope.clearMarkers();
            angular.extend($scope.data, {idSelected: "Cargando..."});
            angular.extend($scope.data, {nombreSelected: "Cargando..."});
            angular.extend($scope.data, {edificioSelected: "Cargando..."});
            angular.extend($scope.data, {plantaSelected: "Cargando..."});
            angular.extend($scope.data, {usoSelected: "Cargando..."});


            angular.extend($scope.data, {
                markers: {
                    m: {
                        lat: latitude,
                        lng: longitude,
                        focus: true,
                        getMessageScope: function () {
                            return $scope;
                        },
                        message: "<div class='container-fluid'>" +
                        "<div class='row'><p><strong>Identificiador:</strong></p><p> {{data.idSelected}} </p></div>" +
                        "<div class='row'><p><strong>Nombre:</strong></p><p> {{data.nombreSelected}} </p></div>" +
                        "<div class='row'><p><strong>Edificio:</strong></p><p> {{data.edificioSelected}} </p></div>" +
                        "<div class='row'><p><strong>Planta:</strong></p><p> {{data.plantaSelected}} </p></div>" +
                        "<div class='row'><p><strong>Tipo de uso:</strong></p><p> {{data.usoSelected}} </p></div>" +
                        "<div class='row'><button type='button' class='btn btn-primary' ng-click='verHorario()'>Ver horario</button></div>"+
                        "<div class='row'><button type='button' class='btn btn-primary' ng-click='verIncidencias()'>Ver incidencias</button></div>",
                        draggable: false,
                        compileMessage: true
                    }
                }
            });
        };

        $scope.clearMarkers = function () {
            $scope.data.markers = {};
        };

        $scope.irPlanta = function(planta) {
            switch(planta) {
                case "S1":
                    plantaActual = -1;
                    $scope.layers.overlays.active = $scope.definedOverlays.sotano;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case "00":
                    plantaActual = 0;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta0;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case "01":
                    plantaActual = 1;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta1;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case "02":
                    plantaActual = 2;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta2;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case "03":
                    plantaActual = 3;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta3;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
                case "04":
                    plantaActual = 4;
                    $scope.layers.overlays.active = $scope.definedOverlays.planta4;
                    $scope.layers.overlays.active.doRefresh = true;
                    break;
            };
        };

        $scope.subirPlanta = function () {
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
                case 4:
                    break;
            }
            $scope.layers.overlays.active.doRefresh = true;
        };

        $scope.bajarPlanta = function () {
            $scope.clearMarkers();
            switch (plantaActual) {
                case -1:
                    break;
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

        $scope.verPlanDeTrabajo = function() {
            console.log("Ver plan de trabajo")
            var modalPlanDeTrabajo = $uibModal.open({
                templateUrl: 'templates/planTrabajo.html',
                animation: true,
                windowClass: 'modal',
                keyboard: false,
                controller: 'modalPlanDeTrabajoCtrl',
                resolve: {
                    idTrabajador: function () {
                        return $scope.userName
                    },
                    map: function () {
                        return map
                    },
                    uibModal: function() {
                        return $uibModal
                    }
                }
            });

            modalPlanDeTrabajo.result.then(function () {

            })
        };

        $scope.verHorario = function () {
            var modalHorario = $uibModal.open({
                templateUrl: 'templates/horario.html',
                animation: true,
                windowClass: 'modal',
                keyboard: false,
                controller: 'modalHorarioCtrl',
                resolve: {
                    horarios: function () {
                        return $scope.horario
                    },
                    idSelected: function () {
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
                    },
                    uibModal: function () {
                        return $uibModal
                    }
                }
            });

            modalHorario.result.then(function () {

            })
        }
    }])

    .controller('modalHorarioCtrl', function ($scope, $uibModalInstance, horarios, idSelected, nombreSelected, userType, map, uibModal) {
        $scope.idSelected = idSelected;
        $scope.nombreSelected = nombreSelected;
        $scope.userType = userType;
        $scope.horarios = horarios;
        $scope.uibModal = uibModal;

        $scope.horasLunes = $scope.horarios.horasLunes;
        $scope.horasMartes = $scope.horarios.horasMartes;
        $scope.horasMiercoles= $scope.horarios.horasMiercoles;
        $scope.horasJueves = $scope.horarios.horasJueves;
        $scope.horasViernes = $scope.horarios.horasViernes;
        $scope.horasCheckear = null;

        $scope.actividadesLunes =[];
        $scope.actividadesMartes = [];
        $scope.actividadesMiercoles = [];
        $scope.actividadesJueves = [];
        $scope.actividadesViernes = [];

        var loadData = function () {
            for (var i = 0; i< 13; i++) {
                $scope.actividadesLunes.push("");
                $scope.actividadesMartes.push("");
                $scope.actividadesMiercoles.push("");
                $scope.actividadesJueves.push("");
                $scope.actividadesViernes.push("");
            }

            for (var i = 8; i <= 20; i++) {
                for (var j=0; j<  $scope.horasLunes.length; j++) {
                    if ($scope.horasLunes[j].horaDeInicio == i) {
                        $scope.actividadesLunes[i-8] = ($scope.horasLunes[j].actividad)
                        break;
                    }
                }
            }

            for (var i = 8; i <= 20; i++) {
                for (var j=0; j<  $scope.horasMartes.length; j++) {
                    if ($scope.horasMartes[j].horaDeInicio == i) {
                        $scope.actividadesMartes[i-8] = ($scope.horasMartes[j].actividad)
                        break;
                    }
                }
            }

            for (var i = 8; i <= 20; i++) {
                for (var j=0; j<  $scope.horasMiercoles.length; j++) {
                    if ($scope.horasMiercoles[j].horaDeInicio == i) {
                        $scope.actividadesMiercoles[i-8] = ($scope.horasMiercoles[j].actividad)
                        break;
                    }
                }
            }

            for (var i = 8; i <= 20; i++) {
                for (var j=0; j<  $scope.horasJueves.length; j++) {
                    if ($scope.horasJueves[j].horaDeInicio == i) {
                        $scope.actividadesJueves[i-8] = ($scope.horasJueves[j].actividad)
                        break;
                    }
                }
            }

            for (var i = 8; i <= 20; i++) {
                for (var j=0; j<  $scope.horasViernes.length; j++) {
                    if ($scope.horasViernes[j].horaDeInicio == i) {
                        $scope.actividadesViernes[i-8] = ($scope.horasViernes[j].actividad)
                        break;
                    }
                }
            }
        };

        loadData();

        $scope.isAdmin = function() {
            if ($scope.userType == "Administrador") return true;
            else return false;
        };

        var getInfoError = function() {};

        var getInfoSuccess = function (espacio) {
            $scope.horarios = espacio.horario
            $scope.horasLunes = $scope.horarios.horasLunes;
            $scope.horasMartes = $scope.horarios.horasMartes;
            $scope.horasMiercoles= $scope.horarios.horasMiercoles;
            $scope.horasJueves = $scope.horarios.horasJueves;
            $scope.horasViernes = $scope.horarios.horasViernes;

            $scope.actividadesLunes =[];
            $scope.actividadesMartes = [];
            $scope.actividadesMiercoles = [];
            $scope.actividadesJueves = [];
            $scope.actividadesViernes = [];

            loadData();
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

        $scope.loadActivities = function() {

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
            var modalEditar = $scope.uibModal.open({
                templateUrl: 'templates/editarHorario.html',
                animation: true,
                windowClass: 'modal',
                controller: 'modalEditarCtrl',
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
            });

            modalEditar.result.then(function (devolucion) {
                $scope.actualizar(devolucion.dia, devolucion.hora);
            })
        }
    })

    .controller('modalEditarCtrl', function ($scope, $uibModalInstance, data) {
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
            var datos = {
                idEspacio: $scope.idEspacio,
                dia: $scope.dia,
                hora: $scope.hora,
                actividad: $scope.actividad
            };
            map.guardarHora(datos, close)
        };
    })

    .controller('modalIncidenciasCtrl', function ($scope, $uibModalInstance, userType, idSelected, plantaActual, userName, map, uibModal, latitude, longitude) {
        $scope.idSelected = idSelected;
        $scope.plantaActual = plantaActual;
        $scope.userName = userName;
        $scope.userType = userType;
        $scope.map = map;
        $scope.uibModal = uibModal;
        $scope.latitude = latitude;
        $scope.longitude = longitude;
        $scope.uibModal = uibModal;

        $scope.incidenciasEspacio = [];

        var llenarIncidenciasEspacio = function(data) {
            $scope.incidenciasEspacio = [];
            for (var i=0; i< data.length; i++) {
                $scope.incidenciasEspacio.push(data[i])
            }
        };

        $scope.asignarIncidencia = function(incidencia) {
            var modalAsignarMantenimiento = $scope.uibModal.open({
                templateUrl: 'templates/horarioAsignar.html',
                animation: true,
                windowClass: 'modal',
                keyboard: false,
                controller: 'modalAsignarMantenimientoCtrl',
                resolve: {
                    incidencia: function () {
                        return incidencia
                    },
                    idTrabajador: function () {
                        return $scope.userName
                    },
                    map: function () {
                        return map
                    }
                }
            });

            modalAsignarMantenimiento.result.then(function () {
                if ($scope.userType == 'Mantenimiento') {
                    $scope.map.obtenerIncidenciasDeEspacioAceptadas($scope.idSelected, llenarIncidenciasEspacio);
                } else $scope.map.obtenerIncidenciasDeEspacio($scope.idSelected, llenarIncidenciasEspacio);
            });
        };

        if ($scope.userType == 'Mantenimiento') {
            $scope.map.obtenerIncidenciasDeEspacioAceptadas($scope.idSelected, llenarIncidenciasEspacio);
        } else $scope.map.obtenerIncidenciasDeEspacio($scope.idSelected, llenarIncidenciasEspacio);

        $scope.crearIncidencia = function() {
            var modalCrearIncidencia = $scope.uibModal.open({
                templateUrl: 'templates/editarIncidencia.html',
                animation: true,
                windowClass: 'modal',
                controller: 'modalEditarIncidenciaCtrl',
                keyboard: false,
                resolve: {
                    data: function () {
                        return {
                            modo: 'Crear',
                            idUsuario: $scope.userName,
                            idEspacio: $scope.idSelected,
                            map: $scope.map,
                            planta: $scope.plantaActual,
                            latitude: $scope.latitude,
                            longitude: $scope.longitude,
                            titulo: "",
                            descripcion: ""
                        }
                    }
                },
            });

            modalCrearIncidencia.result.then(function () {
                $scope.map.obtenerIncidenciasDeUsuario($scope.idSelected, llenarIncidenciasEspacio);
            })
        };

        $scope.verIncidencia = function(incidencia) {
            var modalVerIncidencia = $scope.uibModal.open({
                templateUrl: 'templates/editarIncidencia.html',
                animation: true,
                windowClass: 'modal',
                controller: 'modalEditarIncidenciaCtrl',
                keyboard: false,
                resolve: {
                    data: function () {
                        return {
                            modo: 'Ver',
                            idUsuario: $scope.userName,
                            idEspacio: $scope.idSelected,
                            map: $scope.map,
                            planta: $scope.plantaActual,
                            latitude: $scope.latitude,
                            longitude: $scope.longitude,
                            titulo: incidencia.titulo,
                            descripcion: incidencia.desc,
                            idIncidencia: incidencia.id
                        }
                    }
                },
            });

            modalVerIncidencia.result.then(function () {
            })
        };


        $scope.close = function () {
            $uibModalInstance.close();
        };
    })

    .controller('modalEditarIncidenciaCtrl', function ($scope, $uibModalInstance, data) {
        $scope.modo = data.modo;
        $scope.idUsuario = data.idUsuario;
        $scope.idEspacio = data.idEspacio;
        $scope.map = data.map;
        $scope.planta = data.planta;
        $scope.latitude = data.latitude;
        $scope.longitude = data.longitude;
        $scope.idIncidencia = data.idIncidencia;

        $scope.titulo = data.titulo;
        $scope.descripcion = data.descripcion;

        $scope.volver = function() {
            close();
        };

        $scope.actualizar = function() {
            $scope.map.actualizarIncidencia($scope.titulo, $scope.descripcion, $scope.idIncidencia, close)
        };

        var avisarIncompleta = function() {
            $scope.map.pedirModificarModificada($scope.idIncidencia, close);
        };

        $scope.actualizarIncompleta = function() {
            $scope.map.actualizarIncidencia($scope.titulo, $scope.descripcion, $scope.idIncidencia, avisarIncompleta)
        };


        $scope.guardar = function() {
            if ($scope.idEspacio == 'Exterior') {
                crear();
            } else {
                $scope.map.obtenerCoordenadas($scope.idEspacio, crearFromCoords);
            }
        };
        // titulo, descripcion, idUsuario, planta, x, y, idEspacio, callBack
        var crear = function() {
            var p = "";
            switch($scope.planta) {
                case -1: p = "S1"; break;
                case 0: p = "00"; break;
                case 1: p = "01"; break;
                case 2: p = "02"; break;
                case 3: p = "03"; break;
                case 4: p = "04"; break;
            }

            $scope.map.crearIncidencia($scope.titulo, $scope.descripcion, $scope.idUsuario,p,  $scope.longitude , $scope.latitude,  $scope.idEspacio, close);
        };

        var crearFromCoords = function(data) {
            var p = "";
            switch($scope.planta) {
                case -1: p = "S1"; break;
                case 0: p = "00"; break;
                case 1: p = "01"; break;
                case 2: p = "02"; break;
                case 3: p = "03"; break;
                case 4: p = "04"; break;
            }
            $scope.map.crearIncidencia($scope.titulo, $scope.descripcion, $scope.idUsuario,p, data[0], data[1], $scope.idEspacio, close);
        };

        var close = function () {
            $uibModalInstance.close();
        };
    })

    .controller('modalAsignarMantenimientoCtrl', function ($scope, $uibModalInstance, incidencia, idTrabajador, map) {
        $scope.idTrabajador = idTrabajador;
        $scope.map = map;
        $scope.incidencia = incidencia;

        $scope.horasLunes = [];
        $scope.horasMartes = [];
        $scope.horasMiercoles= [];
        $scope.horasJueves = [];
        $scope.horasViernes = [];

        $scope.asignadasLunes = [];
        $scope.asignadasMartes = [];
        $scope.asignadasMiercoles= [];
        $scope.asignadasJueves = [];
        $scope.asignadasViernes = [];

        $scope.actividadesLunes = [];
        $scope.actividadesMartes = [];
        $scope.actividadesMiercoles = [];
        $scope.actividadesJueves = [];
        $scope.actividadesViernes = [];

        var loadData = function () {
            for (var i = 0; i< 13; i++) {
                $scope.actividadesLunes.push("");
                $scope.actividadesMartes.push("");
                $scope.actividadesMiercoles.push("");
                $scope.actividadesJueves.push("");
                $scope.actividadesViernes.push("");
            }

            for (var i = 8; i <= 20; i++) {
                for (var j=0; j<  $scope.horasLunes.length; j++) {
                    if ($scope.horasLunes[j].horaDeInicio == i) {
                        $scope.actividadesLunes[i-8] = ($scope.horasLunes[j].actividad);
                        break;
                    }
                }
                for (var j=0; j< $scope.asignadasLunes.length; j++) {
                    if ($scope.asignadasLunes[j].hora == i) {
                        $scope.actividadesLunes[i-8] = "HORARIO ASIGNADO INCIDENCIA " + $scope.asignadasLunes[j].idIncidencia;
                        break;
                    }
                }
            }
            for (var i = 8; i <= 20; i++) {
                for (var j=0; j<  $scope.horasMartes.length; j++) {
                    if ($scope.horasMartes[j].horaDeInicio == i) {
                        $scope.actividadesMartes[i-8] = ($scope.horasMartes[j].actividad)
                        break;
                    }
                }
                for (var j=0; j< $scope.asignadasMartes.length; j++) {
                    if ($scope.asignadasMartes[j].hora == i) {
                        $scope.actividadesMartes[i-8] = "HORARIO ASIGNADO INCIDENCIA " + $scope.asignadasMartes[j].idIncidencia;
                        break;
                    }
                }
            }
            for (var i = 8; i <= 20; i++) {
                for (var j=0; j<  $scope.horasMiercoles.length; j++) {
                    if ($scope.horasMiercoles[j].horaDeInicio == i) {
                        $scope.actividadesMiercoles[i-8] = ($scope.horasMiercoles[j].actividad)
                        break;
                    }
                }
                for (var j=0; j< $scope.asignadasMiercoles.length; j++) {
                    if ($scope.asignadasMiercoles[j].hora == i) {
                        $scope.actividadesMiercoles[i-8] = "HORARIO ASIGNADO INCIDENCIA " + $scope.asignadasMiercoles[j].idIncidencia;
                        break;
                    }
                }
            }
            for (var i = 8; i <= 20; i++) {
                for (var j=0; j<  $scope.horasJueves.length; j++) {
                    if ($scope.horasJueves[j].horaDeInicio == i) {
                        $scope.actividadesJueves[i-8] = ($scope.horasJueves[j].actividad)
                        break;
                    }
                }
                for (var j=0; j< $scope.asignadasJueves.length; j++) {
                    if ($scope.asignadasJueves[j].hora == i) {
                        $scope.actividadesJueves[i-8] = "HORARIO ASIGNADO INCIDENCIA " + $scope.asignadasJueves[j].idIncidencia;
                        break;
                    }
                }
            }
            for (var i = 8; i <= 20; i++) {
                for (var j=0; j<  $scope.horasViernes.length; j++) {
                    if ($scope.horasViernes[j].horaDeInicio == i) {
                        $scope.actividadesViernes[i-8] = ($scope.horasViernes[j].actividad)
                        break;
                    }
                }
                for (var j=0; j< $scope.asignadasViernes.length; j++) {
                    if ($scope.asignadasViernes[j].hora == i) {
                        $scope.actividadesViernes[i-8] = "HORARIO ASIGNADO INCIDENCIA " + $scope.asignadasViernes[j].idIncidencia;
                        break;
                    }
                }
            }
        };

        var callBackAsignadas = function(data) {
            for (var i = 0; i< data.length; i++) {
                switch(data[i].dia) {
                    case "Lunes":
                        $scope.asignadasLunes.push(data[i]);
                        break;
                    case "Martes":
                        $scope.asignadasMartes.push(data[i]);
                        break;
                    case "Miercoles":
                        $scope.asignadasMiercoles.push(data[i]);
                        break;
                    case "Jueves":
                        $scope.asignadasJueves.push(data[i]);
                        break;
                    case "Viernes":
                        $scope.asignadasViernes.push(data[i]);
                        break;
                }
            }

            loadData();
        };

        var obtenerAsignadasDeEspacio = function() {
            $scope.map.obtenerAsignadasDeEspacio($scope.incidencia.localizacion.idEspacio, callBackAsignadas);
        };


        var callBackHorarioSuccess = function(data) {
            $scope.horasLunes = data.horario.horasLunes;
            $scope.horasMartes = data.horario.horasMartes;
            $scope.horasMiercoles= data.horario.horasMiercoles;
            $scope.horasJueves = data.horario.horasJueves;
            $scope.horasViernes = data.horario.horasViernes;

            obtenerAsignadasDeEspacio();
        };

        var callBackHorarioFail = function() {};

        $scope.map.getInfo($scope.incidencia.localizacion.idEspacio, callBackHorarioSuccess, callBackHorarioFail);

        var callBackAsignada = function() {
            $uibModalInstance.close();
        };

        $scope.asignar = function(dia, hora) {
            $scope.map.asignarIncidencia($scope.idTrabajador, $scope.incidencia.id, dia, hora, callBackAsignada)
        };

        $scope.close = function () {
            $uibModalInstance.close();
        };

    })

    .controller('modalPlanDeTrabajoCtrl', function ($scope, $uibModalInstance, idTrabajador, map, uibModal) {
        $scope.idTrabajador = idTrabajador;
        $scope.map = map;
        $scope.uibModal = uibModal;

        $scope.asignadasLunes = [];
        $scope.asignadasMartes = [];
        $scope.asignadasMiercoles = [];
        $scope.asignadasJueves = [];
        $scope.asignadasViernes = [];

        var callBack = function(data) {
            $scope.asignadasLunes = [];
            $scope.asignadasMartes = [];
            $scope.asignadasMiercoles = [];
            $scope.asignadasJueves = [];
            $scope.asignadasViernes = [];
            for (var i = 0; i< data.length; i++) {
                switch(data[i].dia) {
                    case "Lunes":
                        $scope.asignadasLunes.push(data[i]);
                        break;
                    case "Martes":
                        $scope.asignadasMartes.push(data[i]);
                        break;
                    case "Miercoles":
                        $scope.asignadasMiercoles.push(data[i]);
                        break;
                    case "Jueves":
                        $scope.asignadasJueves.push(data[i]);
                        break;
                    case "Viernes":
                        $scope.asignadasViernes.push(data[i]);
                        break;
                }
            }
        };



        $scope.verIncidencia = function(idIncidencia) {
            console.log(idIncidencia)
            $scope.map.obtenerIncidencia(idIncidencia, verIncidenciaCallBack);
        };

        var obtenerIncidenciasAsignadaTrabajador = function() {
            $scope.map.obtenerAsignadasDeTrabajador(idTrabajador, callBack);
        };

        obtenerIncidenciasAsignadaTrabajador();

        var verIncidenciaCallBack = function(incidencia) {
            console.log("....................")
            console.log(incidencia)
            var modalVerIncidencia = $scope.uibModal.open({
                templateUrl: 'templates/editarIncidencia.html',
                animation: true,
                windowClass: 'modal',
                controller: 'modalEditarIncidenciaCtrl',
                keyboard: false,
                resolve: {
                    data: function () {
                        return {
                            modo: 'Ver',
                            idUsuario: incidencia.idUsuario,
                            idEspacio: incidencia.localizacion.idEspacio,
                            map: map,
                            planta: incidencia.localizacion.planta,
                            latitude:incidencia.localizacion.y,
                            longitude: incidencia.localizacion.x,
                            titulo: incidencia.titulo,
                            descripcion: incidencia.desc,
                            idIncidencia: incidencia.id
                        }
                    }
                },
            });

            modalVerIncidencia.result.then(function () {
                obtenerIncidenciasAsignadaTrabajador();
            })
        };

        $scope.volver = function() {
            close();
        };
        var close = function () {
            $uibModalInstance.close();
        };
    });
