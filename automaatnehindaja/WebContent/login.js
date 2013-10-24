$(document).ready(function() {
	
	$( "#login" ).submit(function() {
		var pass = $("[name='password']").val();
		var hash = CryptoJS.SHA1(pass);
		
		$("[name='j_password']").val(hash.toString());
		$("[name='password']").attr("name", "");
	});
	
});

