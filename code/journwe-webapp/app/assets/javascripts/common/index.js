define([
    "common/utils",
    "routes",
    "messages",
    "common/gmaps"
], function (utils, routes, messages, gmaps) {


    var publicAdventuresMap,
        publicAdventuresMarkers = [],
        publicAdventuresInfos = [];


    var initializePublicAdventures = function (lastId, insId) {
        initializePublicAdventuresMap();
        loadPublicAdventures(lastId, true, insId);
    };

    var initializeMyAdventures = function (lastId) {
        loadMyAdventures(lastId, true);
    };

    var initializeCategories = function (lastId, superCatId) {
        loadCategories(lastId, true, superCatId);
    };

    var renderAdventure = function (tmplId, adv) {
        if(adv.favoriteTime != null && adv.favoriteTime.startDate != null) adv.favoriteTime.startDate = utils.formatDateShort(adv.favoriteTime.startDate);
        if(adv.status != null) adv.cssLabel = utils.adventurerCSSLabel[adv.status];
        return $(tmpl(tmplId, adv));
    };


    var loadMyAdventures = function (lastId, clear) {
        utils.setSpinning($('.btn-adventures-my-refresh i'));
        utils.setReplaceSpinning($('.btn-adventures-my-load-more'));
        routes.controllers.api.json.ApplicationController.getMyAdventures().ajax({data: {lastId: lastId, count: 10}, success: function (advs) {
            if (clear) $('.list-adventures-my').empty();
            for (var i in advs)
                $('.list-adventures-my').append(renderAdventure('adventure-my-template', advs[i]));

            utils.resetReplaceSpinning($('.btn-adventures-my-load-more'));
            if (advs.length < 10)
                $('.btn-adventures-my-load-more').fadeOut();

            utils.resetSpinning($('.btn-adventures-my-refresh i'));
        }});
    };


    var loadPublicAdventures = function (lastId, clear, insId) {
        utils.setSpinning($('.btn-adventures-public-refresh i'));
        utils.setReplaceSpinning($('.btn-adventures-public-load-more'));
        routes.controllers.api.json.ApplicationController.getPublicAdventures().ajax({data: {lastId: lastId, count: 10, inspirationId: insId}, success: function (advs) {
            if (clear) $('#adventures-public-list').empty();
            if (advs != null && advs.length > 0) {
                for (var i in advs) {
                    $('#adventures-public-list').append(renderAdventure('adventure-public-template', advs[i]));

                    publicAdventuresMarkers[advs[i].id] = new google.maps.Marker({
                        animation: google.maps.Animation.DROP,
                        map: publicAdventuresMap,
                        position: new google.maps.LatLng(advs[i].lat, advs[i].lng),
                        title: advs[i].name
                    });
                    publicAdventuresInfos[advId] = new gmaps.InfoWindow({
                        content: '<a href="' + advs[i].link + '"><h3>' + advs[i].name + '</h3></a>'
                    });
                    (function (marker, infowindow) {
                        gmaps.event.addListener(marker, 'click', function () {
                            infowindow.open(adventuresMap, marker);
                        });
                    })(adventuresMarkers[advId], adventuresInfos[advId]);
                    gmaps.event.addListener(publicAdventuresMarkers[advs[i].id], 'click', function () {
                        infowindow.open(publicAdventuresMap, publicAdventuresMarkers[advs[i].id]);
                    });

                    resetPublicAdventuresMapBounds();

                }

                $('#adventures-public-list .polaroid').hide().fadeIn().hover(
                    function () {
                        $(this).find("div.overlay").slideDown("fast");
                        publicAdventuresMarkers[$(this).data('id')].setAnimation(google.maps.Animation.BOUNCE);
                        adventuresInfos[$(this).data('id')].open(adventuresMap, adventuresMarkers[$(this).data('id')]);
                    },
                    function () {
                        $(this).find("div.overlay").slideUp("fast");
                    }
                );

            } else if (lastId == null) {
                $('#adventures-public-list').html('No public JournWes here.');
                $('#adventures-places-map').parent().hide(function () {
                    $(this).remove();
                });
            }
            utils.resetReplaceSpinning($('.btn-adventures-public-load-more'));
            if (advs.length < 10) {
                $('.btn-adventures-public-load-more, #adventures-public-load-more-button').fadeOut();
            } else {
                $('.btn-adventures-public-load-more, #adventures-public-load-more-button').show().html('Show More');
            }

            utils.resetSpinning($('.btn-adventures-public-refresh i'));
        }});
    };

    var loadCategories = function (lastId, clear, superCatId) {
        superCatId = superCatId ? superCatId : 'SUPER';

        utils.setSpinning($('.btn-categories-refresh i'));
        utils.setReplaceSpinning($('.btn-categories-load-more'));
        routes.controllers.api.json.CategoryController.getCategories(superCatId).ajax({data: {lastId: lastId, count: 10}, success: function (cats) {
            if (clear) $('#categories-list').empty();
            for (var i in cats) {
                $('#categories-list').append(tmpl('category-template', cats[i]));
            }

            utils.resetReplaceSpinning($('.btn-categories-load-more'));
            if (cats.length < 10) {
                $('.btn-categories-load-more').fadeOut();
            } else {
                $('.btn-categories-load-more').show().html('Show More');
            }
            utils.resetSpinning($('.btn-categories-refresh i'));
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
        if($('#adventures-places-map').length) publicAdventuresMap = new gmaps.Map(document.getElementById('adventures-places-map'), mapOptions);
    };

    var resetPublicAdventuresMapBounds = function () {
        gmaps.resetBounds(publicAdventuresMap, publicAdventuresMarkers);
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
        'click .btn-adventures-public-refresh': function () {
            loadPublicAdventures(null, true);
        },
        'click .btn-adventures-public-load-more': function () {
            loadPublicAdventures($('#adventures-public-list .polaroid').last().data('id'));
        }
    });

    return {
        initializeMyAdventures: initializeMyAdventures,
        initializePublicAdventures: initializePublicAdventures,
        initializeCategories: initializeCategories
    };

});