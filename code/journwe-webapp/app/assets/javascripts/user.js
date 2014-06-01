require([
    "common/utils",
    "routes",
    "userData",
    "modules/main"
], function (utils, routes, user) {

    var initialize = function () {
        utils.loadGenericBgImage();

        $('#userName').editable();

        loadUserEmails(true);

        loadUserAdventures(true);

    };

    var renderAdventure = function (tmplId, adv) {
        if (adv.favoriteTime != null && adv.favoriteTime.startDate != null) adv.favoriteTime.startDate = utils.formatDateShort(adv.favoriteTime.startDate);
        if (adv.status != null) adv.cssLabel = utils.adventurerCSSLabel[adv.status];
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
        routes.controllers.api.json.UserController.getPublicAdventures(user.id).ajax({data: {count: 10}, success: function (advs) {
            $('#adventures-user-count').html(advs.count);

            if (clear) $('#adventures-user-list').empty();
            for (var i in advs.adventures) {
                $('#adventures-user-list').append(renderAdventure('adventure-user-template', advs.adventures[i]));
            }
            utils.setStash($('#adventures-user-list > i'));
        }});
    };

    var uploadPrimeImage = function (files) {
        var btn = $('.btn-prime-image-upload');
        utils.setReplaceSpinning(btn);

        var data = new FormData();
        data.append(files[0].name, files[0])

        routes.controllers.api.json.UserController.updateImage().ajax({
            data: data,
            cache: false,
            contentType: false,
            processData: false,
            success: function (result) {
                $('#user-prime-image').attr('src', 'http://www.journwe.com/thumbnail?h=200&w=1200&u=' + result.image + '&t=' + result.imageTimestamp);
                utils.resetReplaceSpinning(btn);
            }
        });

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
        },
        'change #user-prime-image-file-input': function () {
            var inputFile = $('#user-prime-image-file-input'),
                files = inputFile[0].files;
            if (files) {
                uploadPrimeImage(files);
                inputFile.val('');
            }
        },
        'click .btn-prime-image-upload': function () {
            $('#user-prime-image-file-input').click();
        }
    });

    initialize();

});