function getCourses(){
	jQuery.getJSON("getcoursenames", function(data){
		var courses = data.coursenames;
		for (var i = 0; i<courses.length; i++){
			var course = courses[i];
			$('#courses').append($("<option></option>").attr("value",course).text(course));
		};
	});
};

function getStudents(){
	jQuery.getJSON("getusers", function(data){
		var students = data.usernames;
		for (var i = 0; i<students.length; i++){
			var student = students[i];
			$('#students').append($("<option></option>").attr("value",student).text(student));
		};
	});
};

getCourses();
getStudents();

function post(){
	var info = document.getElementById("info");
	var coursename = document.getElementById("courses");
	var username = document.getElementById("students");
	if (($('#courses>option:selected').text() != "") && ($('#students>option:selected').text() != "")){
		var post = $.post("addtocourse", {coursename: coursename.value, username: username.value}, function(data){
			info.innerHTML = data;
			coursename.value = "";
			username.value = "";
		});
		
	}
	else{
		info.innerHTML = "Kursuse või kasutajanimi valimata!";
	}
		
}