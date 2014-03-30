
define([
    "common/utils"
], function (utils) {
    // Init drop behaviour
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

    console.log("journwe basic layer loaded and configured.");


    return function (call) {
        call();
    }
});