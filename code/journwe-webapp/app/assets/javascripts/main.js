/**
 * main.scala.html
 */

require(['utils'], function(utils){

    var lastScrollTop = 0,
        bgr = $('#background');


    // Set background image
    var images = ['prekestollen.jpg', 'kilimanjaro.jpg', 'daftpunkalive.jpg'];
    bgr.css('background-image', 'url("http://i.embed.ly/1/image/resize?width=1600&key=2c8ef5b200c6468f9f863bc75c46009f&url=http%3A%2F%2Fwww.journwe.com%2Fassets%2Fimg%2Fbg%2F' + images[Math.floor(Math.random() * images.length)] + '")');


    // Set parallex scrolling
    $(window).scroll(function(event){
        var st = $(this).scrollTop();
        // if (st > 200 && st > lastScrollTop){
        //     $('.navbar-fixed-top').slideUp('fast');
        // } else {
        //     $('.navbar-fixed-top').slideDown('fast');
        // }
        // 

        var offsetHeight = $(document).height() - $(this).height();
        if (!new RegExp("Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini", "i").test(navigator.userAgent)){
           bgr.css('transform', 'translateY(-'+Math.min(9, Math.max(0, (st/offsetHeight)*9 ))+'%)');
        }

        if ((st > 126 && lastScrollTop <= st) || (st <= 126 && lastScrollTop > st)){
        	$('#adventure-container').attr("class", st > 126 ? "fixed" : "");
    	}  
        lastScrollTop = st;
    });


    // Collapse inspiration
    $('#navbar-secondlevel-collapsable .collapse').collapse({
        toggle: false
    });


    // Init drop behaviour
    jQuery.event.props.push('dataTransfer');
    $(window).bind('drop', function(event){event.stopPropagation(); event.preventDefault(); return false;});
    $(window).bind('dragover', function(event){event.stopPropagation(); event.preventDefault(); return false;});
});