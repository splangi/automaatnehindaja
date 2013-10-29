$(document).ready(function() {
	$.getJSON("LogsServlet", function( data ) {
		$("#tasksViewLoader").css("display","none");
		
		console.log(data);
		var len = data.message.length;
		
		for (var i = 0; i < len; i++){
			var level = data.level[(len - 1) - i];
			var date = data.date[(len - 1) - i];
			var msg = data.message[(len - 1) - i];
			$("#logTable").append(level + ": " + date + " - " + msg + "</br>" );
		}
		
	} );
});
