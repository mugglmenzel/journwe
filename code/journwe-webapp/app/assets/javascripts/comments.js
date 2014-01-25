define([
    "utils",
    "routes",
    "messages"
], function (utils, routes, messages) {


    var refreshComments = function (threadId, list) {
        routes.controllers.api.json.CommentController.listComments(threadId).ajax({success: function (res) {
            list.html("");
            res.forEach(function (f) {
                f.time = utils.formatTime(f.comment.timestamp); // Append
                list.append(tmpl('template-comment', f)).fadeIn();
            });

            if (!res.length) {
                list.append('<div class="empty"><i class="fa fa-envelope"></i> '+messages["adventure.widgetComments.firstComment"]+'</div>');
            }
        }});
    }

    var addComment = function (threadId, val, btn) {
        var list = $('.comment-list-' + threadId);

        btn.html('<i class="fa fa-spin icon-journwe"></i>');

        routes.controllers.api.json.CommentController.saveComment().ajax({data: {text: val, threadId: threadId}, success: function (f) {
            list.find(".empty").remove();

            f.time = utils.formatTime(f.comment.timestamp); // Append
            list.append(tmpl('template-comment', f)).fadeIn();
            btn.html('<i class="fa fa-plus"></i>');
        }});
    }

    utils.on({
        'click .btn-comment-add': function () {
            var val,
                threadId = $(this).data('thread'),
                input = $(this).closest('.input-comment').find('input:text');

            // Check if there is a value
            if (val = (input.val() || "").trim()) {
                input.val(""); // Reset
                addComment(threadId, val, $(this));
            }
        },
        'keyup .comment-input input:text': function (e) {
            if (e.keyCode == 13) {
                $(this).closest('.input-comment').find('.btn-comment-add').click();
            }
        }
    });

    // Load all comment lists
    $('.comment-list').each(function(idx,list){refreshComments($(list).data('thread'), $(list))});



    // <script id="template-wall" type="text/x-tmpl">
    //     <div>
    //         <img class="pull-left" style="margin-right: 10px; max-height: 24pt;" src="{%=o.user.image%}">
    //         <div class="comment-text"><i class="fa fa-comment"></i> {%=o.comment.text%}</div>
    //         <div class="comment-info">{%=o.user.name%} <small>-</small> {%=o.time%}</div>
    //     </div>
    // </script>


    // <script>

    // $(function(){

    // var adventureId = "@adv.getId()",
    //     topic = ("@topic"+""),
    //     threadId = adventureId+topic,
    //     input = $("#input-comment-@topic input"),
    //     btn = $("#btn-add-comment-@topic");

    // // The container for all comments
    // var list = $("#comments-list-@topic");


    // // Perform GET
    // $.get('@api.json.routes.CommentController.listComments("")'+threadId, function(res){
    //     list.html("");
    //     res.forEach(function(f){
    //         f.time = formatTime(f.comment.timestamp); // Append
    //         list.append(tmpl('template-wall', f)).fadeIn();
    //     });

    //     if (!res.length){
    //         list.append('<div class="empty"><i class="fa fa-envelope"></i> @Messages("adventure.wallComments.firstComment")</div>');
    //     }
    // });

    // // Perform POST
    // btn.click(function(){
    //     var val;

    //     // Check if there is a value
    //     if (val = (input.val()||"").trim()){
    //         btn.html('<i class="fa fa-spin icon-journwe"></i>');
    //         input.val(""); // Reset

    //         $.post("@api.json.routes.CommentController.saveComment()", {text: val, threadId: threadId}, function(f){
    //             list.find(".empty").remove();

    //             f.time = formatTime(f.comment.timestamp); // Append
    //             list.append(tmpl('template-wall', f)).fadeIn();
    //             btn.html('<i class="fa fa-plus"></i>');
    //         });
    //     }
    // });

    // input.keyup(function(e) {
    //     if(e.keyCode == 13) {
    //         btn.click();
    //     }
    // });


    // });
    // </script>

});