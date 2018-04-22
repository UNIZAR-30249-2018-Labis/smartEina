angular.module('smartEina')

// 'auth' service manage the authentication function of the page with the server
    .factory('auth', function ($state, $http, $httpParamSerializer) {
        var session = undefined,
            _authenticated = false,
            loggedUsername ="",
            loggedType="";

        return {
            //return true if the user is authenticated
            isAuthenticated: function () {
                if (_authenticated) {
                    return _authenticated;
                } else {
                    var tmp = angular.fromJson(localStorage.sessionJWT);
                    if (tmp !== undefined) {
                        this.authenticate(tmp);
                        return _authenticated;
                    } else {
                        return false;
                    }
                }
            },

            //authenticate the [identity] user
            authenticate: function (jwt) {
                session = jwt;
                _authenticated = jwt !== undefined;
                localStorage.sessionJWT = angular.toJson(session);
            },

            // Funcion que checkea las redirecciones al entrar a una pagina (state) nueva
            checkLogged: function () {
                if (!this.isAuthenticated()) {
                    if ($state.is('login')) {}
                    else if ($state.is('signup')){}
                    else $state.go('login')
                } else {
                    if ($state.is('login')) {
                        $state.go('map')
                    } else if ($state.is('signup')) {
                        $state.go('map')
                    }
                }
            },

            getSession: function () {
                return session;
            },

            getLoggedUsername: function () {
                return loggedUsername;
            },

            getLoggedType: function () {
                return loggedType;
            },

            //logout function
            logout: function () {
                session = undefined;
                _authenticated = false;
                loggedUsername = "";
                loggedType = "";
                localStorage.removeItem("sessionJWT");
                $state.go('login');
            },

            //send the login info to the server
            logIn: function (user, password, callback) {
                var that = this;
                $http({
                    method: 'GET',
                    url: '/logIn',
                    headers: {
                        'user': user,
                        'pass': password
                    }
                }).success(function (data, status, headers) {
                    that.authenticate(headers().authorization);
                    loggedUsername = headers().username;
                    loggedType = headers().type;
                    if (loggedType == 'Administrador') {
                        $state.go('map');
                    } else if (loggedType == 'Mantenimiento') {
                        // Vamos al mapa de mantenimiento
                    } else {
                        $state.go('map');
                    }
                }).error(function (data) {
                    callback(data);
                });
            },

            //send the register info to the server
            signUp: function (userObject, callbackSuccess, callbackError) {
                $http({
                    method: 'POST',
                    url: '/signUp',
                    data: $httpParamSerializer(userObject),
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).success(function (data) {
                    $state.go('login');
                    callbackSuccess(data);
                }).error(function (data) {
                    callbackError(data);
                });
            }
        }
    })

    // Llamadas de la pantalla MAP
    .factory('map', function ($state, $http, $httpParamSerializer) {
        return {
            guardarHora: function (datos, callBack) {
                $http({
                    method: 'POST',
                    url: '/guardarHora',
                    params: datos
                }).success(function(data) {
                    console.log("Exito");
                    callBack()
                }).error(function(data) {
                    console.log("Fallo");
                    callBack()
                });
            },

            getInfo: function (id, callbackSucces, callbackError) {
                $http({
                    method: 'GET',
                    url: '/espacios',
                    headers: {
                        'id': id
                    }
                }).success(function (data, status, headers) {
                    callbackSucces(JSON.parse(headers().espacio));
                }).error(function (data) {
                    callbackError
                });
            },

            crearCapa: function (nombreLeyenda, nombreCapa) {
                return {
                    name: nombreLeyenda,
                    type: 'wms',
                    visible: true,
                    url: 'http://ec2-18-222-45-196.us-east-2.compute.amazonaws.com:8080/geoserver/Labis/wms',
                    tiled: true,
                    layerParams: {
                        layers: nombreCapa,
                        format: 'image/png',
                        transparent: true,
                        "showOnSelector": false
                    },
                    layerOptions: {
                        attribution: "",
                    }
                }
            },

            obtenerId: function (nombreCapa, lat, lng) {
                var url = "http://ec2-18-222-45-196.us-east-2.compute.amazonaws.com:8080/geoserver/Labis/wfs?service" +
                    "=WFS&VERSION=1.0.0&request=GetFeature&typeName=" + nombreCapa + "&outputFormat=application%2Fjson&CQL_FILTER=" +
                    "CONTAINS(the_geom,%20Point(" + lng + "%20" + lat + "))&propertyName=ID_UTC&propertyName=ID_EDIFICI";

                return $http.get(url).then(function (response) {
                    var r = response.data.features;

                    if (r === undefined || r[0] === undefined) {
                        return undefined;
                    } else {
                        console.log("UTC: " + r[0]['properties'].ID_UTC);
                        console.log("EDI: " + r[0]['properties'].ID_EDIFICI);

                        return r[0]['properties'].ID_EDIFICI + "." + r[0]['properties'].ID_UTC;
                    }
                }, function () {
                    return undefined;
                });
            }
        }
    });

