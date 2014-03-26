require([
    "common/utils",
    "routes",
    "common/gmaps",
    "common/index",
    "main",
    "categoryData"

], function (utils, routes, gmaps, index, main, cat) {
    var inspirationsMap,
        inspirationsMarkers = [];
    var inspirationsInfos = [];


    var initialize = function () {

        index.initializeCategories(null, cat.id);

        initializeInspirationsMap();
        loadInspirations(null, true);

        index.initializePublicAdventures();
    };

    var initializeInspirationsMap = function () {
        gmaps.visualRefresh = true;
        var mapOptions = {
            zoom: 5,
            center: new gmaps.LatLng(49.483472, 8.476992),
            mapTypeId: gmaps.MapTypeId.ROADMAP
        };
        if($('#inspirations-places-map').length) inspirationsMap = new gmaps.Map(document.getElementById('inspirations-places-map'), mapOptions);
    }


    var loadInspirations = function (lastId, clear) {
        utils.setSpinning($('#inspirations-public-button-refresh i'));
        utils.setReplaceSpinning($('#inspirations-public-load-more-button'));
        routes.controllers.api.json.CategoryController.getInspirations(cat.id).ajax({data: {lastId: lastId, count: 8}, success: function (advs) {
            if (clear) $('#inspirations-public-list').empty();
            if (advs != null && advs.length > 0) {
                for (var i in advs) {
                    $('#inspirations-public-list').append(tmpl('inspiration-public-template', advs[i]));

                    var insId = advs[i].id;

                    inspirationsMarkers[insId] = new gmaps.Marker({
                        animation: gmaps.Animation.DROP,
                        map: inspirationsMap,
                        position: new gmaps.LatLng(advs[i].lat, advs[i].lng),
                        title: advs[i].name
                    });
                    inspirationsInfos[insId] = new gmaps.InfoWindow({
                        content: '<a href="' + advs[i].link + '"><h3>' + advs[i].name + '</h3></a>'
                    });
                    (function (marker, infowindow) {
                        gmaps.event.addListener(marker, 'click', function () {
                            infowindow.open(inspirationsMap, marker);
                        });
                    })(inspirationsMarkers[insId], inspirationsInfos[insId]);

                    resetInspirationsBounds();
                }

                $('#inspirations-public-list .polaroid').hide().fadeIn().hover(
                    function () {
                        $(this).find("div.overlay").slideDown("fast");
                        inspirationsMarkers[$(this).data('id')].setAnimation(gmaps.Animation.BOUNCE);
                        inspirationsInfos[$(this).data('id')].open(inspirationsMap, inspirationsMarkers[$(this).data('id')]);
                    },
                    function () {
                        $(this).find("div.overlay").slideUp("fast");
                        inspirationsMarkers[$(this).data('id')].setAnimation(gmaps.Animation.DROP);
                        inspirationsInfos[$(this).data('id')].close();
                    }
                );
            } else if (lastId == null) {
                $('#inspirations-public-list').html('No inspirations here.');
                $('#inspirations-places-map').parent().hide(function () {
                    $(this).remove();
                });
            }

            utils.resetReplaceSpinning($('#inspirations-public-load-more-button'));
            if (!advs.length == 8){
                $('#inspirations-public-load-more-button').hide();
            }
            utils.resetSpinning($('#inspirations-public-button-refresh i'));
        }});
    };


    var resetInspirationsBounds = function () {
        gmaps.resetBounds(inspirationsMap, inspirationsMarkers);
    }

    utils.on({
        'click #inspirations-public-button-refresh': function () {
            loadInspirations(null, true);
        },
        'click #inspirations-public-load-more-button': function () {
            loadInspirations($('#inspirations-public-list .polaroid').last().data('id'));
        }
    });


    if (cat.image != null && cat.image != "") {
        $('#background').css('background-image', 'url("http://www.journwe.com/thumbnail?w=1600&u=' + cat.image + '")').addClass('blur');
    } else utils.loadGenericBgImage();

    initialize();

});