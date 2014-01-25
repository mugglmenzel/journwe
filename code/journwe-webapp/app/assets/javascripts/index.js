require([
    "utils",
    "routes",
    "messages",
    "yingyangyong",
    "gmaps"
], function (utils, routes, messages, yingyangyong, gmaps) {


    var publicAdventuresMap,
        publicAdventuresMarkers = [];


    var initialize = function () {

        loadMyAdventures(null, true);

        loadCategories(null, true);

        initializePublicAdventuresMap();
        loadPublicAdventures(null, true);

    };

    var renderAdventure = function (tmplId, adv) {
        adv.favoriteTime.startDate = utils.formatDateShort(adv.favoriteTime.startDate);
        return $(tmpl(tmplId, adv));
    };

    var loadMyAdventures = function (lastId, clear) {
        $('.btn-adventures-my-refresh i').addClass('fa-spin');
        $('.btn-adventures-my-load-more').html('<i class="fa fa-spin icon-journwe"></i>');
        routes.controllers.api.json.ApplicationController.getMyAdventures().ajax({data: {lastId: lastId, count: 10}, success: function (advs) {
            if (clear) $('#adventures-my-list').empty();
            for (var i in advs)
                $('#adventures-my-list').append(renderAdventure('adventure-my-template', advs[i]));

            if (advs.length < 10) {
                $('.btn-adventures-my-load-more').fadeOut();
            } else {
                $('.btn-adventures-my-load-more').show().html('Show More');
            }
            $('.btn-adventures-my-refresh i').removeClass('fa-spin');
        }});
    };


    var loadPublicAdventures = function (lastId, clear) {
        $('.btn-adventures-public-refresh i').addClass('fa-spin');
        $('.btn-adventures-public-load-more').html('<i class="fa fa-spin icon-journwe"></i>');
        routes.controllers.api.json.ApplicationController.getPublicAdventures().ajax({data: {lastId: lastId, count: 10}, success: function (advs) {
            if (clear) $('#adventures-public-list').empty();
            for (var i in advs) {
                $('#adventures-public-list').append(renderAdventure('adventure-public-template', advs[i]));

                publicAdventuresMarkers[advs[i].id] = new google.maps.Marker({
                    animation: google.maps.Animation.DROP,
                    map: publicAdventuresMap,
                    position: new google.maps.LatLng(advs[i].lat, advs[i].lng),
                    title: advs[i].name
                });
                var infowindow = new gmaps.InfoWindow({
                    content: '<a href="' + advs[i].link + '"><h3>' + advs[i].name + '</h3></a>'
                });
                gmaps.event.addListener(publicAdventuresMarkers[advs[i].id], 'click', function () {
                    infowindow.open(publicAdventuresMap, publicAdventuresMarkers[advs[i].id]);
                });

                resetPublicAdventuresMapBounds();

            }

            $('#adventures-public-list .polaroid').hide().fadeIn().hover(
                function () {
                    $(this).find("div.overlay").slideDown("fast");
                },
                function () {
                    $(this).find("div.overlay").slideUp("fast");
                }
            );
            if (advs.length < 10) {
                $('.btn-adventures-public-load-more').fadeOut();
            } else {
                $('.btn-adventures-public-load-more').show().html('Show More');
            }
            $('.btn-adventures-public-refresh i').removeClass('fa-spin');
        }});
    };

    var loadCategories = function (lastId, clear) {
        $('.btn-categories-refresh i').addClass('fa-spin');
        $('.btn-categories-load-more').html('<i class="fa fa-spin icon-journwe"></i>');
        routes.controllers.api.json.ApplicationController.getCategories('SUPER_CATEGORY').ajax({data: {lastId: lastId, count: 10}, success: function (cats) {
            if (clear) $('#categories-list').empty();
            for (var i in cats) {
                $('#categories-list').append(tmpl('category-template', cats[i]));
            }

            if (cats.length < 10) {
                $('.btn-categories-load-more').fadeOut();
            } else {
                $('.btn-categories-load-more').show().html('Show More');
            }
            $('.btn-categories-refresh i').removeClass('fa-spin');
        }});
    };


    //MAP Helpers
    var initializePublicAdventuresMap = function () {
        gmaps.visualRefresh = true;

        var mapOptions = {
            zoom: 5,
            center: new gmaps.LatLng(49.483472, 8.476992),
            mapTypeId: gmaps.MapTypeId.ROADMAP
        };
        publicAdventuresMap = new gmaps.Map(document.getElementById('adventures-places-map'), mapOptions);
    };

    var resetPublicAdventuresMapBounds = function () {
        var bounds = new gmaps.LatLngBounds();
        for (var i in publicAdventuresMarkers) {
            bounds.extend(publicAdventuresMarkers[i].getPosition());
        }
        publicAdventuresMap.fitBounds(bounds);
        if (publicAdventuresMap.getZoom() > 10) publicAdventuresMap.setZoom(10);
        gmaps.event.trigger(publicAdventuresMap, 'resize');
    };


    utils.on({
        'click .btn-adventure-my-refresh': function () {
            loadMyAdventures(null, true);
        },
        'click .btn-adventures-my-load-more': function () {
            loadMyAdventures($('#adventures-my-list .adventure-entry').last().data('id'));
        },
        'click .btn-categories-refresh': function () {
            loadCategories(null, true);
        },
        'click .btn-yingyangyong-start': function () {
            $('#yingyangyong-container').slideDown(function () {
                yingyangyong.resetBounds();
            });
            $('.btn-yingyangyong-start').prop('disabled', true);
        },
        'click .btn-yingyangyong-close': function () {
            $('#yingyangyong-container').slideUp(500);
            $('.btn-yingyangyong-start').prop('disabled', false);
        },
        'click .btn-adventures-public-refresh': function () {
            loadPublicAdventures(null, true);
        },
        'click .btn-adventures-public-load-more': function () {
            loadPublicAdventures($('#adventures-public-list .polaroid').last().data('id'));
        }

    });


    initialize();

});