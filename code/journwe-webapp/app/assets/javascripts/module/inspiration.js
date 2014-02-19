require([
    "routes",
    "common/utils",
    "common/gmaps",
    "common/index",
    "main",
    "inspirationData"
], function (routes, utils, gmaps, index, main, ins) {

    var placeMap;

    var initialize = function () {
        loadUrls();
        loadTips();
        loadImages();
        initializePlaceMap();

        index.initializePublicAdventures(null, ins.id);
    };

    var loadUrls = function () {
        routes.controllers.api.json.InspirationController.getUrls(ins.id).ajax({success: function (urls) {
            $('.list-inspiration-urls').empty();
            if (urls)
                for (var i in urls)
                    $('.list-inspiration-urls').append(tmpl('inspiration-url-template', urls[i]));
        }});
    };

    var loadTips = function () {
        routes.controllers.api.json.InspirationController.getTips(ins.id).ajax({success: function (tips) {
            $('.list-inspiration-tips').empty();
            if (tips) {
                for (var i in tips) {
                    tips[i].tip.created = utils.formatTime(tips[i].tip.created);
                    $('.list-inspiration-tips').append(tmpl('inspiration-tip-template', tips[i]));
                }
            } else $('.list-inspiration-tips').html('No Tips.');
        }});
    };

    var loadImages = function () {
        routes.controllers.api.json.InspirationController.getImages(ins.id).ajax({success: function (images) {
            if (images) {
                for (var i in images) {
                    var image = $.extend({active: i == 0 ? 'active' : ''}, images[i]);
                    $('.inspiration-photos').append(tmpl('inspiration-photo-template', image));
                    $('.carousel-indicators-inspiration-photos').append(tmpl('inspiration-photo-carousel-indicator-template', image));
                    $('.carousel-inner-inspiration-photos').append(tmpl('inspiration-photo-carousel-item-template', image));
                    $('#inspiration-photos .polaroid').last().hide().fadeIn();
                }
            } else $('.inspiration-photos').html('No Photos.');
        }});
    };


    var initializePlaceMap = function () {
        gmaps.visualRefresh = true;

        var mapOptions = {
            zoom: 5,
            center: new gmaps.LatLng(49.483472, 8.476992),
            mapTypeId: gmaps.MapTypeId.ROADMAP
        };
        if($('#inspiration-place-map').length) placeMap = new gmaps.Map(document.getElementById('inspiration-place-map'), mapOptions);

        var placeLatLng = new gmaps.LatLng(ins.placeLatitude, ins.placeLongitude);

        new gmaps.Marker({
            map: placeMap,
            position: placeLatLng
        });
        placeMap.setCenter(placeLatLng);
    }


    utils.on({
        'click #tip-add-button': function () {
            routes.controllers.api.json.InspirationController.addTip(ins.id).ajax({data: {tip: $('#tip-add-text').val()}, success: function () {
                $('#tip-add-text').val('');
                loadTips();
            }
            });
        },
        'click .btn-inspiration-photo': function () {
            $('#carousel-inspiration-photos').fadeIn();
            $('#carousel-inspiration-photos').carousel({pause: 'false'});
            $('#carousel-inspiration-photos').carousel($(this).data('index'));
        },
        'click .carousel-fullscreen .carousel-inner': function () {
            $('#carousel-inspiration-photos').fadeOut();
        }
    });

    if (ins.image != null && ins.image != "") {
        $('#background').css('background-image', 'url("http://i.embed.ly/1/image/resize?width=1600&key=2c8ef5b200c6468f9f863bc75c46009f&url=' + ins.image + '")');
    }

    initialize();

});