/**
 * main.scala.html
 */

require(['common/utils', 'https://cdnjs.cloudflare.com/ajax/libs/bootstrap-switch/2.0.0/js/bootstrap-switch.min.js'], function (utils) {

    var lastScrollTop = 0,
        bgr = $('#background'),
        adv = $('#adventure-container');


    // Set background image
    var images = ['kilimanjaro.jpg', 'cinque_terre.jpg', 'tokyo_tower.jpg', 'camper_beach.jpg', 'lake_sailing.jpg']; //  'tokyo_drums.jpg', 'arirang.jpg',
    if (!bgr.css('background-image') || bgr.css('background-image') == "none") {
        bgr.css('background-image', 'url("http://i.embed.ly/1/image/resize?width=1600&key=2c8ef5b200c6468f9f863bc75c46009f&url=http%3A%2F%2Fwww.journwe.com%2Fassets%2Fimg%2Fbg%2F' + images[Math.floor(Math.random() * images.length)] + '")');
    }


    // Set parallex scrolling
    if ($(".nav-adventure").length) {
        var top;
        $(window).scroll($.throttle(0, function (event) {
            if ("undefined" == typeof top) {
                top = $(".nav-adventure").offset().top + 1;
            }

            var st = $(this).scrollTop();
            // if (st > 200 && st > lastScrollTop){
            //     $('.navbar-fixed-top').slideUp('fast');
            // } else {
            //     $('.navbar-fixed-top').slideDown('fast');
            // }
            // 

            // TODO: Optimize
            // var offsetHeight = $(document).height() - $(this).height();
            // if (!utils.isMobile()){
            //     bgr.css('transform', 'translate3d(0, -'+Math.min(9, Math.max(0, (st/offsetHeight)*9 ))+'%, 0)');
            // }

            if ((st > top && lastScrollTop <= top) || (st <= top && lastScrollTop > top)) {
                adv.attr("class", st > top ? "fixed" : "");
            }
            lastScrollTop = st;
        }.bind(this))
        );
    }


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

    //Flash Modals
    $('.modal-auto-load').modal();

});