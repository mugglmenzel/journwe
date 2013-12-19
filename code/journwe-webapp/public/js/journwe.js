

$('body').ready(function(){
    
    var lastScrollTop = 0,
        bgr = $('#background');

    var images = ['prekestollen.jpg', 'kilimanjaro.jpg', 'daftpunkalive.jpg'];
    bgr.css('background-image', 'url("http://i.embed.ly/1/image/resize?width=1600&key=2c8ef5b200c6468f9f863bc75c46009f&url=http%3A%2F%2Fwww.journwe.com%2Fassets%2Fimg%2Fbg%2F' + images[Math.floor(Math.random() * images.length)] + '")');


    $(window).scroll(function(event){
        var st = $(this).scrollTop();
        // if (st > 200 && st > lastScrollTop){
        //     $('.navbar-fixed-top').slideUp('fast');
        // } else {
        //     $('.navbar-fixed-top').slideDown('fast');
        // }
        // lastScrollTop = st;

        var offsetHeight = $(document).height() - $(this).height();
        if (!new RegExp("Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini", "i").test(navigator.userAgent)){
           bgr.css('transform', 'translateY(-'+Math.min(9, Math.max(0, (st/offsetHeight)*9 ))+'%)');
        }

        $('#adventure-container').attr("class", st > 126 ? "fixed" : "");

    });

    $('#navbar-secondlevel-collapsable .collapse').collapse({
        toggle: false
    });
});

// Formats a date to hh:mm dd.MM.YYYY
function formatTime(time){
    time = time instanceof Date ? time : new Date(parseInt(time, 10));

    var two = function(r){ return (r < 10 ? "0" : "")+r },
        now = new Date(),
        today = now.getDate() == time.getDate()
                && now.getMonth() == time.getMonth()
                && now.getFullYear() == time.getFullYear();

    if (today){
        return time.getHours()
            + ":"
            + two(time.getMinutes());
    } else {
        return formatDateShort(time);
    }
};

// Formats a date to hh:mm dd.MM.YYYY
function formatDate(time){
    time = time instanceof Date ? time : new Date(parseInt(time, 10));

    var two = function(r){ return (r < 10 ? "0" : "")+r };

    // Todo Use better formating
    return two(time.getDate())
        + "."
        + two(time.getMonth()+1)
        + "."
        + time.getFullYear();
};

function formatDateShort(time){
    time = time instanceof Date ? time : new Date(parseInt(time, 10));

    var two = function(r){ return (r < 10 ? "0" : "")+r };

    // Todo Use better formating
    return two(time.getDate())
        + "."
        + two(time.getMonth()+1)
        + "."
        + (""+time.getYear()).substr(1,2);
};

function replaceURLWithHTMLLinks(text) {
    var exp = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
    return text.replace(exp,"<a href='$1'>$1</a>");
}