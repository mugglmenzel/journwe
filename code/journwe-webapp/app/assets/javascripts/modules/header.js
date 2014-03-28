require([
    "common/utils",
    "routes",
    "journwe"
], function (utils, routes, journwe) {
    journwe(function () {
        console.log("header js is loaded.");

        var pop_content = $('#notifications-popover-content');
        var pop_content_loaded = false;

        $('#notifications-link').popover({content: function () {
            return pop_content;
        }});

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

        }


        utils.on({
            'show.bs.popover #notifications-link': function () {
                $('body').addClass('unscrollable');
                if (!pop_content_loaded)
                    loadNotifications(true);
            },
            'hide.bs.popover #notifications-link': function () {
                $('body').removeClass('unscrollable');
            }

        });
    });
});