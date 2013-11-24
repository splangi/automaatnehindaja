function getCourses(){
	jQuery.getJSON("getcoursenames", function(data){
		var courses = data.coursenames;
		for (var i = 0; i<courses.length; i++){
			var course = courses[i];
			$('#courses').append($("<option></option>").attr("value",course).text(course));
		};
	});
};

getCourses();

function getTasks(){
	var course = $('#courses>option:selected').text();
	jQuery.getJSON("Taskstable?course=" +course, function(data) {
		$('#taskname option').remove();
		$('#taskname').append($("<option></option>"));
		for (var i = 0; i<data.id.length; i++){
			$('#taskname').append($("<option></option>").attr("value",data.id[i]).text(data.name[i]));
		};	
	});	
}

function openOverlay(){
	$("#overlay").css("display", "block");
}

function closeOverlay(){
	$("#overlay").css("display", "none");
}


function post(){
	var info = document.getElementById("info");
	var coursename = document.getElementById("courses");
	var taskname = document.getElementById("taskname");
	if (($('#courses>option:selected').text() != "") && ($('#taskname>option:selected').text() != "")){
		
		alertify.set({ labels: {
		    ok     : "Jah",
		    cancel : "Ei"
		} });
		
		alertify.confirm("Hoiatus, See on tagasipöördumatu toiming! Kas olete kindel et soovite seda teha? ", function (e) {
		    if (e) {
		        // user clicked "ok"
		    	console.log("clicked OK");
		    	var post = $.post("closeAttempts", {coursename: coursename.value, taskid: taskname.value}, function(data){
					info.innerHTML = data;
					coursename.value = "";
					taskname.value = "";
				});
		    } else {
		        // user clicked "cancel"
		    	console.log("clicked cancel");
		    }
		});
	}
	else{
		info.innerHTML = "Kursuse või ülesande nimi valimata!";
	}
		
}