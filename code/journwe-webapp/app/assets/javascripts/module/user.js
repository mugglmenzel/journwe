require([
    "common/utils",
    "routes",
    "main",
    "userData"
], function (utils, routes, main, user) {

    var initialize = function () {
        utils.loadGenericBgImage();

        $.fn.editable.defaults.mode = 'inline';
        $.fn.editableform.loading = '<div class="x-edit-loading"><i class="icon-journwe fa fa-spin"></i></div>';
        $('#userName').editable();

        loadUserEmails(true);

        loadUserAdventures(true);

    };

    var renderAdventure = function (tmplId, adv) {
        if(adv.favoriteTime != null && adv.favoriteTime.startDate != null) adv.favoriteTime.startDate = utils.formatDateShort(adv.favoriteTime.startDate);
        if(adv.status != null) adv.cssLabel = utils.adventurerCSSLabel[adv.status];
        return $(tmpl(tmplId, adv));
    };
    
    var loadUserEmails = function (clear) {
        routes.controllers.api.json.UserController.getEmails(user.id).ajax({success: function (emails) {
            if (clear) $('.list-user-emails').empty();
            for (var i in emails) {
                $('.list-user-emails').append(tmpl('user-email-template', emails[i]));
            }
            $('.userEmail').editable();
        }});
    };


    var loadUserAdventures = function (clear) {
        utils.resetStash($('#adventures-user-list i'));
        routes.controllers.api.json.UserController.getAdventures(user.id).ajax({data: {count: 10}, success: function (advs) {
            $('#adventures-user-count').html(advs.count);

            if (clear) $('#adventures-user-list').empty();
            for (var i in advs.adventures) {
                $('#adventures-user-list').append(renderAdventure('adventure-user-template', advs.adventures[i]));
            }
            utils.setStash($('#adventures-user-list > i'));
        }});
    };

    utils.on({
        'click .btn-select-notification-frequency button': function () {
            var el = $(this),
                html = el.html();

            el.css({width: el.width() + "px"})
                .html('<i class="fa fa-spin icon-journwe"></i>');

            routes.controllers.api.json.UserController.setMailDigestFrequency(user.id, el.data('frequency')).ajax({success: function (json) {
                var state = json;

                el.css({width: ""})
                    .html(html);

                el.parent().find('.active').removeClass('active');
                el.addClass('active');
            }});
            return false;
        }
    });


    initialize();

});