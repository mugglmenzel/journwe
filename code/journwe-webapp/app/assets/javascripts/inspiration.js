require([
    "routes",
    "common/utils",
    "common/gmaps",
    "common/index",
    "inspirationData"
], function (routes, utils, gmaps, index, ins) {

    var placeMap;

    var initialize = function () {
        loadTips();
        loadImages();
        initializePlaceMap();

        index.initializePublicAdventures(null, ins.id);
    };

    var loadTips = function () {
        routes.controllers.api.json.InspirationController.getTips(ins.id).ajax({success: function (tips) {
            $('#inspiration-tips').empty();
            if (tips) {
                for (var i in tips) {
                    tips[i].tip.created = formatTime(tips[i].tip.created);
                    $('#inspiration-tips').append(tmpl('inspiration-tip-template', tips[i]));
                }
            } else $('#inspiration-tips').html('No Tips.');
        }});
    };

    var loadImages = function () {
        routes.controllers.api.json.InspirationController.getImages(ins.id).ajax({success: function (images) {
            if (images) {
                for (var i in images) {
                    $('#inspiration-photos').append(tmpl('inspiration-photo-template', images[i]));
                }
                $('#inspiration-photos .polaroid').last().hide().fadeIn();
            } else $('#inspiration-photos').html('No Photos.');
        }});
    };


    var initializePlaceMap = function () {
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


    utils.on({
        'click #tip-add-button': function () {
            routes.controllers.api.json.InspirationController.addTip(ins.id).ajax({data: {tip: $('#tip-add-text').val()}, success: function () {
                $('#tip-add-text').val('');
                loadTips();
            }
            });
        }
    });

    if (ins.image != null && ins.image != "") {
        $('#background').css('background-image', 'url("http://i.embed.ly/1/image/resize?width=1600&key=2c8ef5b200c6468f9f863bc75c46009f&url=' + ins.image + '")');
    }

    initialize();

});