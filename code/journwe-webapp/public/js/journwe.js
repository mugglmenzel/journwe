var two = function(r){ return (r < 10 ? "0" : "")+r; };
function formatDate(time){
    time = time instanceof Date ? time : new Date(parseInt(time, 10));

    // Todo Use better formating
    return two(time.getDate())
        + "."
        + two(time.getMonth()+1)
        + "."
        + time.getFullYear();
};