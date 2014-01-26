define([
    "common/utils",
    "routes",
    "messages",
    "common/gmaps"
], function (utils, routes, messages, gmaps) {


    var map, markers = {};
    var facebookUsers = {},
        facebookUserNames = [],
        peopleCounter = 0;


    var initialize = function () {
        gmaps.visualRefresh = true;

        if (map) {
            return;
        }
        var mapOptions = {
            zoom: 15,
            minZoom: 2,
            maxZoom: 19,
            center: new google.maps.LatLng(49.483472, 8.476992),
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };
        map = new gmaps.Map(document.getElementById('where-map'),
            mapOptions);

        facebookFriendTypeahead();
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
            var id = 'places-item-' + results[0].geometry.location.lat().toString().replace('.', '_') + '-' + results[0].geometry.location.lng().toString().replace('.', '_');
            $('#places-list').append('<li id="' + id + '"><input type="hidden" name="place[]" value="' + results[0].formatted_address + '/' + results[0].geometry.location.lat().toString() + '/' + results[0].geometry.location.lng().toString() + '"><span class="label label-success"><i class="fa fa-map-marker"></i> ' + results[0].formatted_address + ' &nbsp;<a href="#" onclick="$(\'#' + id + '\').remove(); removeMarker(\'' + id + '\'); return false;">&times;</a></span> <span class="divider"></span></li>');
            map.setCenter(results[0].geometry.location);
            var marker = new google.maps.Marker({
                map: map,
                position: results[0].geometry.location
            });
            markers[id] = marker;

            resetBounds();
            $('#place-add-input').val('');
        });
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
    }


    var addFriend = function () {
        var id = 'people-item-' + peopleCounter++;
        var input = ($('#people-add-input').attr('type') == 'text') ? '<input type="hidden" name="facebook[]" value="' + facebookUsers[$('#people-add-input').val()] + '">' : '<input type="hidden" name="email[]" value="' + $('#people-add-input').val() + '">';
        $('#people-list').append('<li id="' + id + '">' + input + '<span class="label label-danger"><i class="' + $('#people-add-type').attr('class') + '"></i> ' + $('#people-add-input').val() + ' &nbsp;<a href="#" onclick="$(\'#' + id + '\').remove();">&times;</a></span> <span class="divider"></span></li>');
        $('#people-add-input').val('');
    }

    var facebookFriendTypeahead = function () {
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
                url: routes.controllers.api.json.AdventurePeopleController.autocompleteFacebook().absoluteURL() + '?input=%QUERY',
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

        var id = 'time-item-' + start.val() + '-' + end.val();
        $('#time-list').append('<li id="' + id + '"><input type="hidden" name="time[]" value="' + start.val() + ',' + end.val() + '"><span class="label label-info"><i class="fa fa-time"></i> ' + start.val() + ' to ' + end.val() + ' &nbsp;<a href="#" onclick="$(\'#' + id + '\').remove();">&times;</a></span> <span class="divider"></span></li>');

        start.val('');
        end.val('');
    };

    utils.on({

        'submit #yingyangyong-form': function () {
            return validateYingYangYong();
        },
        'click .btn-yingyangyong-submit': function () {
            $('#yingyangyong-form').submit();
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
            $('#people-add-input').attr('type', $(this).data('type'));
            if ($(this).data('typeahead') == "on") facebookFriendTypeahead();
            else $('#people-add-input').typeahead('destroy');
            $('#people-add-input').focus();
        },
        'change #facebookWall': function () {
            $('.post-on-facebook').toggle($(this).is(':checked'));
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