define([
    "common/utils",
    "routes",
    "messages"
], function (utils, routes, messages) {


    var refreshComments = function (threadId, list) {
        routes.controllers.api.json.CommentController.listComments(threadId).ajax({success: function (res) {
            list.html("");
            res.forEach(function (f) {
                f.time = utils.formatTime(f.comment.timestamp);
                f.color = utils.colorOfUser(f.user.name);
                list.append(tmpl('template-comment', f)).fadeIn();
            });

            if (!res.length) {
                list.append('<div class="empty"><i class="fa fa-envelope"></i> ' + messages["adventure.widgetComments.firstComment"] + '</div>');
            }
        }});
    }

    var addComment = function (threadId, val, btn) {
        var list = $('.comment-list-' + threadId);

        btn.html('<i class="fa fa-spin icon-journwe"></i>');

        routes.controllers.api.json.CommentController.saveComment().ajax({data: {text: val, threadId: threadId}, success: function (f) {
            list.find(".empty").remove();

            f.time = utils.formatTime(f.comment.timestamp);
            f.color = utils.colorOfUser(f.user.name);
            list.append(tmpl('template-comment', f)).fadeIn();
            btn.html('<i class="fa fa-plus"></i>');
        }});
    };



    utils.on({
        'click .btn-comment-add': function () {
            var val,
                threadId = $(this).data('thread'),
                input = $(this).closest('.comment-input').find('input:text');

            // Check if there is a value
            if (val = (input.val() || "").trim()) {
                input.val(""); // Reset
                addComment(threadId, val, $(this));
            }
        },
        'keyup .comment-input input:text': function (e) {
            if (e.keyCode == 13) {
                $(this).closest('.comment-input').find('.btn-comment-add').click();
            }
        }
    });

    // Load all comment lists
    $('.comment-list').each(function (idx, list) {
        refreshComments($(list).data('thread'), $(list))
    });
});