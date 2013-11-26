function init(){	
	console.log("hello");
	if (getUrlVars()["archived"] === "true"){
		$("#archived").attr("checked", true);
	}
	getCourses();
}

function change(){
	window.location.hash = "#studentsView?course=" + $("#courses :selected").val() +"&archived="+$("#archived").is(":checked");
	
}

function fillUpTasks(course){
	archived = $("#archived").is(":checked");
	jQuery.getJSON("StudentsTable", {course: course}, function(data) {
		$("#tasksViewLoader").css("display", "none");
		tableCreate(data.name, data.attemptCount);
	});
}

function tableCreate(name, count){
	var tableDiv = document.getElementById("tableDiv");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	var head = document.createElement("thead");
	var row = document.createElement("tr");
	jQuery("<th />").text("Tudeng").appendTo(row);
	jQuery("<th />").text("Soorituste arv").appendTo(row);
	table.appendChild(head);
	head.appendChild(row);
	var body = document.createElement("tbody");
	table.appendChild(body);
	for (var i = 0; i < name.length; i++){
		row = document.createElement("tr");
		jQuery("<td />").text(name[i]).appendTo(row);
		jQuery("<td />").text(count[i]).appendTo(row);
		body.appendChild(row);
	}
	table.setAttribute("class", "tablesorter");
	table.setAttribute("id", "studentTable");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);

	$.getScript("Scripts/jquery.tablesorter.min.js", function() {
		$("#studentTable").tablesorter( { sortList: [[0,0]] } ); 
	});
}

function getCourses(){
	jQuery.getJSON("getcoursenames?archived=" + $("#archived").is(":checked") , function(data){
		var courses = data.coursenames;
		
		if (courses == null) {
			$("#tasksViewLoader").css("display", "none");
		}
		else {
			if ($.inArray(getUrlVars()["course"], courses)>-1){
				fillUpTasks(courses[$.inArray(getUrlVars()["course"], courses)]);
			}
			else if (courses.length > 0){
				fillUpTasks(courses[0]);
			}
			$('#courses option').remove();
			for (var i = 0; i<courses.length; i++){
				var course = courses[i];
				if (data.active[i] === true){
					$('#courses').append($("<option></option>").attr("value",course).text(course));
				}
				else {
					$('#courses').append($("<option></option>").attr("value",course).text(course + " (arhiveeritud)"));
				}
			};
			if (getUrlVars()["course"] !== undefined){
				$('#courses option:eq('+ $.inArray(getUrlVars()["course"], courses) + ')').prop('selected', true);
			}
		}
	});
};

function getUrlVars() {
    var vars = {};
    window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
}

init();