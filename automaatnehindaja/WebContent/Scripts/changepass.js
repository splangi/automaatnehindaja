$("#changePassForm").submit(function() {
	var oldPass = $( "[name='oldPass']" )[0].value;
	var oldPassHash = (CryptoJS.SHA1(oldPass)).toString();
	var newPass1 = $( "[name='newPass1']" )[0].value;
	var newPass2 = $( "[name='newPass2']" )[0].value;
	var newPassHash = (CryptoJS.SHA1(newPass1)).toString();
	
	if (oldPass === "" || newPass1 === "" || newPass2 === "") {
		$("#message").text("Täida kõik lahtrid!");
	}
	
	else if (newPass1 !== newPass2) {
		$("#message").text("Salasõnad ei kattu!");
	}
	else {
		$.post( "ChangePassServlet", { oldPass : oldPassHash, newPass : newPassHash }, function( data ) { 
			if (data === "success") {
				$("#message").text("Salasõna muudetud!").css("color", "green");
			}
			else if (data === "wrongPass") {
				$("#message").text("Vale salasõna!");
			};
		});
	}
	
	event.preventDefault();
});
