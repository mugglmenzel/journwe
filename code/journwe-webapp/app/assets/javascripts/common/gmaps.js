define(['config', 'common/async!https://maps.googleapis.com/maps/api/js?libraries=places,weather,panoramio&key=AIzaSyAbYnwpdOgqWhspiETgFdlXyX3H2Fjb8fY&sensor=false'],
    function () {

        console.log("google maps api injected and loaded.");
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
                    val.setMap(null);
                });
            },
            setCenterOffset: function (map, latlng, offsetX, offsetY) {
                var point1 = map.getProjection().fromLatLngToPoint(
                    (latlng instanceof window.google.maps.LatLng) ? latlng : map.getCenter()
                );
                var point2 = new window.google.maps.Point(
                        ( (typeof(offsetX) == 'number' ? offsetX : 0) / Math.pow(2, map.getZoom()) ) || 0,
                        ( (typeof(offsetY) == 'number' ? offsetY : 0) / Math.pow(2, map.getZoom()) ) || 0
                );
                map.setCenter(map.getProjection().fromPointToLatLng(new google.maps.Point(
                        point1.x - point2.x,
                        point1.y - point2.y
                )));
                window.google.maps.event.trigger(map, 'resize');
            }

        });
});  //'common/google!maps,3,other_params:[libraries="places,weather,panoramio",key=AIzaSyAbYnwpdOgqWhspiETgFdlXyX3H2Fjb8fY,sensor=false]'
//'common/async!https://maps.googleapis.com/maps/api/js?libraries=places,weather,panoramio&key=AIzaSyAbYnwpdOgqWhspiETgFdlXyX3H2Fjb8fY&sensor=false'