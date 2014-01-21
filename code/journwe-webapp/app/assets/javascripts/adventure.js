require([
    "main",
    "utils",
    "routes",
    "adventureData",
    "adventurerData",
    "async!https://maps.googleapis.com/maps/api/js?key=AIzaSyAbYnwpdOgqWhspiETgFdlXyX3H2Fjb8fY&sensor=false!callback"
], function (main, utils, routes, adv, advr) {


        var now = new Date(),
            today = new Date(now.getFullYear(), now.getMonth(), now.getDate(),
                0, 0, 0, 0);
        var map,
            markers = [];
        var votePlaceIconCSSClassMap = {'YES': 'fa fa-thumbs-up', 'NO': 'fa fa-thumbs-down', 'MAYBE': 'fa fa-question'},
            votePlaceButtonCSSClassMap = {'YES': 'btn-success', 'NO': 'btn-danger', 'MAYBE': 'btn-warning'},
            votePlaceLabelCSSClassMap = {'YES': 'label-success', 'NO': 'label-danger', 'MAYBE': 'label-warning'};
        var voteTimeIconCSSClassMap = {'YES': 'fa fa-thumbs-up', 'NO': 'fa fa-thumbs-down', 'MAYBE': 'fa fa-question'},
            voteTimeButtonCSSClassMap = {'YES': 'btn-success', 'NO': 'btn-danger', 'MAYBE': 'btn-warning'},
            voteTimeLabelCSSClassMap = {'YES': 'label-success', 'NO': 'label-danger', 'MAYBE': 'label-warning'};
        var favoritePlace, favoriteTime;


        var initialize = function () {
            initializeMap();

            loadEmails();

            initializePlaces();
            initializeTime();
            initializeTodos();
        };


        var processDroppedPrimeImage = function (event) {
            event.stopPropagation();
            event.preventDefault();

            uploadPrimeImage(event.target.files || event.dataTransfer.files);

            return false;
        };

        var uploadPrimeImage = function (files) {
            var btn = $('#adventure-prime-image-upload-button'),
                btnOriginal = btn.html();
            btn.css({width: btn.css('width')})
                .html('<i class="fa fa-spin icon-journwe"></i>');

            var data = new FormData();
            data.append(files[0].name, files[0])

            routes.controllers.AdventureController.updateImage(adv.id).ajax({
                data: data,
                cache: false,
                contentType: false,
                processData: false,
                success: function (result) {
                    $('#adventure-prime-image').css('background', 'url(\'http://i.embed.ly/1/image/crop?height=200&width=1200&url=' + result.image + '&key=2c8ef5b200c6468f9f863bc75c46009f&timestamp=' + new Date().getTime() + '\')');
                    btn.css({width: ""})
                        .html(btnOriginal);
                }
            });

        };


        var loadEmails = function () {
            $('#emails-button-refresh i').addClass("fa-spin");

            routes.controllers.AdventureEmailController.listEmails(adv.id).ajax({success: function (emails) {
                $('#emails-list tbody').empty();

                for (var i in emails) {
                    renderEmail(emails[i]);
                }
                if (emails.length) {
                    $('#emails-list').show();
                } else {
                    $('#emails-list').hide();
                }

                $('#emails-button-refresh i').removeClass("fa-spin");
            }});
        };


        var renderEmail = function (data, replace) {
            if (replace)
                replace.replaceWith(tmpl('emails-template', data)).fadeIn();
            else
                $('#emails-list tbody').append(tmpl('emails-template', data));

        };

        var initializePlaces = function () {
            routes.controllers.AdventurePlaceController.getPlaces(adv.id).ajax({success: function (result) {
                $('#places-list tbody').empty();
                for (var id in result)
                    renderPlaceOption(result[id])

                if (result.length)
                    $('#places-list').show();
                else
                    $('#places-list').hide();

                $('.places-loading').hide();

                updateFavoritePlace();

                routes.controllers.AdventureController.placeVoteOpen(adv.id).ajax({success: function (result) {
                    updatePlaceVoteOpen(result);
                }});
            }});
        };

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
            var place = $(tmpl('place-template', $.extend({
                votePlaceLabelCSSClassMap: votePlaceLabelCSSClassMap
            }, data)));

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

        var updateFavoritePlace = function () {

            $('#places-favorite-place-icon').removeClass("fa-star").addClass("fa-spin icon-journwe");
            $('#places-autofavorite-place-icon').removeClass("fa-star").addClass("fa-spin icon-journwe");

            routes.controllers.AdventurePlaceController.getFavoritePlace(adv.id).ajax({success: function (result) {
                favoritePlace = result.favorite;
                if (result.favorite != null) $('#places-favorite-place-name').html(result.favorite.address);
                if (result.autoFavorite != null)$('#places-autofavorite-place-name').html(result.autoFavorite.address);
                $('.btn-close-place').toggle(!!result.favorite);
                $('#places-favorite-place-icon').removeClass("fa-spin icon-journwe").addClass("fa-star");
                $('#places-autofavorite-place-icon').removeClass("fa-spin icon-journwe").addClass("fa-star");
            }});
        };

        var setFavoritePlace = function (placeID, el) {

            el.find('i').attr("class", "fa fa-spin icon-journwe");
            $('#places-favorite-place-icon').removeClass("fa-star").addClass("fa-spin icon-journwe");

            routes.controllers.AdventurePlaceController.setFavoritePlace(adv.id).ajax({
                data: {favoritePlaceId: placeID},
                success: function (data) {
                    favoritePlace = data;
                    $('#places-favorite-place-name').html(data.address);
                    $('#places-favorite-place-icon').removeClass("fa-spin icon-journwe").addClass("fa-star");

                    $(el).find('i').removeClass("fa-spin icon-journwe").addClass("fa-star");
                    el.closest('table').find('td:first-child .btn-success').removeClass('btn-success');
                    $(el).addClass('btn-success');

                    updatePlaceVoteOpen($(".btn-set-close-place").is(".btn-success"));
                }
            });
        };

        var votePlace = function (vote, voteGrav, optId) {

            // Spin
            $('#placeoption-item-' + optId + ' .dropdown-toggle')
                .html('<i class="fa fa-spin icon-journwe"></i>');

            routes.controllers.AdventurePlaceController.voteParam(adv.id).ajax({data: {placeId: optId, vote: vote, voteGravity: voteGrav}, success: function (res) {
                renderPlaceOption(res, $('#placeoption-item-' + res.placeId));
                updateFavoritePlace();
                $('#placeoption-item-' + res.placeId + ' .dropdown-toggle')
                    .html('<i class="fa fa-pencil"></i>');
            }});
        };


        var deletePlace = function (optId, el) {
            el.html('<i class="fa fa-spin icon-journwe"></i>');
            routes.controllers.AdventurePlaceController.deletePlace(adv.id, optId).ajax({success: function () {
                removeMapMarker(optId);
                $('#placeoption-item-' + optId).fadeOut(function () {
                    $('#placeoption-item-' + optId).remove();
                });
            }});
        };


        var initializeTime = function () {
            $("#time-add-input-start").datepicker({startDate: new Date()}).on('changeDate', function (e) {
                $("#time-add-input-end").datepicker('setStartDate', e.date);
                $("#time-add-input-start").datepicker('hide');
            });
            $("#time-add-input-end").datepicker({startDate: new Date()}).on('changeDate', function (e) {
                $("#time-add-input-end").datepicker('hide');
            });

            $('#times-favorite-time-icon').removeClass("fa-star").addClass("fa-spin icon-journwe");
            routes.controllers.AdventureTimeController.getTimes(adv.id).ajax({success: function (result) {
                if (result.favoriteTime != '') $('#times-favorite-time-name').html(formatDate(result.favoriteTime.startDate) + " - " + formatDate(result.favoriteTime.endDate));
                $('.btn-close-time').toggle(!!result.favoriteTime);

                $('#times-list tbody').empty();
                for (var id in result.times) {
                    renderTimeOption(result.times[id])
                }
                if (result.times.length) {
                    $('#times-list').show();
                } else {
                    $('#times-list').hide();
                }

                $('.times-loading').hide();
                $('#times-favorite-time-icon').removeClass("fa-spin icon-journwe").addClass("fa-star");

                updateTimeVoteOpen(adv.timeVoteOpen);
            }});
        };


        var setFavoriteTime = function (timeID, el) {
            $('#times-favorite-time-icon').removeClass("fa-star").addClass("fa-spin icon-journwe");
            $(el).find('i').removeClass("fa-star").addClass("fa-spin icon-journwe");
            routes.controllers.AdventureTimeController.setFavoriteTime(adv.id).ajax({data: {favoriteTimeId: timeID}, success: function (data) {
                favoriteTime = data;
                $('#times-favorite-time-name').html(formatDate(data.startDate) + " - " + formatDate(data.endDate));
                $('#times-favorite-time-icon').removeClass("fa-spin icon-journwe").addClass("fa-star");

                el.closest('table').find('td:first-child .btn-success').removeClass('btn-success');
                $(el).addClass('btn-success');
                $(el).find('i').removeClass("fa-spin icon-journwe").addClass("fa-star");

                $('.btn-close-time').show();
            }});
        };

        var renderTimeOption = function (data, replace) {

            var time = $(tmpl('time-template', $.extend({
                voteTimeLabelCSSClassMap: voteTimeLabelCSSClassMap
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
                $('#times-list button').prop('disabled', false);
                $('#time-add-form').fadeIn();
            } else {
                $('#times-list button').prop('disabled', true);
                $('#time-add-form').fadeOut();
            }
        };


        var voteTime = function (vote, optId) {
            $('#timeoption-item-' + optId + ' .dropdown-toggle')
                .html('<i class="fa fa-spin icon-journwe"></i>');
            routes.controllers.AdventureTimeController.vote(adv.getId, optId).ajax({
                data: {
                    vote: vote
                },
                success: function (res) {
                    renderTimeOption(res, $('#timeoption-item-' + res.timeId));
                    $('#timeoption-item-' + res.timeId + ' .dropdown-toggle')
                        .html('<i class="fa fa-pencil"></i>');
                }
            });
        };

        var deleteTime = function (optId, el) {
            $(el).html('<i class="fa fa-spin icon-journwe"></i>');
            routes.controllers.AdventureTimeController.deleteTime(adv.id, optId).ajax({success: function () {
                $('#timeoption-item-' + optId).fadeOut(function () {
                    $('#timeoption-item-' + optId).remove();
                });
            }
            });
        };


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


        var initializeTodos = function () {
            loadUserTodos();
            var firstAdvr = $('#todos-adventurers-selection div').first().data('id');
            if (firstAdvr != null) loadAdventurerTodos(firstAdvr);
        };

        var loadUserTodos = function () {
            loadTodos(advr.userId, '#todos-list', 'todo-template');
        };

        var loadAdventurerTodos = function (advrId) {
            $('#todos-adventurers-selection > div').removeClass("polaroid-active");
            $('#todos-adventurer-' + advrId + '').addClass("polaroid-active");
            loadTodos(advrId, '#todos-adventurers-list', 'todo-adventurer-template');
        };

        var loadTodos = function (userId, target, template) {
            $('#todos-button-refresh i').addClass("fa-spin");
            routes.controllers.AdventureTodoController.getTodos(adv.id, userId).ajax({success: function (results) {
                $(target + ' tbody').empty();

                for (var todo in results)
                    renderTodo(results[todo], target, template);

                if (results.length)
                    $(target).show();
                else
                    $(target).hide();

                $('#todos-button-refresh i').removeClass("fa-spin");
            }});
        };

        var renderTodo = function (data, target, template, replace) {
            if (replace)
                replace.replaceWith(tmpl(template, data)).fadeIn();
            else
                $(target + ' tbody').append(tmpl(template, data));
        };


        /* start now */
        initialize();

        utils.on({
            // Click on navigation to scroll to it
            'click .nav-adventure a': function () {

                var section = $(this).attr('href');

                $('html, body').animate({
                    scrollTop: $(section).offset().top - 100
                }, 'slow');

                return false;
            },

            'click .btn-participate': function () {
                var el = $(this),
                    html = el.html();

                el.css({width: el.width() + "px"})
                    .html('<i class="fa fa-spin icon-journwe"></i>');

                return true;
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

            'click #emails-button-refresh': function () {
                loadEmails();
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
                    $(this).html('<i class="fa fa-spin icon-journwe"></i>');
                    new google.maps.Geocoder().geocode({'address': $('#place-add-input').val()}, function (results, status) {
                        routes.controllers.AdventurePlaceController.addPlace(adv.id).ajax({data: { address: results[0].formatted_address, lat: results[0].geometry.location.lat(), lng: results[0].geometry.location.lng(), comment: $('#place-add-comment-input').val()}, success: function (res) {
                            renderPlaceOption(res);
                            $('#place-add-input').val("");
                            $('#place-add-comment-input').val("");
                            $('#place-add-button').html('<i class="fa fa-plus"></i>');
                            $('#places-list').show();
                            refreshComments("@adv.getId" + "places", $("#comments-list-places"));
                        }});
                    });
                } else {
                    $('#place-add-input').focus();
                    return false;
                }
            },
            'change #places-voting-active-switch': function () {
                routes.controllers.AdventureController.updatePlaceVoteOpen(adv.id).ajax({data: {voteOpen: $('#places-voting-active-switch').prop('checked')}, success: updatePlaceVoteOpen});
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

            'click .btn-favorite-place': function () {
                setFavoritePlace($(this).closest('tr').data('placeid'), $(this));
            },
            'click .btn-delete-place': function () {
                deletePlace($(this).closest('tr').data('placeid'), $(this));
            },

            'click .btn-set-close-place': function () {

                var btn = $(this),
                    open = btn.is(".btn-success");

                btn.find('i').attr("class", "fa fa-spin icon-journwe");

                routes.controllers.AdventureController.updatePlaceVoteOpen(adv.id).ajax({
                    data: {voteOpen: !open},
                    success: function (data) {
                        btn.find('i').attr("class", "fa fa-ok");
                        updatePlaceVoteOpen(data);
                    }
                });
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

                $(this).html('<i class="fa fa-spin icon-journwe"></i>');
                routes.controllers.AdventureTimeController.addTime(adv.id).ajax({
                    data: { startDate: start.val(), endDate: end.val()},
                    success: function (res) {
                        renderTimeOption(res);
                        $('#time-add-form input[name=name]').val("");
                        $('#time-add-form input[name=startDate]').val("");
                        $('#time-add-form input[name=endDate]').val("");
                        $('#time-add-button').html('<i class="fa fa-plus"></i>');
                        $('#times-list').show();
                    }});
            },
            'change #times-voting-active-switch': function () {
                routes.controllers.AdventureController.updateTimeVoteOpen(adv.id).ajax({data: {voteOpen: $('#times-voting-active-switch').prop('checked')}, success: updateTimeVoteOpen});
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
                    complete = i.is('.fa .fa-square-o');

                i.removeClass('fa fa-check, fa fa-check-empty').addClass('fa fa-spin icon-journwe');

                routes.controllers.AdventureTodoController.setTodo(adv.id, id).ajax({
                    data: {
                        status: complete ? 'COMPLETE' : 'NEW'
                    },
                    success: function (res) {
                        tr.get(0).className = 'status-' + res.status + ' todo-entry';
                        i.removeClass('fa fa-spin icon-journwe').addClass(res.status == 'COMPLETE' ? 'fa fa-check' : 'fa fa-check-empty');
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

                btn.html('<i class="fa fa-spin icon-journwe"></i>');

                routes.controllers.AdventureTodoController.addTodo(adv.id).ajax({data: {title: title}, success: function (res) {
                    loadUserTodos();
                    btn.html('<i class="fa fa-plus"></i>');
                }});
            },
            'keydown #todo-title': function (e) {
                if (e.keyCode == 13) {
                    $('#btn-add-todo').click();
                }
            },

            'click #btn-add-todo': function () {
                $(this).html('<i class="fa fa-spin icon-journwe"></i>');
                var btn = $(this),
                    fld = $('#todo-title');

                routes.controllers.AdventureTodoController.addTodo(adv.id).ajax({data: { title: fld.val() }, success: function (res) {
                    //$('.table-my-todos  tbody').append($(tmpl('todo-template', res).trim()).fadeIn());
                    //activateUserButtonListener();
                    loadUserTodos();
                    fld.val("");
                    $('#btn-add-todo').html('<i class="fa fa-plus"></i>')
                }});
            },
            'click #todos-list tbody .btn-delete': function (e) {
                $(this).html('<i class="fa fa-spin icon-journwe"></i>');
                e.preventDefault();

                var a = $(this),
                    tr = a.parents('.todo-entry'),
                    id = tr.data('id');

                routes.controllers.AdventureTodoController.deleteTodo(adv.id, id).ajax({
                    success: function (res) {
                        tr.fadeOut(function () {
                            tr.remove();
                        });
                    }
                });

            },

            'click #todos-list tbody .btn-affiliate, #todos-adventurers-list tbody .btn-affiliate': function (e) {
                e.preventDefault();

                var a = $(this),
                    tr = a.parents('.todo-entry'),
                    id = tr.data('id');

                $('#amazon-affiliate .modal-content .modal-body .content').first().empty();
                $('#amazon-affiliate .modal-content .modal-body .fa-spin').first().removeClass('stash');
                $('#amazon-affiliate').modal();

                routes.controllers.AdventureTodoController.getTodoAffiliateItems(adv.id).ajax({data: {id: id}, success: function (res) {
                    if (!res.length) $('#amazon-affiliate .modal-content .modal-body .content').first().html('@Messages("adventure.todos.amazon.noItems")');

                    for (var i in res)
                        $('#amazon-affiliate .modal-content .modal-body .content').first().append(tmpl('todo-affiliate-template', res[i]));

                    $('#amazon-affiliate .modal-content .modal-body .fa-spin').first().addClass('stash');
                }});
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


    }
)
;