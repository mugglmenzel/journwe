require([
    "common/utils",
    "routes",
    "userData"
], function (utils, routes, user) {


    var loadUserAdventures = function (clear) {
        $('#adventures-user-list i').removeClass('stash');
        routes.controllers.api.json.UserController.getAdventures(user.id).ajax({data: {count: 10}, success: function (advs) {
            $('#adventures-user-count').html(advs.count);

            if (clear) $('#adventures-user-list').empty();
            for (var i in advs.adventures) {
                if(advs.adventures[i].favoriteTime != null && advs.adventures[i].favoriteTime.startDate != null) advs.adventures[i].favoriteTime.startDate = utils.formatDateShort(advs.adventures[i].favoriteTime.startDate);
                $('#adventures-user-list').append(tmpl('adventure-user-template', advs.adventures[i]));
            }

            $('#adventures-user-list .polaroid').hide().fadeIn().hover(
                function () {
                    $(this).find("div.overlay").slideDown("fast");
                },
                function () {
                    $(this).find("div.overlay").slideUp("fast");
                }
            );
            $('#adventures-user-list i').addClass('stash');
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

    loadUserAdventures();

});