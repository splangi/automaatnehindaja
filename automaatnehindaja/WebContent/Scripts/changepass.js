function checkPassword() {
	
	var oldPass = $( "[name='oldPass']" )[0].value;
	var newPass1 = $( "[name='newPass1']" )[0].value;
	var newPass2 = $( "[name='newPass2']" )[0].value;
	
	if (oldPass === "" || newPass1 === "" || newPass2 === "") {
		$("#message").text("Täida kõik lahtrid!");
	}
	
	else if (newPass1 !== newPass2) {
		$("#message").text("Salasõnad ei kattu!");
	}
	else {
		$.post( "ChangePassServlet", { oldPass : oldPass, newPass1: newPass1, newPass2: newPass2 }, 
				function( data ) { 
			if (data === "success") {
				$("#message").text("Salasõna muudetud!").css("color", "green");
			}
			else if (data === "wrongPass") {
				$("#message").text("Vale salasõna!");
			}
		});
	}
}
