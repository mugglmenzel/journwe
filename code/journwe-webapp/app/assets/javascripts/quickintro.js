require([
    'common/utils'
], function (utils) {

    utils.on({
        'slid.bs.carousel #quickintro-explain': function () {
            $('.nav-quickintro li').removeClass('active');
            $('.nav-quickintro li.' + $('#quickintro-explain .carousel-inner .active').data('tab-class')).addClass('active');
        }
    });

});