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
	if (($('#courses>option:selected').text() != "")){
		
		alertify.set({ labels: {
		    ok     : "Jah",
		    cancel : "Ei"
		} })
		
		alertify.confirm("Hoiatus, See on tagasipöördumatu toiming! Kas olete kindel et soovite seda teha? ", function (e) {
		    if (e) {
		        console.log("clicked OK");
		    	var post = $.post("closeCourse", {coursename: coursename.value}, function(data){
					info.innerHTML = data;
					coursename.value = "";
				});
		    } else {
		       console.log("clicked cancel");
		    }
		});
	}
	else{
		info.innerHTML = "Kursus valimata";
	}
		
}