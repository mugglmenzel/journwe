define(['common/async!https://maps.googleapis.com/maps/api/js?libraries=places&key=AIzaSyAbYnwpdOgqWhspiETgFdlXyX3H2Fjb8fY&sensor=false'],
    function () {
        // return the gmaps namespace for brevity
        return $.extend({resetBounds: function(map, markers){
            var bounds = new google.maps.LatLngBounds();
            for(var i in markers) {
                bounds.extend(markers[i].getPosition());
            }
            map.fitBounds(bounds);
            if(map.getZoom() > 10) map.setZoom(10);
            google.maps.event.trigger(map, 'resize');
        }}, window.google.maps);
    });
