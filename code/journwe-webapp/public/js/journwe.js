


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