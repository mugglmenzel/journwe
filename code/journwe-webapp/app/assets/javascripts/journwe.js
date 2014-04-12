
define([
    "common/utils",
    "config"
], function (utils) {
    // Init drop behaviour
    jQuery.event.props.push('dataTransfer');
    $(window).bind('drop', function (event) {
        event.stopPropagation();
        event.preventDefault();
        return false;
    });
    $(window).bind('dragover', function (event) {
        event.stopPropagation();
        event.preventDefault();
        return false;
    });

    // Init x-editable
    $.fn.editable.defaults.mode = 'inline';
    $.fn.editableform.loading = '<div class="loader"></div>';
    $('.editable').editable();

    // After saving textareas via x-editable, replace links
    $.extend($.fn.editabletypes.textarea.prototype, {
        v2h: $.fn.editabletypes.textarea.prototype.value2html,
        value2html: function (foo, element) {
            this.v2h.apply(this, arguments);
            $(element).html(utils.replaceURLWithHTMLLinks($(element).html()));
        }
    });

    // Init footer tooltip
    $('.social-icons li a').tooltip();

    // Init bootstrap switch
    $('input[type="checkbox"],[type="radio"]').not('.star').bootstrapSwitch();

    // Flash Modals
    $('.modal-auto-load').modal();

    console.log("journwe basic layer loaded and configured.");


    return function (call) {
        call();
    }
});