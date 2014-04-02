define([
    "common/tmpl",
    "common/bootstrap",
    "common/bootstrap-switch",
    "common/bootstrap-editable",
    "common/bootstrap-datepicker",
    "common/typeahead"
], function (tmpl) {
    window.tmpl = tmpl;
    jQuery.event.props.push('dataTransfer');
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

    console.log("libraries loaded and configured.");

});