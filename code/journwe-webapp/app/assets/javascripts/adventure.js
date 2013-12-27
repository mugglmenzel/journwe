require([
	"main", 
	"utils"
], function(main, utils){


	utils.on({

		// Click on navigation to scroll to it
		'click .nav-adventure a': function(){

			var section = $(this).attr('href');

			$('html, body').animate({
				scrollTop : $(section).offset().top - 100
			}, 'slow');

			return false;
		}
	})



	// $('body').scrollspy({offset: 100}).on('activate', function(evt){
	// 	$('.row.active').removeClass('active');
	// 	$($(evt.target).find('a').attr('href')).parent('.row').addClass('active');
	// });


});