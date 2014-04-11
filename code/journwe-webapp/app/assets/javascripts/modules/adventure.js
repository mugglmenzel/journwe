define([
    "common/utils",
    "routes",
    "messages",
    "modules/comments",
    "common/gmaps",
    "adventureData",
    "adventurerData"
], function (utils, routes, messages, comments, gmaps, adv, advr) {

    //Constants
    var now = new Date(),
        today = new Date(now.getFullYear(), now.getMonth(), now.getDate(),
            0, 0, 0, 0);

    var votePlaceIconCSSClassMap = {'YES': 'fa fa-thumbs-up', 'NO': 'fa fa-thumbs-down', 'MAYBE': 'fa fa-question'},
        votePlaceButtonCSSClassMap = {'YES': 'btn-success', 'NO': 'btn-danger', 'MAYBE': 'btn-warning'},
        votePlaceLabelCSSClassMap = {'YES': 'label-success', 'NO': 'label-danger', 'MAYBE': 'label-warning'};
    var voteTimeIconCSSClassMap = {'YES': 'fa fa-thumbs-up', 'NO': 'fa fa-thumbs-down', 'MAYBE': 'fa fa-question'},
        voteTimeButtonCSSClassMap = {'YES': 'btn-success', 'NO': 'btn-danger', 'MAYBE': 'btn-warning'},
        voteTimeLabelCSSClassMap = {'YES': 'label-success', 'NO': 'label-danger', 'MAYBE': 'label-warning'};

    //State Vars
    var visibleSections = typeof advr.visibleSections === 'undefined' || advr.visibleSections == null ? ['discussion', 'adventurers'] : JSON.parse(advr.visibleSections);
    var favoritePlace, favoriteTime;


    //Temp Vars
    var map,
        bgmap,
        markers = [],
        bgMarker,
        layers = {weather: [], photos: []};
    var socialUsers = {};
    var socialUserNames = [],
        _timer;


    // Init bg & scrollspy
    var initBackground = function () {
        if (adv.image != null && adv.image != '') {
            $('#background').css('background-image', 'url("http://www.journwe.com/thumbnail?w=1600&u=' + adv.image + '&t=' + adv.imageTimestamp + '")').addClass('blur');
        } else utils.loadGenericBgImage();
    };


    var loadBgMap = function (lat, lng) {
        var mapOptions = {
            zoom: 13,
            mapTypeId: gmaps.MapTypeId.ROADMAP,
            center: new gmaps.LatLng(lat, lng),
            styles: [
                {
                    "featureType": "all",
                    "elementType": "labels",
                    "stylers": [
                        { "visibility": "off" }

                    ]
                },
                {
                    "featureType": "landscape",
                    "elementType": "geometry",
                    "stylers": [
                        { "hue": "#94ccf9" },
                        { "visibility": "on" },
                        { "saturation": 0 },
                        { "gamma": 5 }
                    ]
                },
                {
                    "stylers": [
                        { "visibility": "simplified" }
                    ]
                },
                {
                    "featureType": "poi",
                    "elementType": "geometry",
                    "stylers": [
                        { "visibility": "simplified" },
                        { "hue": "#94ccf9" },
                        { "weight": 0.1 },
                        { "gamma": 6 }
                    ]
                },
                {
                    "featureType": "water",
                    "elementType": "geometry",
                    "stylers": [
                        { "visibility": "simplified" },
                        { "hue": "#94ccf9" },
                        { "saturation": 50 },
                        { "gamma": 2 }

                    ]
                },
                {
                    "featureType": "road",
                    "elementType": "geometry.fill",
                    "stylers": [
                        { "color": "#cccccc" },
                        { "visibility": "simplified" },
                        { "lightness": 50 },
                        { "gamma": 5 },
                        { "weight": 1.5 }
                    ]
                },
                {
                    "featureType": "transit",
                    "elementType": "geometry.fill",
                    "stylers": [
                        { "color": "#cccccc" },
                        { "visibility": "simplified" },
                        { "lightness": 50 },
                        { "gamma": 2 },
                        { "weight": 1.5 }
                    ]
                },
                {
                    "featureType": "road.local",
                    "stylers": [
                        { "visibility": "off" }
                    ]
                },
                {
                    "featureType": "road",
                    "elementType": "labels",
                    "stylers": [
                        { "visibility": "off" }
                    ]
                },
                {
                    "featureType": "poi",
                    "elementType": "labels",
                    "stylers": [
                        { "visibility": "off" }
                    ]
                },
                {
                    "featureType": "poi.attraction",
                    "elementType": "labels.icon",
                    "stylers": [
                        { "visibility": "on" }
                    ]
                },
                {
                    "featureType": "transit.station",
                    "elementType": "labels",
                    "stylers": [
                        { "visibility": "on" }
                    ]
                },
                {
                    "featureType": "road.highway",
                    "elementType": "geometry",
                    "stylers": [
                        { "color": "#ffffff" },
                        { "visibility": "simplified" },
                        { "lightness": 50},
                        { "weight": 2 },
                        { "gamma": 2 }
                    ]
                }
            ],
            disableDefaultUI: true,
            draggable: false,
            scrollwheel: false,
            panControl: false
        };


        bgmap = new gmaps.Map(document.getElementById('background-map'), mapOptions);
        setBgMapCenterOffset(lat, lng);
    };

    var setBgMapCenterOffset = function (lat, lng) {
        if (bgMarker) bgMarker.setMap(null);

        gmaps.setCenterOffset(
            bgmap,
            new gmaps.LatLng(lat, lng),
                -1 * (Math.round($('#background-map').width() / 2) - 100),
                -1 * (Math.round($('#background-map').height() / 2) - 300)
        );
        bgMarker = new gmaps.Marker({animation: gmaps.Animation.DROP, map: bgmap, position: new gmaps.LatLng(lat, lng)});
    }

    var initScrollspy = function () {
        // $('body').scrollspy({offset: ($('.nav-adventure').first().offset().top + $('.nav-adventure').first().height())});
        //     .on('activate', function (evt) {
        //     $('.row.active').removeClass('active');
        //     $($(evt.target).find('a').attr('href')).parent('.row').addClass('active');
        // });
    };


    //NAVIGATION
    var initNavigation = function () {
        $('.nav-adventure-list a').tooltip();

        $('.nav-adventure').affix({
            offset: {
                top: function () {
                    console.log('returning offset ' + $('.nav-container').first().offset().top + ", window scroll " + $(window).scrollTop());
                    return $('.nav-container').first().offset().top + $('.nav-adventure').height() - $(window).height();
                }
            }
        });


        $.each(visibleSections, function (i, val) {
            var sec = $('.jrn-adventure-section[id=' + val + ']');
            if (sec.length) showNavSection(sec);
        });
    }

    var showNavSection = function (sec) {
        var icon = sec.find('.btn-nav-section-collapse-toggle i'),
            secName = sec.attr('id'),
            container = sec.find('.jrn-container'),
            collapsables = sec.find('.collapsable'),
            li = $('.nav-adventure-list li a[href="#' + secName + '"]').closest('li');

        collapsables.slideDown('100', function () {
            icon.removeClass().addClass('fa fa-caret-up');
            //GMap bugfix
            if ('places' == secName) resetMapBounds();
        });
        container.removeClass('jrn-collapsed');
        li.addClass('active');

        if (visibleSections.indexOf(secName) < 0) {
            visibleSections.push(secName);
            updateVisibleSections();
        }
    };

    var hideNavSection = function (sec) {
        var icon = sec.find('.btn-nav-section-collapse-toggle i'),
            secName = sec.attr('id'),
            container = sec.find('.jrn-container'),
            collapsables = sec.find('.collapsable'),
            li = $('.nav-adventure-list li a[href="#' + secName + '"]').closest('li');

        collapsables.slideUp('100', function () {
            container.addClass('jrn-collapsed');
            icon.removeClass().addClass('fa fa-caret-down');
        });
        li.removeClass('active');

        var i = visibleSections.indexOf(secName);
        if (i > -1) {
            visibleSections.splice(i, 1);
            updateVisibleSections();
        }
    };

    var toggleNavSection = function (sec) {
        var collapsables = sec.find('.collapsable');

        if (collapsables.first().is(':visible')) {
            hideNavSection(sec);
        } else {
            showNavSection(sec);
        }
    };


    var updateVisibleSections = function () {
        routes.controllers.api.json.AdventureController.updateVisibleSections(adv.id).ajax({data: {visibleSections: JSON.stringify(visibleSections)}, success: function (result) {
            visibleSections = JSON.parse(result);
        }});
    };


    //TOOLBAR
    var initPrimeImage = function () {
        if (adv.image != null && adv.image != '') {
            $('#adventure-prime-image').css('background', 'url("http://www.journwe.com/thumbnail?h=200&w=1200&u=' + adv.image + '&t=' + adv.imageTimestamp + '")');
        }
    }

    var processDroppedPrimeImage = function (event) {
        event.stopPropagation();
        event.preventDefault();

        uploadPrimeImage(event.target.files || event.dataTransfer.files);

        return false;
    };

    var uploadPrimeImage = function (files) {
        var btn = $('.btn-prime-image-upload');
        utils.setReplaceSpinning(btn);

        var data = new FormData();
        data.append(files[0].name, files[0])

        routes.controllers.api.json.AdventureController.updateImage(adv.id).ajax({
            data: data,
            cache: false,
            contentType: false,
            processData: false,
            success: function (result) {
                $('#adventure-prime-image').css('background', 'url("http://www.journwe.com/thumbnail?h=200&w=1200&u=' + result.image + '&t=' + result.imageTimestamp + '")');
                utils.resetReplaceSpinning(btn);
            }
        });

    };


    //INDEX
    var initializeIndex = function () {
        loadPhotos(true);

        // Replace all links with html links for certain elements
        $('.replace-links').each(function () {
            $(this).html(utils.replaceURLWithHTMLLinks($(this).html()));
        });
    }

    var loadPhotos = function (clear) {
        utils.resetStash('.loader-photos');
        routes.controllers.api.json.AdventurePhotoController.getPhotos(adv.id).ajax({success: function (images) {
            if (images) {
                if (clear) $('.adventure-photos').empty();
                for (var i in images) {
                    var image = $.extend({active: i == 0 ? 'active' : ''}, images[i]);
                    renderPhoto(image);
                }
            } else $('.adventure-photos').html('No Photos.');
            utils.setStash('.loader-photos');
        }});
    };

    var renderPhoto = function (image) {
        $('.adventure-photos').append(tmpl('adventure-photo-template', image));
        $('.carousel-indicators-adventure-photos').append(tmpl('adventure-photo-carousel-indicator-template', image));
        $('.carousel-inner-adventure-photos').append(tmpl('adventure-photo-carousel-item-template', image));
        $('#adventure-photos .polaroid').last().hide().fadeIn();
    }

    var processDroppedPhoto = function (event) {
        event.stopPropagation();
        event.preventDefault();

        uploadPhoto(event.target.files || event.dataTransfer.files);

        return false;
    };

    var uploadPhoto = function (files) {
        utils.resetStash('.loader-photos');

        var data = new FormData();
        data.append(files[0].name, files[0])

        routes.controllers.api.json.AdventurePhotoController.addPhoto(adv.id).ajax({
            data: data,
            cache: false,
            contentType: false,
            processData: false,
            success: function () {
                loadPhotos(true);
                utils.setStash('.loader-photos');
            }
        });

    };

    var deletePhoto = function (id) {
        utils.resetStash('.loader-photos');
        routes.controllers.api.json.AdventurePhotoController.deletePhoto(adv.id, id).ajax({success: function () {
            loadPhotos(true);
            utils.setStash('.loader-photos');
        }});
    };


    //OPTIONS

    var initializeOptions = function () {
        updateCategorySelection(adv.categoryId);
        loadCategoriesOptionsMap();
        updateTwitterShareButton();
    };

    var loadCategoriesOptionsMap = function () {
        routes.controllers.api.json.CategoryController.categoriesOptionsMap().ajax({success: function (cats) {
            $('.btn-journwe-category ul.dropdown-menu').empty();
            if (cats != null && cats.length > 0)
                for (var i in cats)
                    $('.btn-journwe-category ul.dropdown-menu').append('<li data-id="' + cats[i].id + '"><a>' + cats[i].name + '</a></li>');

        }, complete: function () {
            utils.resetSpinning($('.btn-journwe-category button i'));
        }});
    };

    var updateCategorySelection = function (catId) {
        utils.setSpinning($('.btn-journwe-category button i'));
        routes.controllers.api.json.AdventureController.updateCategory(adv.id).ajax({
            data: {categoryId: catId},
            success: function (data) {
                if (data && data.name != null && data.name.length > 0){
                    $('.btn-journwe-category button span').first().html(data.name);
                }
            }, complete: function () {
                utils.resetSpinning($('.btn-journwe-category button i'));
            }});
    };

    var updateTwitterShareButton = function () {
        var el = $('.options-twitter-share');
        if ($('#adventure-public-switch').find('input:checkbox').is(':checked')) el.show();
        else el.hide();
    };


    //EMAILS

    var initializeEmails = function () {
        loadEmails();
    };

    var loadEmails = function () {
        utils.setSpinning($('.btn-emails-refresh i'));

        routes.controllers.api.json.AdventureEmailController.listEmails(adv.id).ajax({success: function (emails) {
            $('.table-emails tbody').empty();

            for (var i in emails) {
                renderEmail(emails[i]);
            }
            if (emails && emails.length > 0) {
                $('.table-emails').show();
            } else {
                $('.table-emails').hide();
            }

            utils.resetSpinning($('.btn-emails-refresh i'));
        }});
    };


    var renderEmail = function (data, replace) {
        if (data.timestamp != null) data.timestampFormatted = utils.formatDateLong(data.timestamp);

        if (replace)
            replace.replaceWith(tmpl('emails-template', data)).fadeIn();
        else
            $('.table-emails tbody').append(tmpl('emails-template', data));

    };


    //PLACES

    var initializePlaces = function () {
        initializeMap();

        routes.controllers.api.json.AdventurePlaceController.getPlaces(adv.id).ajax({success: function (result) {
            $('#places-list tbody').empty();
            for (var id in result)
                renderPlaceOption(result[id])

            if (result.length)
                $('#places-list').show();
            else
                $('#places-list').hide();

            $('.places-loading').hide();

            updateFavoritePlace();

            updatePlaceVoteOpen(adv.placeVoteOpen);

        }});
    };

    var initializeMap = function () {
        gmaps.visualRefresh = true;
        var mapOptions = {
            zoom: 19,
            minZoom: 2,
            maxZoom: 19,
            center: new gmaps.LatLng(52.467541, 13.324957),
            mapTypeId: gmaps.MapTypeId.ROADMAP
        };
        map = new gmaps.Map(document.getElementById('place-add-map'), mapOptions);
        new gmaps.places.Autocomplete(document.getElementById('place-add-input'));

        initializeMapLayers();
    };

    var initializeMapLayers = function () {

        layers.weather = [new gmaps.weather.WeatherLayer({temperatureUnits: gmaps.weather.TemperatureUnit.CELSIUS}), new gmaps.weather.CloudLayer()];
        layers.photos = [new gmaps.panoramio.PanoramioLayer()];

        //initialize weather
        gmaps.showMapLayers(map, layers.weather);

    };

    var resetMapBounds = function () {
        if (typeof map === 'undefined') initializeMap();
        gmaps.resetBounds(map, markers);
    };

    var removeMapMarker = function (id) {
        markers[id].setMap(null);
        delete markers[id];
        resetMapBounds();
    };

    var renderPlaceOption = function (data, replace) {
        data.voteGroup = Math.round(data.voteGroup * 5 * 100) / 100;
        var place = $(tmpl('place-template', $.extend({
            votePlaceLabelCSSClassMap: votePlaceLabelCSSClassMap
        }, data)));

        if (replace)
            replace.replaceWith(place).fadeIn();
        else
            $('#places-list tbody').append(place).fadeIn();

        var marker = new gmaps.Marker({animation: gmaps.Animation.DROP, map: map, position: new gmaps.LatLng(data.lat, data.lng), title: data.address});
        markers[data.placeId] = marker;
        map.setCenter(new gmaps.LatLng(data.lat, data.lng));
        resetMapBounds();

        $('#placeoption-status-icon-' + data.placeId).addClass(votePlaceIconCSSClassMap[data.vote]);
        $('#placeoption-status-' + data.placeId).addClass(votePlaceButtonCSSClassMap[data.vote]);
    };


    var updatePlaceVoteOpen = function (open) {

        if (open) {
            var address = favoritePlace && favoritePlace.address.split(",")[0];
            $(".btn-set-close-place").removeClass('btn-default').addClass("btn-success").html('<i class="fa fa-ok"></i> Close' + (address ? ' with ' + address : ''));
            $('#places-list button, .btn-set-reminder-place').prop('disabled', false);
            $('#place-add-form').fadeIn();
        } else {
            $(".btn-set-close-place").removeClass("btn-success").addClass('btn-default').html('Reopen');
            $('#places-list button, .btn-set-reminder-place').prop('disabled', true);
            $('#place-add-form').fadeOut();
        }

    };

    var updateFavoritePlace = function () {

        utils.setSpinning($('.icon-favorite-place'));

        routes.controllers.api.json.AdventurePlaceController.getFavoritePlace(adv.id).ajax({success: function (result) {
            favoritePlace = result.favorite;
            if (result.favorite != null) {
                $('.places-favorite-place-name').html(result.favorite.address);
                loadBgMap(result.favorite.lat, result.favorite.lng);
            } else initBackground();
            if (result.autoFavorite != null) $('.places-autofavorite-place-name').html(result.autoFavorite.address);
            $('.btn-close-place').toggle(!!result.favorite);
            utils.resetSpinning($('.icon-favorite-place'));
        }});
    };

    var setFavoritePlace = function (placeID, el) {

        utils.setSpinning(el.find('i'));
        utils.setSpinning($('.icon-favorite-place'));

        routes.controllers.api.json.AdventurePlaceController.setFavoritePlace(adv.id).ajax({
            data: {favoritePlaceId: placeID},
            success: function (data) {
                favoritePlace = data;
                $('.places-favorite-place-name').html(data.address);
                if(data.lat && data.lng) setBgMapCenterOffset(data.lat, data.lng);
                utils.resetSpinning($('.icon-favorite-place'));

                utils.resetSpinning($(el).find('i'));
                el.closest('table').find('td:first-child .btn-success').removeClass('btn-success');
                $(el).addClass('btn-success');

                updatePlaceVoteOpen($(".btn-set-close-place").is(".btn-success"));
            }
        });
    };

    var votePlace = function (vote, voteGrav, optId) {
        routes.controllers.api.json.AdventurePlaceController.vote(adv.id, optId).ajax({
            data: {
                vote: vote, voteGravity: voteGrav
            },
            success: function (res) {
                renderPlaceOption(res, $('#placeoption-item-' + res.placeId));
                updateFavoritePlace();
            }});
    };


    var deletePlace = function (optId, el) {
        utils.setReplaceSpinning(el);
        routes.controllers.api.json.AdventurePlaceController.deletePlace(adv.id, optId).ajax({success: function () {
            removeMapMarker(optId);
            $('#placeoption-item-' + optId).fadeOut(function () {
                $('#placeoption-item-' + optId).remove();
            });
            updateFavoritePlace();
        }});
    };


    //PEOPLE

    var initializePeople = function () {
        loadAllAdventurers();
        //loadParticipants();
        //loadInvitees();
        //loadApplicants();

        friendTypeahead();
    };

    var loadPublicAdventurers = function () {
        loadAdventurers(routes.controllers.api.json.AdventurePeopleController.getAdventurers(adv.id), '.adventurers-list');
    }

    var loadAllAdventurers = function (clear) {
        if (clear) $('.adventurers-list').empty();
        loadParticipants();
        loadInvitees();
        loadApplicants();
    };

    var loadParticipants = function (clear) {
        loadAdventurers(routes.controllers.api.json.AdventurePeopleController.getParticipants(adv.id), '.adventurers-list', clear);
    };

    var loadInvitees = function (clear) {
        loadAdventurers(routes.controllers.api.json.AdventurePeopleController.getInvitees(adv.id), '.adventurers-list', clear);
    };

    var loadApplicants = function (clear) {
        loadAdventurers(routes.controllers.api.json.AdventurePeopleController.getApplicants(adv.id), '.adventurers-list', clear);
    };

    var loadAdventurers = function (endpoint, target, clear, template, hideOnEmpty) {
        template = template ? template : 'adventurer-template';
        endpoint.ajax({success: function (advs) {
            if (clear) $(target).empty();
            if (advs != null && advs.length > 0) {
                for (var i in advs) {
                    advs[i].cssLabel = utils.adventurerCSSLabel[advs[i].status];
                    advs[i].color = utils.colorOfUser(advs[i].name);
                    $(target).append(tmpl(template, advs[i]));
                }
                $(target).parent().show();
            } else if (hideOnEmpty) $(target).parent().hide();
        }});
    };

    var addFriend = function () {
        utils.setReplaceSpinning($('.btn-people-add'));
        var data = {
            socialId: getSocialIdByType($('#people-add-provider-icon').data('social-provider')),
            provider: $('#people-add-provider-icon').data('social-provider')
        };
        routes.controllers.api.json.AdventurePeopleController.invite(adv.id).ajax({data: data, success: function () {
            $('#people-add-input').val('');
            loadAllAdventurers(true);
            utils.resetReplaceSpinning($('.btn-people-add'));
        }});
    };

    var getSocialIdByType = function (type) {
        if (type === 'email') return $('#people-add-input').val();
        else return socialUsers[$('#people-add-input').val()]
    };

    var friendTypeahead = function () {
        if ($('#people-add-provider-icon').data('typeahead') == "off") $('.input-people-add').typeahead('destroy');
        else {
            var provider = $('#people-add-provider-icon').data('social-provider');

            if (provider != null && provider != 'email') {
                $('.input-people-add').attr('type', 'text');
                $('.input-people-add').typeahead({highlight: true, minLength: 3},
                    {
                    name: 'people-typeahead',
                    displayKey: 'name',
                    source: function(query, callback){
                        routes.controllers.api.json.AdventurePeopleController.autocomplete().ajax({data: {provider: provider, input: query},
                        success: function (data) {
                            socialUsers = {};
                            socialUserNames = [];
                            $.each(data, function (ix, item) {
                                if ($.inArray(item.name, socialUserNames) > -1) {
                                    item.nameId = item.name + ' #' + item.id;
                                } else item.nameId = item.name

                                socialUserNames.push({value: item.nameId, name: item.name, tokens: [item.id, item.name]});
                                socialUsers[item.nameId] = item.id;
                            });

                            callback(socialUserNames);
                        }})
                    }
                });
            } else {
                $('.input-people-add').attr('type', 'email');
                $('.input-people-add').typeahead('destroy');
            }
        }
    };


    var changeAdventurerStatus = function (el) {
        utils.setReplaceSpinning(el);

        routes.controllers.api.json.AdventurePeopleController.participateStatus(adv.id, el.data('status')).ajax({success: function (json) {
            var state = json.participationStatus;

            utils.resetReplaceSpinning(el);

            el.parent().find('.active').removeClass('active');
            el.addClass('active');

            $('#adventurers-adventurer-' + json.userId)
                .find('.label').removeClass().addClass('label label-' + utils.adventurerCSSLabel[state])
                .html(state);
        }});
        return false;
    };

    var adoptAdventurer = function (el) {
        utils.setReplaceSpinning(el);

        routes.controllers.api.json.AdventurePeopleController.adopt(adv.id, el.data('id')).ajax({success: function (json) {
            var state = json.participationStatus;

            loadApplicants();
            loadParticipants();

            utils.resetReplaceSpinning(el);
        }});
        return false;
    };

    var denyAdventurer = function (el) {
        var html = el.html();

        el.css({width: el.width() + "px"})
            .html('<i class="fa fa-spin icon-journwe"></i>');

        routes.controllers.api.json.AdventurePeopleController.deny(adv.id, el.data('id')).ajax({success: function (json) {
            loadInvitees();
            loadApplicants();
        }});
        return false;
    };


    //TIME

    var initializeTime = function () {
        // Init all date fields
        $('.date').datepicker({
            startDate: today,
            weekStart: 1
        });

        $("#time-add-input-start").datepicker({startDate: new Date()}).on('changeDate', function (e) {
            $("#time-add-input-end").datepicker('setStartDate', e.date);
            $("#time-add-input-start").datepicker('hide');
        });
        $("#time-add-input-end").datepicker({startDate: new Date()}).on('changeDate', function (e) {
            $("#time-add-input-end").datepicker('hide');
        });

        routes.controllers.api.json.AdventureTimeController.getTimes(adv.id).ajax({success: function (result) {
            $('#times-list tbody').empty();
            for (var id in result) {
                renderTimeOption(result[id])
            }
            if (result.length) {
                $('#times-list').show();
            } else {
                $('#times-list').hide();
            }

            $('.times-loading').hide();

            updateFavoriteTime();

            updateTimeVoteOpen(adv.timeVoteOpen);
        }});
    };


    var renderTimeOption = function (data, replace) {
        data.voteGroup = Math.round(data.voteGroup * 5 * 100) / 100;
        var time = $(tmpl('time-template', $.extend({
            voteTimeLabelCSSClassMap: voteTimeLabelCSSClassMap,
            formatDate: utils.formatDate
        }, data)));

        if (replace) {
            replace.replaceWith(time).fadeIn();
        } else {
            var el;
            if ("number" == typeof data.index && data.index >= 0) {
                el = $('#times-list tbody tr').get(data.index - 1);
            }

            if (data.index === 0) {
                $('#times-list tbody').prepend(time);
            } else if (el) {
                $(el).after(time);
            } else {
                $('#times-list tbody').append(time);
            }
        }

        $('#timeoption-status-icon-' + data.timeId).addClass(voteTimeIconCSSClassMap[data.vote]);
        $('#timeoption-status-' + data.timeId).addClass(voteTimeButtonCSSClassMap[data.vote]);
    };


    var updateTimeVoteOpen = function (data) {
        if (data) {
            var time = favoriteTime && (utils.formatDateShort(favoriteTime.startDate) + '-' + utils.formatDateShort(favoriteTime.endDate));
            $(".btn-set-close-time").addClass("btn-success").removeClass('btn-default').html('<i class="fa fa-ok"></i> Close' + (time ? ' with ' + time : ''));
            $('#times-list button').prop('disabled', false);
            $('#time-add-form').fadeIn();
        } else {
            $(".btn-set-close-time").removeClass("btn-success").addClass('btn-default').html('Reopen');
            $('#times-list button').prop('disabled', true);
            $('#time-add-form').fadeOut();
        }
    };

    var updateFavoriteTime = function () {

        utils.setSpinning($('.icon-favorite-time'));

        routes.controllers.api.json.AdventureTimeController.getFavoriteTime(adv.id).ajax({success: function (result) {
            favoriteTime = result.favorite;
            if (result.favorite != null) $('.times-favorite-time-name').html(utils.formatDate(result.favorite.startDate) + (result.favorite.startDate != result.favorite.endDate ? " - " + utils.formatDate(result.favorite.endDate) : ""));
            if (result.autoFavorite != null)$('#times-autofavorite-time-name').html(utils.formatDate(result.autoFavorite.startDate) + (result.favorite.startDate != result.favorite.endDate ? " - " + utils.formatDate(result.autoFavorite.endDate) : ""));
            $('.btn-close-time').toggle(!!result.favorite);
            utils.resetSpinning($('.icon-favorite-time'));
        }});
    };


    var setFavoriteTime = function (timeID, el) {
        utils.setSpinning($('.icon-favorite-time'));
        utils.setSpinning($(el).find('i'));
        routes.controllers.api.json.AdventureTimeController.setFavoriteTime(adv.id).ajax({data: {favoriteTimeId: timeID}, success: function (data) {
            favoriteTime = data;
            $('.times-favorite-time-name').html(utils.formatDate(data.startDate) + " - " + utils.formatDate(data.endDate));
            utils.resetSpinning($('.icon-favorite-time'));

            el.closest('table').find('td:first-child .btn-success').removeClass('btn-success');
            $(el).addClass('btn-success');
            utils.resetSpinning($(el).find('i'));

            $('.btn-close-time').show();
        }});
    };


    var voteTime = function (vote, voteGrav, optId) {
        $('#timeoption-item-' + optId + ' .dropdown-toggle')
            .html('<i class="fa fa-spin icon-journwe"></i>');
        routes.controllers.api.json.AdventureTimeController.vote(adv.id, optId).ajax({
            data: {
                vote: vote, voteGravity: voteGrav
            },
            success: function (res) {
                renderTimeOption(res, $('#timeoption-item-' + res.timeId));
                updateFavoriteTime();
                $('#timeoption-item-' + res.timeId + ' .dropdown-toggle')
                    .html('<i class="fa fa-pencil"></i>');
            }
        });
    };

    var deleteTime = function (optId, el) {
        $(el).html('<i class="fa fa-spin icon-journwe"></i>');
        routes.controllers.api.json.AdventureTimeController.deleteTime(adv.id, optId).ajax({success: function () {
            $('#timeoption-item-' + optId).fadeOut(function () {
                $('#timeoption-item-' + optId).remove();
            });
        }
        });
    };

    //TIMELINE
    var initializeTimeline = function () {
        $('.nav-discussion .btn').tooltip();
        loadTimelineAll();
    };

    var loadTimelineAll = function (el) {
        loadTimeline(routes.controllers.api.json.AdventureTimelineController.get(adv.id), el);
    };

    var loadTimelineLogs = function (el) {
        loadTimeline(routes.controllers.api.json.AdventureTimelineController.getLogs(adv.id), el);
    };

    var loadTimelineEmails = function (el) {
        loadTimeline(routes.controllers.api.json.AdventureTimelineController.getEmails(adv.id), el);
    };

    var loadTimelineComments = function (el) {
        loadTimeline(routes.controllers.api.json.AdventureTimelineController.getComments(adv.id), el);
    };

    var loadTimelineFiles = function (el) {
        loadTimeline(routes.controllers.api.json.AdventureTimelineController.getFiles(adv.id), el);
    };

    var loadTimeline = function (route, el) {
        // if (el){
        //     el.parent().find(".active").removeClass("active");
        //     el.addClass("active");
        // }

        utils.setSpinning($(".btn-timeline-refresh i"));
        $('.timeline').parent().fadeTo(300, 0.5);

        route.ajax({success: function (result) {

            var t = $('.timeline').empty();

            $.each(result, function (i, res) {
                t.append(renderTimeline(res));
            });

            t.parent().fadeTo(300, 1);
            utils.resetSpinning($(".btn-timeline-refresh i"));
        }});
    };

    var renderTimeline = function (time) {
        var type = time.type,
            data = $.extend({
                time: utils.formatTime(time.timestamp),
                fulltime: utils.formatDateLong(time.timestamp)
            }, time);

        if ("log" == type) {
            if ("PLACE_VOTE_OPEN" == time.log.topic) {
                data.message = time.log.data == "false" ? "Closed place vote." : "Reopened place vote.";
            } else if ("TIME_VOTE_OPEN" == time.log.topic) {
                data.message = time.log.data == "false" ? "Closed time vote." : "Reopened time vote.";
            } else if ("DESCRIPTION_CHANGE" == time.log.topic) {
                data.message = time.log.data ? time.log.data : "Description removed.";
                data.info = "Changed description";
            }

            if (data.message) {
                type = "info";
            }
        }

        return $('<div></div>').html(tmpl('timeline-template-' + type, data));
    };


    //DEADLINE

    var deadline = function (btn, date, route) {

        // Set spinner/hide calendar
        btn.find("i").attr("class", "fa fa-spin icon-journwe");
        btn.datepicker("hide");

        // Do ajax call
        route(adv.id).ajax({
            data: {voteDeadline: date.getTime()},
            success: function (data) {

                // Set response
                var d = date,
                    z = function (r) {
                        return r < 10 ? "0" + r : r;
                    },
                    f = (z(d.getDate()) + "-" + z(d.getMonth() + 1) + "-" + (d.getYear() + 1900));
                btn.html('<i class="fa fa-calendar"></i> ' + f);
            }
        });
    };


    //TODOS

    var initializeTodos = function () {
        loadUserTodos();
        loadOtherAdventurers();
        var firstAdvr = $('#todos-adventurers-selection div').first().data('id');
        if (firstAdvr != null) loadAdventurerTodos(firstAdvr);
    };

    var loadUserTodos = function () {
        loadTodos(advr.userId, '#todos-list', 'todo-template');
    };

    var loadOtherAdventurers = function () {
        loadAdventurers(routes.controllers.api.json.AdventurePeopleController.getOtherParticipants(adv.id), '#todos-adventurers-selection', true, 'todos-adventurer-template');
    }

    var loadAdventurerTodos = function (advrId) {
        $('#todos-adventurers-selection > div').removeClass("polaroid-active");
        $('#todos-adventurer-' + advrId + '').addClass("polaroid-active");
        loadTodos(advrId, '#todos-adventurers-list', 'todo-adventurer-template');
    };

    var loadTodos = function (userId, target, template) {
        utils.setSpinning($('#todos-button-refresh i'));
        routes.controllers.api.json.AdventureTodoController.getTodos(adv.id, userId).ajax({success: function (results) {
            $(target + ' tbody').empty();

            for (var todo in results)
                renderTodo(results[todo], target, template);

            if (results.length)
                $(target).show();
            else
                $(target).hide();

            utils.resetSpinning($('#todos-button-refresh i'));
        }});
    };

    var renderTodo = function (data, target, template, replace) {
        if (replace)
            replace.replaceWith(tmpl(template, data)).fadeIn();
        else
            $(target + ' tbody').append(tmpl(template, data));
    };


    //FILES

    var initializeFiles = function () {
        loadFiles();
    };

    var uploadFiles = function (files, target) {
        var btn = $('.files-upload-dropzone .btn-upload');
        utils.setReplaceSpinning(btn);

        target.addClass("uploading");

        for (var i = 0; i < files.length; i++) {
            var data = new FormData();
            data.append('uploadFile', files[i]);
            data.append('fileName', files[i].name);

            routes.controllers.api.json.AdventureFileController.uploadFile(adv.id).ajax({
                data: data,
                cache: false,
                contentType: false,
                processData: false,
                xhr: function () {
                    var xhr = $.ajaxSettings.xhr();
                    if (xhr.upload) {
                        $(xhr.upload).bind('progress', function (e) {
                            var oe = e.originalEvent;
                            // Make sure the progress event properties get copied over:
                            e.lengthComputable = oe.lengthComputable;
                            e.loaded = oe.loaded;
                            e.total = oe.total;
                            if (e.lengthComputable) {
                                var percent = Math.floor(e.loaded / e.total * 100);
                                $('#upload-progress').modal('show');
                                $('#upload-progress .progress-bar').css('width', percent + '%').html(percent + '%');
                            }
                        });
                    }
                    return xhr;
                },
                success: function (result) {
                    utils.resetReplaceSpinning(btn);
                    target.removeClass("uploading");
                    $('#upload-progress').modal('hide');

                    if (target.closest('.file-upload-timeline').length) {
                        result.type = "file";
                        $('.timeline').prepend(renderTimeline(result));
                    } else {
                        loadFiles();
                    }
                }
            });
        }
    };

    var loadFiles = function () {
        //$('.files-loading').show();
        utils.setSpinning($('.btn-files-refresh i'));

        routes.controllers.api.json.AdventureFileController.listFiles(adv.id).ajax({success: function (files) {
            $('.table-files tbody').empty();

            for (var fileName in files) {
                renderFile(files[fileName]);
            }
            if (files && files.length > 0) {
                $('.table-files').show();
            } else {
                $('.table-files').hide();
            }

            //$('.files-loading').hide();
            utils.resetSpinning($('.btn-files-refresh i'));
        }});
    };


    var renderFile = function (data, replace) {
        if (replace)
            replace.replaceWith(tmpl('files-template', data)).fadeIn();
        else
            $('.table-files tbody').append(tmpl('files-template', data));

    };

    var deleteFile = function (fileName, el) {

        utils.setReplaceSpinning(el);

        routes.controllers.api.json.AdventureFileController.deleteFile(adv.id, fileName).ajax({
            success: function () {
                utils.resetReplaceSpinning(el);

                var tr = $(el).closest('tr');
                if (tr.length)
                    tr.fadeOut(function () {
                        tr.remove();
                    });

                var te = $(el).closest('.timeline-entry');
                if (te.length)
                    te.fadeOut(function () {
                        te.remove();
                    });
            }
        });
    };


    //LISTENER

    utils.on({
        // Click on navigation to scroll to it
        'click .nav-adventure-list a': function (e) {

            var section = $(this).attr('href');

            if ($(section).length) {
                var sec = $(section).closest('.jrn-adventure-section'),
                    collapsables = sec.find('.collapsable');

                if (e.altKey)
                    hideNavSection(sec);
                else {
                    if (!collapsables.first().is(':visible'))
                        showNavSection(sec);
                    $('html, body').animate({
                        scrollTop: $(section).offset().top - 150
                    }, 'slow');
                }

            }

            return false;
        },
        'click .btn-nav-section-collapse-toggle': function () {
            toggleNavSection($(this).closest('.jrn-adventure-section'));
        },

        'click .btn-participate': function () {
            var el = $(this),
                html = el.html();

            el.css({width: el.width() + "px"})
                .html('<i class="fa fa-spin icon-journwe"></i>');

            return true;
        },
        'drop .file-dropzone': function (event) {
            processDroppedPrimeImage(event);
        },
        'drop #adventure-prime-image': function (event) {
            processDroppedPrimeImage(event);
        },
        'change #adventure-prime-image-file-input': function () {
            var inputFile = $('#adventure-prime-image-file-input'),
                files = inputFile[0].files;
            if (files) {
                uploadPrimeImage(files);
                inputFile.val('');
            }
        },
        'click .btn-prime-image-upload': function () {
            $('#adventure-prime-image-file-input').click();
        },

        'click .btn-edit-description': function (e) {
            e.stopPropagation();

            $('.editable[data-name=adventureDescription]').editable('show');
        },

        'click .btn-adventure-photo': function () {
            $('#carousel-adventure-photos').carousel({pause: 'false'});
            $('#carousel-adventure-photos').carousel($(this).data('index'));
            $('#carousel-adventure-photos').modal('show');
        },
        'click .carousel-fullscreen .carousel-inner .item.active': function () {
            $('#carousel-adventure-photos').modal('hide');
        },
        'drop .adventure-prime-image': function (event) {
            processDroppedPhoto(event);
        },
        'change #adventure-photo-file-input': function () {
            var inputFile = $('#adventure-photo-file-input'),
                files = inputFile[0].files;
            if (files) {
                uploadPhoto(files);
                inputFile.val('');
            }
        },
        'click .btn-photo-upload': function () {
            $('#adventure-photo-file-input').click();
        },
        'click .btn-photo-delete': function () {
            deletePhoto($(this).data('id'));
        },

        'change #adventure-public-switch': function () {
            var el = $(this);
            routes.controllers.api.json.AdventureController.updatePublic(adv.id).ajax({
                data: {publish: el.find('input:checkbox').prop('checked')},
                success: function (pub) {
                    el.find('input:checkbox').prop('checked', pub);
                    updateTwitterShareButton();
                }});
        },
        'click .btn-journwe-category li': function () {
            updateCategorySelection($(this).data('id'));
        },


        'click .btn-emails-refresh': function () {
            loadEmails();
        },

        'click .btn-timeline-refresh': function () {
            initializeTimeline();
        },
        'click .btn-timeline-filter-all': function () {
            loadTimelineAll($(this));
        },
        'click .btn-timeline-filter-logs': function () {
            loadTimelineLogs($(this));
        },
        'click .btn-timeline-filter-emails': function () {
            loadTimelineEmails($(this));
        },
        'click .btn-timeline-filter-comments': function () {
            loadTimelineComments($(this));
        },
        'click .btn-timeline-filter-files': function () {
            loadTimelineFiles($(this));
        },

        'keydown #place-add-input': function (e) {
            if (e.keyCode == 13) {
                $('#place-add-button').click();
            }
        },
        'keydown #place-add-comment-input': function (e) {
            if (e.keyCode == 13) {
                $('#place-add-button').click();
            }
        },
        'focus #place-add-input': function (e) {
            $(this).css("width", "50%");
            $('#place-add-comment-input').fadeIn();
        },
        'blur #place-add-input': function (e) {
            if ($(this).val() == '') {
                var el = $(this);
                $('#place-add-comment-input').fadeOut(function () {
                    el.css("width", "100%");
                });
            }
        },
        'click #place-add-button': function () {
            if ($('#place-add-input').val() != null && $('#place-add-input').val() != '') {
                var el = $(this);
                utils.setReplaceSpinning(el);
                new gmaps.Geocoder().geocode({'address': $('#place-add-input').val()}, function (results, status) {
                    routes.controllers.api.json.AdventurePlaceController.addPlace(adv.id).ajax({data: { address: results[0].formatted_address, lat: results[0].geometry.location.lat(), lng: results[0].geometry.location.lng(), comment: $('#place-add-comment-input').val()}, success: function (res) {
                        renderPlaceOption(res);
                        $('#place-add-input').val("");
                        $('#place-add-comment-input').val("");
                        utils.resetReplaceSpinning(el);
                        $('#places-list').show();
                    }});
                });
            } else {
                $('#place-add-input').focus();
                return false;
            }
        },

        'click .rating-place :radio': function (e) {
            var vote = 'MAYBE';
            switch ($(e.target).val()) {
                case '1':
                case '2':
                    vote = 'NO';
                    break;
                case '4':
                case '5':
                    vote = 'YES';
            }
            ;
            if ($(e.target).is(':checked')) votePlace(vote, $(e.target).val() / 5, $(e.target).data('id'));
        },
        'change #places-voting-active-switch': function () {
            routes.controllers.api.json.AdventureController.updatePlaceVoteOpen(adv.id).ajax({data: {voteOpen: $('#places-voting-active-switch').prop('checked')}, success: updatePlaceVoteOpen});
        },


        'changeDate .btn-set-reminder-place': function (e) {
            deadline(
                $(this),
                e.date,
                routes.controllers.api.json.AdventureController.updatePlaceVoteDeadline
            );
        },


        'click .btn-favorite-place': function () {
            setFavoritePlace($(this).closest('tr').data('placeid'), $(this));
        },
        'click .btn-delete-place': function () {
            deletePlace($(this).closest('tr').data('placeid'), $(this));
        },

        'click .btn-set-close-place': function () {

            var btn = $(this),
                open = btn.is(".btn-success");

            utils.setReplaceSpinning(btn);

            routes.controllers.api.json.AdventureController.updatePlaceVoteOpen(adv.id).ajax({
                data: {voteOpen: !open},
                success: function (data) {
                    utils.resetReplaceSpinning(btn);
                    updatePlaceVoteOpen(data);
                }
            });
        },
        'click .btn-places-map-layers-show': function () {
            $.each(layers, function (i, val) {
                gmaps.hideMapLayers(map, val);
            });
            $(this).closest('.btn-group').find('.btn').removeClass('active');
            gmaps.showMapLayers(map, layers[$(this).data('layers')]);
            $(this).addClass('active');
        },


        'click #people-add-provider .dropdown-menu a': function () {
            $('#people-add-provider-icon').attr('class', $(this).data('icon'));
            $('#people-add-provider-icon').data('social-provider', $(this).data('social-provider'));
            $('#people-add-provider-icon').data('typeahead', $(this).data('typeahead'));
            $('#people-add-input').attr('type', $(this).data('input-type'));
            friendTypeahead();
            $('#people-add-input').focus();
        },
        'click .btn-select-adventurer button': function () {
            return changeAdventurerStatus($(this));
        },
        'click .btn-adopt': function () {
            return adoptAdventurer($(this));
        },
        'click .btn-deny': function () {
            return denyAdventurer($(this));
        },
        'click .btn-people-add': function () {
            addFriend();
        },
        'keypress #people-add-input': function () {
            if (event.which == 13 || event.keyCode === 13) {
                addFriend();
                event.preventDefault();
                return false;
            } else return true;
        },

        'click .btn-favorite-time': function () {
            setFavoriteTime($(this).parents("tr").data('timeid'), $(this));
        },
        'click .btn-delete-time': function () {
            deleteTime($(this).closest('tr').data('timeid'), $(this));
        },

        'click #time-add-button': function () {

            var start = $('#time-add-form input[name=startDate]'),
                end = $('#time-add-form input[name=endDate]');

            if (!start.val()) {
                start.focus();
                return false;
            }
            if (!end.val()) {
                end = start;
            }
            var el = $(this);

            utils.setReplaceSpinning(el);
            routes.controllers.api.json.AdventureTimeController.addTime(adv.id).ajax({
                data: { startDate: start.val(), endDate: end.val()},
                success: function (res) {
                    renderTimeOption(res);
                    $('#time-add-form input[name=name]').val("");
                    $('#time-add-form input[name=startDate]').val("");
                    $('#time-add-form input[name=endDate]').val("");
                    utils.resetReplaceSpinning(el);
                    $('#times-list').show();
                }});
        },
        'click .rating-time :radio': function (e) {
            var vote = 'MAYBE';
            switch ($(e.target).val()) {
                case '1':
                case '2':
                    vote = 'NO';
                    break;
                case '5':
                case '6':
                    vote = 'YES';
            }
            ;

            if ($(e.target).is(':checked')) voteTime(vote, $(e.target).val() / 5, $(e.target).data('id'));
        },
        'change #times-voting-active-switch': function () {
            routes.controllers.api.json.AdventureController.updateTimeVoteOpen(adv.id).ajax({data: {voteOpen: $('#times-voting-active-switch').prop('checked')}, success: updateTimeVoteOpen});
        },
        'changeDate .btn-set-reminder-time': function (e) {
            deadline(
                $(this),
                e.date,
                routes.controllers.api.json.AdventureController.updateTimeVoteDeadline
            );
        },

        'click .btn-set-close-time': function () {

            var btn = $(this),
                open = btn.is(".btn-success");

            utils.setReplaceSpinning(btn);

            routes.controllers.api.json.AdventureController.updateTimeVoteOpen(adv.id).ajax({
                data: {voteOpen: !open},
                success: function (data) {
                    utils.resetReplaceSpinning(btn);
                    updateTimeVoteOpen(data);
                }
            });
        },


        'click #todos-button-refresh': function () {
            loadUserTodos();
        },

        'click .todos-adventurer': function () {
            loadAdventurerTodos($(this).closest('div').data('id'));
        },

        'click #todos-list tbody .btn-check': function () {

            var input = $(this),
                tr = input.closest('.todo-entry'),
                id = tr.data('id'),
                i = tr.find('.btn-check i'),
                complete = i.is('.fa-square-o');

            utils.setReplaceSpinning(i);

            routes.controllers.api.json.AdventureTodoController.setTodo(adv.id, id).ajax({
                data: {
                    status: complete ? 'COMPLETE' : 'NEW'
                },
                success: function (res) {
                    tr.get(0).className = 'status-' + res.status + ' todo-entry';
                    utils.resetReplaceSpinning(i);
                    i.removeClass().addClass('fa ' + (res.status == 'COMPLETE' ? 'fa-check-square-o' : 'fa-square-o'));
                }
            });

            return false;
        },
        'click #todos-adventurers-list tbody .btn-copy': function (e) {
            e.preventDefault();

            var btn = $(this),
                tr = btn.closest('.todo-entry'),
                td = tr.find('td'),
                title = $(td[1]).text();

            utils.setReplaceSpinning(btn);

            routes.controllers.api.json.AdventureTodoController.addTodo(adv.id).ajax({data: {title: title}, success: function (res) {
                loadUserTodos();
                utils.resetReplaceSpinning(btn);
            }});
        },
        'keydown #todo-title': function (e) {
            e.keyCode == 13 && $('#btn-add-todo').click();
        },

        'click #btn-add-todo': function () {
            utils.setReplaceSpinning($(this));
            var btn = $(this),
                fld = $('#todo-title');

            routes.controllers.api.json.AdventureTodoController.addTodo(adv.id).ajax({data: { title: fld.val() }, success: function (res) {
                //$('.table-my-todos  tbody').append($(tmpl('todo-template', res).trim()).fadeIn());
                //activateUserButtonListener();
                loadUserTodos();
                fld.val("");
                utils.resetReplaceSpinning($('#btn-add-todo'));
            }});
        },
        'click #todos-list tbody .btn-delete': function (e) {

            e.preventDefault();

            var a = $(this),
                tr = a.parents('.todo-entry'),
                id = tr.data('id');
            utils.setReplaceSpinning(a);

            routes.controllers.api.json.AdventureTodoController.deleteTodo(adv.id, id).ajax({
                success: function (res) {
                    utils.resetReplaceSpinning(a);
                    tr.fadeOut(function () {
                        tr.remove();
                    });
                }
            });

        },

        'click #todos-list tbody .btn-affiliate, #todos-adventurers-list tbody .btn-affiliate': function (e) {

            var a = $(this),
                tr = a.parents('.todo-entry'),
                id = tr.data('id');

            $('#amazon-affiliate .modal-content .modal-body .content').empty();
            $('#amazon-affiliate .modal-content .modal-body .affiliate-loading').removeClass('stash');
            $('#amazon-affiliate').modal('show');


            routes.controllers.api.json.AdventureTodoController.getTodoAffiliateItems(adv.id).ajax({data: {id: id}, success: function (res) {
                if (!res.length) $('#amazon-affiliate .modal-content .modal-body .content').html(messages['adventure.todos.amazon.noItems']);


                for (var i in res)
                    $('#amazon-affiliate .modal-content .modal-body .content').append(tmpl('todo-affiliate-template', res[i]));


                $('#amazon-affiliate .modal-content .modal-body .affiliate-loading').addClass('stash');
            }});
        },

        'click .btn-files-refresh': function () {
            loadFiles();
        },
        'drop .files-upload-dropzone': function (event) {
            event.stopPropagation();
            event.preventDefault();

            uploadFiles(event.target.files || event.dataTransfer.files, $(this));

            $('.files-upload-dropzone').removeClass('hover');
            return false;
        },
        'click .btn-file-delete': function () {
            deleteFile($(this).data('id'), $(this));
        },
        'change #files-file-input': function () {
            var inputFile = $('#files-file-input'),
                files = inputFile[0].files;
            if (files) {
                uploadFiles(files);
                inputFile.val('');
            }
        },
        'click .btn-file-upload': function () {
            $('#files-file-input').click();
        },
        'click .btn-email-preview': function () {
            routes.controllers.api.json.AdventureEmailController.getEmail(adv.id, $(this).data('timestamp')).ajax({success: function (res) {
                $('#email-modal .email-title').html(res.subject);
                $('#email-modal .email-body').html(res.body);
                $('#email-modal').modal('show');
            }});

        },


        'click .btn-comment-add': function () {
            var val,
                btn = $(this),
                input = btn.closest('.timeline-content').find('input:text'),
                threadId = adv.id + "_discussion";

            // Check if there is a value
            if (val = (input.val() || "").trim()) {
                input.val(""); // Reset
                var list = $('.comment-list-' + threadId);

                btn.html('<i class="fa fa-spin icon-journwe"></i>');

                routes.controllers.api.json.CommentController.saveComment().ajax({data: {text: val, threadId: threadId}, success: function (f) {
                    f.type = "comment";
                    $('.timeline').prepend(renderTimeline(f));
                    btn.html('<i class="fa fa-plus"></i>');
                }});

            }
        },
        'keyup .timeline-content input:text': function (e) {
            if (e.keyCode == 13) {
                $(this).closest('.timeline-content').find('.btn-comment-add').click();
            }
        },

        'dragenter .files-upload-dropzone': function (evt) {
            window.clearTimeout(_timer);
            window.setTimeout(function () {
                window.clearTimeout(_timer);
                $(this).addClass("hover");
            }.bind(this), 10);
        },

        'dragleave .files-upload-dropzone': function (evt) {
            _timer = window.setTimeout(function () {
                $(this).removeClass("hover");
            }.bind(this), 100);
        },

        'show.bs.tab .nav.nav-discussion a': function (evt) {
            if ($(evt.target).attr("href") === $(evt.relatedTarget).attr("href")) {
                $(evt.target).parent().addClass("active");
                $(evt.relatedTarget).parent().removeClass("active");
                return false;
            }
        }


    });


    return {
        initBackground: initBackground,
        initPrimeImage: initPrimeImage,
        initScrollspy: initScrollspy,
        initNavigation: initNavigation,
        initializeEmails: initializeEmails,
        initializeIndex: initializeIndex,
        initializeOptions: initializeOptions,
        initializePlaces: initializePlaces,
        initializePeople: initializePeople,
        initializeTime: initializeTime,
        initializeTimeline: initializeTimeline,
        initializeTodos: initializeTodos,
        initializeFiles: initializeFiles,
        loadPublicAdventurers: loadPublicAdventurers,
        updateFavoriteTime: updateFavoriteTime,
        updateFavoritePlace: updateFavoritePlace
    };

});