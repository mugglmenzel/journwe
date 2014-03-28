/**
 * the app
 */


requirejs.config({
    paths: {
        'jquery': '//cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min',
        'bootstrap': 'common/bootstrap',
        'bootstrap-switch': '//cdnjs.cloudflare.com/ajax/libs/bootstrap-switch/2.0.0/js/bootstrap-switch.min',
        'bootstrap-editable': '//cdnjs.cloudflare.com/ajax/libs/x-editable/1.5.0/bootstrap3-editable/js/bootstrap-editable.min',
        'bootstrap-datepicker': '//cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.1.3/js/bootstrap-datepicker.min',
        'typeahead': '//cdnjs.cloudflare.com/ajax/libs/typeahead.js/0.9.3/typeahead.min',
        'tmpl': 'common/tmpl'
    },
    shim: {
        'bootstrap': ['jquery'],
        'bootstrap-switch': ['bootstrap'],
        'bootstrap-editable': ['bootstrap'],
        'bootstrap-datepicker': ['bootstrap'],
        'typeahead': ['jquery'],
        'tmpl': {
            exports: 'tmpl'
        }
    },
    waitSeconds: 15
});

define([
    "common/utils",
    "tmpl",
    "jquery",
    "bootstrap",
    "bootstrap-switch",
    "bootstrap-editable",
    "bootstrap-datepicker",
    "typeahead"
], function (utils, tmpl) {
    window.tmpl = tmpl;

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

    // Init footer tooltip
    $('.social-icons li a').tooltip();

    // Init bootstrap switch
    $('input[type="checkbox"],[type="radio"]').not('.star').bootstrapSwitch();

    // Flash Modals
    $('.modal-auto-load').modal();

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


    return function (call) {
        call();
    }
});