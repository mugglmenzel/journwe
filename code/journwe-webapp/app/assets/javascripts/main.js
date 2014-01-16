/**
 * main.scala.html
 */

require(['utils'], function(utils){

    var lastScrollTop = 0,
        bgr = $('#background'),
        adv = $('#adventure-container');


    // Set background image
    var images = ['prekestollen.jpg', 'kilimanjaro.jpg', 'daftpunkalive.jpg'];
    if (!bgr.css('background-image') || bgr.css('background-image') == "none"){
        bgr.css('background-image', 'url("http://i.embed.ly/1/image/resize?width=1600&key=2c8ef5b200c6468f9f863bc75c46009f&url=http%3A%2F%2Fwww.journwe.com%2Fassets%2Fimg%2Fbg%2F' + images[Math.floor(Math.random() * images.length)] + '")');
    }

    // Set parallex scrolling

    var requestAnimFrame = (function(){
      return  window.requestAnimationFrame       ||
              window.webkitRequestAnimationFrame ||
              window.mozRequestAnimationFrame    ||
              function( callback ){
                window.setTimeout(callback, 1000 / 60);
              };
    })();

    var cancelAnimationFrame = (function(){
      return  window.cancelAnimationFrame || 
              window.mozCancelAnimationFrame ||
              function( fn ){
                window.clearTimeout(fn);
              };
    })();

    var timer, top = 140;
    $(window).scroll(function(event){
        cancelAnimationFrame(timer);
        timer = requestAnimFrame(function(){

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
            //     bgr.css('transform', 'translateY(-'+Math.min(9, Math.max(0, (st/offsetHeight)*9 ))+'%)');
            // }
            if ((st > top && lastScrollTop <= top) || (st <= top && lastScrollTop > top)){
                adv.attr("class", st > top ? "fixed" : "");
            }  
            lastScrollTop = st;
        }.bind(this));
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