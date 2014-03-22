define(['common/async!https://maps.googleapis.com/maps/api/js?libraries=places,weather,panoramio&key=AIzaSyAbYnwpdOgqWhspiETgFdlXyX3H2Fjb8fY&sensor=false'],
    function () {
        // return the gmaps namespace for brevity
        return $.extend(window.google.maps, {
            resetBounds: function (map, markers) {
                if (!map) {
                    return;
                }
                if (!$.isEmptyObject(markers)) {
                    var bounds = new window.google.maps.LatLngBounds();
                    for (var i in markers) {
                        bounds.extend(markers[i].getPosition());
                    }
                    map.fitBounds(bounds);
                } else if (navigator.geolocation) {
                    navigator.geolocation.getCurrentPosition(function (position) {
                        var pos = new window.google.maps.LatLng(position.coords.latitude, position.coords.longitude);
                        map.setCenter(pos);
                    }, function () {
                    });
                }
                if (map.getZoom() > 10) map.setZoom(10);
                window.google.maps.event.trigger(map, 'resize');
            },
            showMapLayers: function (map, layers) {
                $.each(layers, function (i, val) {
                    val.setMap(map);
                });
            },
            hideMapLayers: function (map, layers) {
                $.each(layers, function (i, val) {
                    val.setMap(map);
                });
            }

        });
    });
