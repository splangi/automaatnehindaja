function login() {
	
	
	//var hash = CryptoJS.SHA1("Message");
	
	var user = $( "[name='j_username']" )[0].value;
	var pass = $( "[name='j_password']" )[0].value;
	
	
	
	$.post( "/automaatnehindaja/j_security_check", { j_username : user, j_password: pass});
}
