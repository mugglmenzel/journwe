require([
    "routes",
    "common/utils",
    "common/gmaps",
    "modules/index",
    "inspirationData",
    "modules/main"
], function (routes, utils, gmaps, index, ins) {

    var placeMap,
        layers = {weather: [], photos: []};

    var initialize = function () {
        loadUrls();
        loadTips();
        loadImages();
        initializePlaceMap();

        index.initializePublicAdventuresInspiration(null, ins.id);
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
        if ($('#inspiration-place-map').length) {
            gmaps.visualRefresh = true;

            var mapOptions = {
                zoom: 5,
                center: new gmaps.LatLng(49.483472, 8.476992),
                mapTypeId: gmaps.MapTypeId.ROADMAP
            };

            placeMap = new gmaps.Map(document.getElementById('inspiration-place-map'), mapOptions);

            var placeLatLng = new gmaps.LatLng(ins.placeLatitude, ins.placeLongitude);

            new gmaps.Marker({
                map: placeMap,
                position: placeLatLng
            });
            placeMap.setCenter(placeLatLng);
        }

        initializeMapLayers();
    };

    var initializeMapLayers = function () {

        layers.weather = [new gmaps.weather.WeatherLayer({temperatureUnits: gmaps.weather.TemperatureUnit.CELSIUS}), new gmaps.weather.CloudLayer()];
        layers.photos = [new gmaps.panoramio.PanoramioLayer()];

        //initialize weather
        gmaps.showMapLayers(placeMap, layers.weather);

    };


    utils.on({
        'click .btn-tip-add': function () {
            routes.controllers.api.json.InspirationController.addTip(ins.id).ajax({data: {tip: $('#tip-add-text').val()}, success: function () {
                $('#tip-add-text').val('');
                loadTips();
            }
            });
        },
        'click .btn-inspiration-photo': function () {
            //$('#carousel-inspiration-photos').fadeIn();
            $('#carousel-inspiration-photos').carousel({pause: 'false'});
            $('#carousel-inspiration-photos').carousel($(this).data('index'));
            $('#carousel-inspiration-photos').modal('show');
        },
        'click .carousel-fullscreen .carousel-inner .item.active': function () {
            $('#carousel-inspiration-photos').modal('hide');
        },
        'keydown body': function (e) {
            e.keyCode == 27 && $('#carousel-inspiration-photos').modal('hide');
        },
        'click .btn-place-map-layers-show': function () {
            $.each(layers, function (i, val) {
                gmaps.hideMapLayers(placeMap, val);
            });
            $(this).closest('.btn-group').find('.btn').removeClass('active');
            gmaps.showMapLayers(placeMap, layers[$(this).data('layers')]);
            $(this).addClass('active');
        },
        'focus #tip-add-text' : function () {
            $('#tip-add-text').prop('rows', 3);
        },
        'blur #tip-add-text' : function () {
            $('#tip-add-text').prop('rows', 1);
        }
    });

    if (ins.image != null && ins.image != "") {
        $('#background').css('background-image', 'url("http://www.journwe.com/thumbnail?w=1600&u=' + ins.image + '")').addClass('blur');
    } else utils.loadGenericBgImage();

    initialize();


});