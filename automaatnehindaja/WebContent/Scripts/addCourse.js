function addcourse(){
	var coursename = document.getElementById("coursename");
	var info = document.getElementById("info");
	if (coursename.value.length > 0){
		var post = $.post("addCourse", {coursename: coursename.value}, function(data){
			info.innerHTML = data;
			coursename.value = "";
		});
	}
	else{
		info.innerHTML = "Palun sisestage kursuse nimi";
	}
	
	
}