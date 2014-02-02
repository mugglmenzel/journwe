/**
 * Defines a list on utils which can 
 * be used over the whole application
 *
 */

define(function(){
    

    // Formats an int to a string with a leading zero
    var two = function(r){ return (r < 10 ? "0" : "")+r; },
        isMobile = new RegExp("Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini", "i").test(navigator.userAgent);


    return {

        /**
         * Returns the last string of the path
         * @return String
         */
        id: function(){
            return location.pathname.split("/").slice(-1)+"";
        },

        /**
         * Returns TRUE if the client is a mobile browser
         * @return Boolean
         */
        isMobile: function(){
            return isMobile;
        },

        /**
         * Helper function to specify multiple event listeners
         * @param Object 
         */
        on: function(obj){

            for( var i in obj){

                if (typeof i != "string" || typeof obj[i] != "function"){
                    continue;
                }

                var evt = i.split(" ").slice(0, 1)[0],
                    cls = i.split(" ").slice(1).join(" ");

                $(document).on(evt, cls, obj[i]);
            }
        },

        /**
         * Formats a date and return either the date or the time (if the date is today)
         * @param Date|Int
         */
        formatTime: function(time){
            time = time instanceof Date ? time : new Date(parseInt(time, 10));

            var now = new Date(),
                today = now.getDate() == time.getDate()
                        && now.getMonth() == time.getMonth()
                        && now.getFullYear() == time.getFullYear();

            if (today){
                return time.getHours()
                    + ":"
                    + two(time.getMinutes());
            } else {
                return this.formatDateShort(time);
            }
        },

        /**
         * Formats a date in the format dd.MM.YYYY
         * @param Date|Int
         */
        formatDate: function(time){
            time = time instanceof Date ? time : new Date(parseInt(time, 10));

            // Todo Use better formating
            return two(time.getDate())
                + "."
                + two(time.getMonth()+1)
                + "."
                + time.getFullYear();
        },

        /**
         * Formats a date in the format dd.MM.YY
         * @param Date|Int
         */
        formatDateShort: function(time){
            time = time instanceof Date ? time : new Date(parseInt(time, 10));

            // Todo Use better formating
            return two(time.getDate())
                + "."
                + two(time.getMonth()+1)
                + "."
                + (""+time.getYear()).substr(1,2);
        },

        /**
         * Replaces all the link from the text into anchors
         * @param String text
         * @return String
         */
        replaceURLWithHTMLLinks: function(text) {
            var exp = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
            return text.replace(exp,"<a href='$1'>$1</a>");
        },

        colorOfUser: function (userStr) {
            return ('#' + ('000000' + (parseInt(parseInt(userStr, 36).toExponential().slice(2, -5), 10) & 0xFFFFFF).toString(16).toUpperCase()).slice(-6));
        },

        setSpinning: function(el) {
            $(el).addClass('fa fa-spin');
        }                                ,
        resetSpinning: function(el) {
            $(el).removeClass('fa-spin');
        },
        setStash: function(el) {
            $(el).addClass('stash');
        },
        resetStash: function(el) {
            $(el).removeClass('stash');
        }

    };
});