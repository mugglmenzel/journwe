define([
    "common/utils",
    "routes"
], function (utils, routes) {

    var pop_content = $('#notifications-popover-content');
    var pop_content_loaded = false;

    var initialize = function () {

        $('#notifications-link').popover({content: function () {
            return pop_content;
        }});


        utils.on({
            'show.bs.popover #notifications-link': function () {
                $('body').addClass('unscrollable');
                if (!pop_content_loaded)
                    loadNotifications(true);
            },
            'hide.bs.popover #notifications-link': function () {
                $('body').removeClass('unscrollable');
            },

            'click .login-mail': function(){
                $(this).parents('.navbar-right').hide();
                $('.login-mail-container').fadeIn();
            }

        });
    };

    var loadNotifications = function (clear, lastId) {
        if (clear) utils.resetStash($(pop_content).find('i.loading-top'));
        routes.controllers.api.json.UserController.getNotifications().ajax({data: {lastId: lastId, count: 5}, success: function (d) {
            if (clear) $(pop_content).find('.notifications-list').empty();
            for (var i in d)
                $(pop_content).find('.notifications-list').append(tmpl('header-notification-template', d[i]));

            if (!pop_content_loaded)
                $(pop_content).find('.notifications-list').scroll(function (e) {
                    e.preventDefault();
                    e.stopPropagation();
                    event.preventDefault();
                    event.stopPropagation();

                    if ($(this).scrollTop() === 0)
                        loadNotifications(true);
                    else if ($(this).scrollTop() >= $(this).prop('scrollHeight') - $(this).height() - 20) {
                        var lastKey = $(this).find('a').last().data('id');
                        if (lastKey) {
                            $(pop_content).find('i.loading-bottom').removeClass('stash');
                            loadNotifications(false, lastKey);
                        }
                    }
                    return false;
                });

            pop_content_loaded = true;

            utils.setStash($(pop_content).find('i'));
        }});

    };

    return {initialize: initialize};

});