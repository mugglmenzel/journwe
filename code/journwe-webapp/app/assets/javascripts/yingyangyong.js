define([
    "common/utils",
    "routes",
    "messages",
    "common/gmaps",
    "inspirationData"
], function (utils, routes, messages, gmaps, ins) {


    var map, markers = {};
    var socialUsers = {},
        socialUserNames = [];


    var initialize = function () {
        gmaps.visualRefresh = true;

        if (map) {
            return;
        }
        var mapOptions = {
            zoom: 15,
            minZoom: 2,
            maxZoom: 19,
            center: new gmaps.LatLng(49.483472, 8.476992),
            mapTypeId: gmaps.MapTypeId.ROADMAP
        };
        if($('#where-map').length) map = new gmaps.Map(document.getElementById('where-map'),
            mapOptions);

        new gmaps.places.Autocomplete(document.getElementById('place-add-input'));

        friendTypeahead();

        //load inspiration
        if (ins != null && ins.id != null) {
            ins.timeStart = utils.formatDateSDF(ins.timeStart);
            ins.timeEnd = utils.formatDateSDF(ins.timeEnd);

            renderPlace(ins);
            renderTime(ins);
        }

    };


    /*
     var checkShortname = function () {
     $.post('@api.json.routes.AdventureController.checkShortname()', { value : $('#shortname').val() },
     function(check) {
     $('#shortname-available').html(check.valid ? '<span class="text-success"><i class="fa fa-ok"></i> available.</span>' : '<span class="text-error"><i class="fa fa-warning-sign"></i> not available!</span>' );
     $('#shortname').attr('aria-invalid', !check.valid);
     if(!check.valid)
     $('#shortname').parents(".control-group").first().addClass('error');
     else
     $('#shortname').parents(".control-group").first().removeClass('error');
     });
     };
     */

    var validateYingYangYong = function () {
        /*
         if ($('input[name="name"]').val() == null || $('input[name="name"]').val() == '') {
         $('input[name="name"]').focus();
         return false;
         }
         */
        /*
         if($('input[name="shortname"]').val() == null ||  $('input[name="shortname"]').val() == '') {
         $('input[name="shortname"]').focus();
         return false;
         }
         */

        return true;
    };


    var searchPlace = function () {
        new google.maps.Geocoder().geocode({'address': $('#place-add-input').val()}, function (results, status) {
            var ins = {
                id: 'item-place-' + String(results[0].geometry.location.lat()).replace('.', '_') + '-' + String(results[0].geometry.location.lng()).replace('.', '_'),
                placeLatitude: results[0].geometry.location.lat(),
                placeLongitude: results[0].geometry.location.lng(),
                placeAddress: results[0].formatted_address
            };
            renderPlace(ins);
            $('.input-place-add').val('');
        });
    };

    var renderPlace = function (data) {

        $('.list-places').append(tmpl('item-place-template', data));

        markers[data.id] = new gmaps.Marker({
            map: map,
            position: new gmaps.LatLng(data.placeLatitude, data.placeLongitude)
        });

        resetBounds();
    };

    var resetBounds = function () {
        gmaps.resetBounds(map, markers);
    };

    var removeMarker = function (id) {
        markers[id].setMap(null);
        delete markers[id];
        resetBounds();
        return false;
    };


    var addFriend = function () {
        var ins = {
            socialId: getSocialIdByType($('#people-add-provider-icon').data('social-provider')),
            name: $('#people-add-input').val(),
            provider: $('#people-add-provider-icon').data('social-provider'),
            iconCss: $('#people-add-provider-icon').attr('class')
        };

        renderFriend(ins);

        $('#people-add-input').val('');
    };

    var renderFriend = function (data) {
        $('.list-people').append(tmpl('item-friend-template', data));
    };


    var getSocialIdByType = function (type) {
        if (type === 'email') return $('#people-add-input').val();
        else return socialUsers[$('#people-add-input').val()]
    };

    var friendTypeahead = function () {
        if ($('#people-add-provider-icon').data('typeahead') == "off") $('.input-people-add').typeahead('destroy');
        else {
            var provider = $('#people-add-provider-icon').data('social-provider');

            if (provider != null && provider != 'email'){
                $('.input-people-add').attr('type', 'text');
                $('.input-people-add').typeahead({
                    name: 'people-typeahead',
                    template: '<p><strong>{%=o.name%}</strong></p>',
                    engine: {_templ: '', compile: function (template) {
                        _templ = template;
                        return this;
                    }, render: function (data) {
                        return tmpl(_templ, data);
                    }},
                    remote: {
                        url: routes.controllers.api.json.AdventurePeopleController.autocomplete().absoluteURL() + '?provider=' + provider + '&input=%QUERY',
                        filter: function (data) {
                            socialUsers = {};
                            socialUserNames = [];
                            $.each(data, function (ix, item) {
                                if ($.inArray(item.name, socialUserNames) > -1) {
                                    item.nameId = item.name + ' #' + item.id;
                                } else item.nameId = item.name

                                socialUserNames.push({value: item.nameId, name: item.name, tokens: [item.id, item.name]});
                                socialUsers[item.nameId] = item.id;
                            });

                            return socialUserNames;
                        }
                    }
                });
            } else {
                $('.input-people-add').attr('type', 'email');
                $('.input-people-add').typeahead('destroy');
            }
        }
    };

    var addTime = function () {

        var start = $('#time-add-input-start'),
            end = $('#time-add-input-end');

        if (!start.val()) {
            start.focus();
            return false;
        }

        if (!end.val()) {
            end = start;
        }

        var ins = {
            timeStart: start.val(),
            timeEnd: end.val()
        };

        renderTime(ins);

        start.val('');
        end.val('');
    };

    var renderTime = function (data) {
        $('.list-times').append(tmpl('item-time-template', data));
    };

    utils.on({

        'submit #yingyangyong-form': function () {
            return validateYingYangYong();
        },
        'click .btn-yingyangyong-submit': function () {
            $('#yingyangyong-form').submit();
        },

        'click .item-place-remove': function () {
            var li = $(this).closest('.item-place');
            removeMarker(li.data('id'));
            li.remove();
            return false;
        },
        'click .btn-place-add': function () {
            searchPlace();
        },
        'keypress .input-place-add': function () {
            if (event.which == 13 || event.keyCode == 13) {
                searchPlace();
                event.preventDefault();
                return false;
            } else return true;
        },

        'click .item-friend-remove': function () {
            $(this).closest('.item-friend').remove();
            return false;
        },
        'click .btn-people-add': function () {
            addFriend();
        },
        'keypress .input-people-add': function () {
            if (event.which == 13 || event.keyCode === 13) {
                addFriend();
                event.preventDefault();
                return false;
            } else return true;
        },
        'click #people-add-provider .dropdown-menu a': function () {
            $('#people-add-provider-icon').attr('class', $(this).data('icon'));
            $('#people-add-provider-icon').data('social-provider', $(this).data('social-provider'));
            $('#people-add-provider-icon').data('typeahead', $(this).data('typeahead'));
            $('#people-add-input').attr('type', $(this).data('input-type'));
            friendTypeahead();
            $('#people-add-input').focus();
        },
        'change #facebookWall': function () {
            $('.post-on-facebook').toggle($(this).is(':checked'));
        },

        'click .item-time-remove': function () {
            $(this).closest('.item-time').remove();
            return false;
        },
        'click .btn-time-add': function () {
            addTime();
        }

    });


    $("#time-add-input-start").datepicker({startDate: new Date()}).on('changeDate', function (e) {
        $("#time-add-input-end").datepicker('setStartDate', e.date);
        $("#time-add-input-start").datepicker('hide');
    });
    $("#time-add-input-end").datepicker({startDate: new Date()}).on('changeDate', function (e) {
        $("#time-add-input-end").datepicker('hide');
    });


    initialize();


    return {
        resetBounds: function () {
            resetBounds();
        }
    }

});