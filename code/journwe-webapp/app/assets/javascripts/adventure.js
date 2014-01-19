require([
    "main",
    "utils",
    "routes"
], function (main, utils, routes) {


    var now = new Date(),
        today = new Date(now.getFullYear(), now.getMonth(), now.getDate(),
            0, 0, 0, 0);
    var map,
        markers = [];
    var votePlaceIconCSSClassMap = {'YES': 'fa fa-thumbs-up', 'NO': 'fa fa-thumbs-down', 'MAYBE': 'fa fa-question'},
        votePlaceButtonCSSClassMap = {'YES': 'btn-success', 'NO': 'btn-danger', 'MAYBE': 'btn-warning'},
        votePlaceLabelCSSClassMap = {'YES': 'label-success', 'NO': 'label-danger', 'MAYBE': 'label-warning'};
    var favoritePlace;


    var initializeMap = function () {
        google.maps.visualRefresh = true;
        var mapOptions = {
            zoom: 19,
            minZoom: 2,
            maxZoom: 19,
            center: new google.maps.LatLng(52.467541, 13.324957),
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new google.maps.Map(document.getElementById('place-add-map'), mapOptions);
    };


    var resetMapBounds = function () {
        var bounds = new google.maps.LatLngBounds();
        for (var i in markers) {
            bounds.extend(markers[i].getPosition());
        }
        map.fitBounds(bounds);
        google.maps.event.trigger(map, 'resize');
    };

    var removeMapMarker = function (id) {
        markers[id].setMap(null);
        delete markers[id];
        resetMapBounds();
    };

    var renderPlaceOption = function (data, replace) {
        var place = $(tmpl('place-template', data));

        if (replace)
            replace.replaceWith(place).fadeIn();
        else
            $('#places-list tbody').append(place).fadeIn();


        place.find('.rating :radio').click(function (e) {
            if ($(e.target).is(':checked')) votePlace('', $(e.target).val() / 5, $(e.target).data('id'));
        });

        var marker = new google.maps.Marker({animation: google.maps.Animation.DROP, map: map, position: new google.maps.LatLng(data.lat, data.lng), title: data.address});
        markers[data.placeId] = marker;
        map.setCenter(new google.maps.LatLng(data.lat, data.lng));
        resetMapBounds();

        $('#placeoption-status-icon-' + data.placeId).addClass(votePlaceIconCSSClassMap[data.vote]);
        $('#placeoption-status-' + data.placeId).addClass(votePlaceButtonCSSClassMap[data.vote]);
    };


    var updatePlaceVoteOpen = function (open) {

        if (open) {
            var address = favoritePlace && favoritePlace.address.split(",")[0];
            $(".btn-set-close-place").addClass("btn-success").html('<i class="fa fa-ok"></i> Close' + (address ? ' with ' + address : ''));
            $('#places-list button, .btn-set-reminder-place').prop('disabled', false);
            $('#place-add-form').fadeIn();
        } else {
            $('#places-list button, .btn-set-reminder-place').prop('disabled', true);
            $('#place-add-form').fadeOut();
        }

    };

    var updateFavorite = function () {

        $('#places-favorite-place-icon').removeClass("fa-star").addClass("fa-spin icon-journwe");
        $('#places-autofavorite-place-icon').removeClass("fa-star").addClass("fa-spin icon-journwe");

        $.get(routes.AdventurePlaceController.getFavoritePlace(utils.id()), function (result) {
            favoritePlace = result.favorite;
            if (result.favorite != null) $('#places-favorite-place-name').html(result.favorite.address);
            if (result.autoFavorite != null)$('#places-autofavorite-place-name').html(result.autoFavorite.address);
            $('.btn-close-place').toggle(!!result.favorite);
            $('#places-favorite-place-icon').removeClass("fa-spin icon-journwe").addClass("fa-star");
            $('#places-autofavorite-place-icon').removeClass("fa-spin icon-journwe").addClass("fa-star");
        });
    };

    var votePlace = function (vote, voteGrav, optId) {

        // Spin
        $('#placeoption-item-' + optId + ' .dropdown-toggle')
            .html('<i class="fa fa-spin icon-journwe"></i>');

        $.post(routes.AdventurePlaceController.voteParam(utils.id()), {placeId: optId, vote: vote, voteGravity: voteGrav}, function (res) {
            renderPlaceOption(res, $('#placeoption-item-' + res.placeId));
            updateFavorite();
            $('#placeoption-item-' + res.placeId + ' .dropdown-toggle')
                .html('<i class="fa fa-pencil"></i>');
        });
    };


    var deletePlace = function (optId, event) {
        $(event.target).html('<i class="fa fa-spin icon-journwe"></i>');
        $.ajax({url: '@routes.AdventurePlaceController.deletePlace(adv.getId, "")' + optId, type: 'DELETE', success: function () {
            removeMapMarker(optId);
            $('#placeoption-item-' + optId).fadeOut(function () {
                $('#placeoption-item-' + optId).remove();
            });
        }});
    };


    var deadline = function (btn, date, route) {

        // Set spinner/hide calendar
        btn.find("i").attr("class", "icon-spin icon-journwe");
        btn.datepicker("hide");

        // Do ajax call
        route(utils.id()).ajax({
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




    $(document).ready(function () {
        alert('initializing');
        //initialize Google Maps with callback to initializeMap
        var script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = 'https://maps.googleapis.com/maps/api/js?key=AIzaSyAbYnwpdOgqWhspiETgFdlXyX3H2Fjb8fY&sensor=false';
        document.body.appendChild(script);
        initializeMap();

        $.get(routes.AdventurePlaceController.getPlaces(utils.id()), function (result) {
            $('#places-list tbody').empty();
            for (var id in result)
                renderPlaceOption(result[id])

            if (result.length)
                $('#places-list').show();
            else
                $('#places-list').hide();

            $('.places-loading').hide();

            updateFavorite();

            $.get(routes.AdventureController.placeVoteOpen(utils.id()), function (result) {
                updatePlaceVoteOpen(result);
            });
        });

    });

    utils.on({

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
                $(this).html('<i class="fa fa-spin icon-journwe"></i>');
                new google.maps.Geocoder().geocode({'address': $('#place-add-input').val()}, function (results, status) {
                    $.post('@routes.AdventurePlaceController.addPlace(adv.getId)', { address: results[0].formatted_address, lat: results[0].geometry.location.lat(), lng: results[0].geometry.location.lng(), comment: $('#place-add-comment-input').val()}, function (res) {
                        renderPlaceOption(res);
                        $('#place-add-input').val("");
                        $('#place-add-comment-input').val("");
                        $('#place-add-button').html('<i class="fa fa-plus"></i>');
                        $('#places-list').show();
                        refreshComments("@adv.getId" + "places", $("#comments-list-places"));
                    });
                });
            } else {
                $('#place-add-input').focus();
                return false;
            }
        },
        'change #places-voting-active-switch': function () {
            $.post('@routes.AdventureController.updatePlaceVoteOpen(adv.getId)', {voteOpen: $('#places-voting-active-switch').prop('checked')}, updatePlaceVoteOpen);
        },
        // Click on navigation to scroll to it
        'click .nav-adventure a': function () {

            var section = $(this).attr('href');

            $('html, body').animate({
                scrollTop: $(section).offset().top - 100
            }, 'slow');

            return false;
        },


        'changeDate .btn-set-reminder-place': function (e) {
            deadline(
                $(this),
                e.date,
                routes.controllers.AdventureController.updatePlaceVoteDeadline
            );
        },


        'changeDate .btn-set-reminder-time': function (e) {
            deadline(
                $(this),
                e.date,
                routes.controllers.AdventureController.updateTimeVoteDeadline
            );
        },

        'click .btn-favorit': function () {

            var el = $(this),
                tb = el.closest('table'),
                placeID = el.is(".btn-success") ? undefined : el.data('placeid');

            tb.find('td:first-child .btn-success').removeClass('btn-success');
            el.find('i').attr("class", "icon-spin icon-journwe");
            $('icon-favorite-places').removeClass("icon-star").addClass("icon-spin icon-journwe");

            routes.controllers.AdventurePlaceController.setFavoritePlace(utils.id()).ajax({
                data: {favoritePlaceId: placeID},
                success: function (data) {
                    favoritePlace = data;
                    $('icon-favorite-places').removeClass("icon-spin icon-journwe").addClass("icon-star");
                    // Set stars
                    $(el).find('i').attr("class", "icon-star");
                    $('#places-favorite-place-name').html(data.address);
                    if (placeID) {
                        $(el).addClass('btn-success');
                    }
                    updatePlaceVoteOpen($(".btn-set-close-place").is(".btn-success"));
                }
            });
        },

        'click .btn-set-close-place': function () {

            var btn = $(this),
                open = btn.is(".btn-success");

            btn.find('i').attr("class", "icon-spin icon-journwe");

            routes.controllers.AdventureController.updatePlaceVoteOpen(utils.id()).ajax({
                data: {voteOpen: !open},
                success: function (data) {
                    btn.find('i').attr("class", "icon-ok");
                    updatePlaceVoteOpen(data);
                }
            });
        }
    });


    // Init all date fields
    $('.date').datepicker({
        startDate: today,
        weekStart: 1
    });


    $('body').scrollspy({offset: 100}).on('activate', function (evt) {
        $('.row.active').removeClass('active');
        $($(evt.target).find('a').attr('href')).parent('.row').addClass('active');
    });


});