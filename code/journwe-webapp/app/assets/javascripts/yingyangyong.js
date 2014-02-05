define([
    "common/utils",
    "routes",
    "messages",
    "common/gmaps",
    "inspirationData"
], function (utils, routes, messages, gmaps, ins) {


    var map, markers = {};
    var facebookUsers = {},
        facebookUserNames = [];


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
        map = new gmaps.Map(document.getElementById('where-map'),
            mapOptions);

        friendTypeahead();

        //load inspiration
        if (ins != null) {
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
        if ($('input[name="name"]').val() == null || $('input[name="name"]').val() == '') {
            $('input[name="name"]').focus();
            return false;
        }
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

        markers['item-place-' + data.placeLatitude.toString.replace('.', '_') + '-' + data.placeLongitude.toString().replace('.', '_')] = new gmaps.Marker({
            map: map,
            position: new gmaps.LatLng(data.placeLatitude, data.placeLongitude)
        });

        resetBounds();
    };

    var resetBounds = function () {
        if (!map) {
            return;
        }
        if (!$.isEmptyObject(markers)) {
            var bounds = new gmaps.LatLngBounds();
            for (var i in markers) {
                bounds.extend(markers[i].getPosition());
            }
            map.fitBounds(bounds);
        } else if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                var pos = new gmaps.LatLng(position.coords.latitude, position.coords.longitude);
                map.setCenter(pos);
            }, function () {
            });
        }
        gmaps.event.trigger(map, 'resize');
    };

    var removeMarker = function (id) {
        markers[id].setMap(null);
        delete markers[id];
        resetBounds();
        return false;
    };


    var addFriend = function () {
        var ins = {
            socialId: getSocialIdByType($('#people-add-type-icon').data('social-type')),
            type: $('#people-add-type-icon').data('social-type'),
            iconCss: $('#people-add-type-icon').attr('class')
        };

        renderFriend(ins);

        $('#people-add-input').val('');
    };

    var renderFriend = function (data) {
        $('.list-people').append(tmpl('item-friend-template', data));
    };


    var getSocialIdByType = function (type) {
        if (type === 'facebook') return facebookUsers[$('#people-add-input').val()];
        if (type === 'email') return   $('#people-add-input').val();
        return '';
    };

    var friendTypeahead = function () {
        var type = $('#people-add-type-icon').data('social-type'),
            route = '';

        if(type === 'facebook') route =  routes.controllers.api.json.AdventurePeopleController.autocompleteFacebook().absoluteURL();

        if(route != '')
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
                    url: route + '?input=%QUERY',
                    filter: function (data) {
                        facebookUsers = {};
                        facebookUserNames = [];
                        $.each(data, function (ix, item) {
                            if ($.inArray(item.name, facebookUserNames) > -1) {
                                item.nameId = item.name + ' #' + item.id;
                            } else item.nameId = item.name

                            facebookUserNames.push({value: item.nameId, name: item.name, tokens: [item.id, item.name]});
                            facebookUsers[item.nameId] = item.id;
                        });

                        return facebookUserNames;
                    }
                }
            });
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
        'click #people-add-type .dropdown-menu a': function () {
            $('#people-add-type-icon').attr('class', $(this).data('icon'));
            $('#people-add-type-icon').data('social-type', $(this).data('social-type'));
            $('#people-add-input').attr('type', $(this).data('input-type'));
            if ($(this).data('typeahead') == "on") friendTypeahead();
            else $('#people-add-input').typeahead('destroy');
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