$(document).ready(function() {
	
	$( "#resetPass" ).submit(function() {
		var mail = $( "[name='mail']" )[0].value;
		
		if (mail === "") {
			$("#message").css("color","red");
			$("#message").text("Lahter on täitmata!");
		}
		else {
			$.post( "resetPass", { mail : mail }, function( data ) {
				if (data === "success") {
					$("#message").css("color","green");
					$("#message").text("Saadetud!");
				}
				else {
					$("#message").css("color","red");
					$("#message").text("Kasutajat ei eksisteeri");
				}
			});
		}

		event.preventDefault();
	});
	
});

