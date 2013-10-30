

function init(){
	getCourses();
	
}

function changeCourse(){
	fillUpTasks($("#courses option:selected").val());
}

function fillUpTasks(course){
	console.log("fillUpTasks: " + course);
	jQuery.getJSON("Taskstable?course=" +course, function(data) {
		$("#tasksViewLoader").css("display", "none");
		if (data.role == "tudeng"){
			tableCreate(data.id, data.name, data.deadline, data.result);
		}
		else if (data.role == "admin" || data.role == "responsible"){
			tableCreate2(data.id, data.name, data.deadline, data.resultCount, data.successCount);
		}
	});	
}

function getCourses(){
	jQuery.getJSON("getcoursenames", function(data){
		console.log("getCourses: " + data);
		var courses = data.coursenames;
		if (courses.length > 0){
			fillUpTasks(courses[0]);
		}
		for (var i = 0; i<courses.length; i++){
			var course = courses[i];
			$('#courses').append($("<option></option>").attr("value",course).text(course));
		};
	});
};

function tableCreate(idList, nameList, deadlineList, resultList){
	var tableDiv = document.getElementById("tableDiv");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "tasksTable";
	var head = document.createElement("thead");
	var row = document.createElement("tr");
	jQuery("<th />").text("Ülesanne").appendTo(row);
	jQuery("<th />").text("Tähtaeg").appendTo(row);
	jQuery("<th />").text("Tulemus").appendTo(row);
	table.appendChild(head);
	head.appendChild(row);
	var body = document.createElement("tbody");
	table.appendChild(body);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		var cell = document.createElement("td");
		cell.innerHTML = '<a href="#taskview.html?id=' + idList[i] + '">' + nameList[i] + "</a>";
		row.appendChild(cell);
		jQuery("<td />").text(deadlineList[i]).appendTo(row);
		jQuery("<td />").text(resultList[i]).appendTo(row);
		body.appendChild(row);
	}
	table.setAttribute("class", "tablesorter");
	table.setAttribute("id", "tasksTable");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);

	$.getScript("Scripts/jquery.tablesorter.min.js", function() {
		$("#tasksTable").tablesorter( { sortList: [[0,0]] } ); 
	});
}

function tableCreate2(idList, nameList, deadlineList, resultCount, successCount){
	var tableDiv = document.getElementById("tableDiv");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "tasksTable";
	var head = document.createElement("thead");
	var row = document.createElement("tr");
	jQuery("<th />").text("Ülesanne").appendTo(row);
	jQuery("<th />").text("Tähtaeg").appendTo(row);
	jQuery("<th />").text("Esitanuid").appendTo(row);
	jQuery("<th />").text("Õnnestujaid").appendTo(row);
	table.appendChild(head);
	head.appendChild(row);
	var body = document.createElement("tbody");
	table.appendChild(body);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		var cell = document.createElement("td");
		cell.innerHTML = '<a href = "#taskview?id=' + idList[i] + '">' + nameList[i] + "</a>";
		row.appendChild(cell);
		jQuery("<td />").text(deadlineList[i]).appendTo(row);
		jQuery("<td />").text(resultCount[i]).appendTo(row);
		jQuery("<td />").text(successCount[i]).appendTo(row);
		body.appendChild(row);
	}
	table.setAttribute("class", "tablesorter");
	table.setAttribute("id", "tasksTable");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);
	
	$.getScript("Scripts/jquery.tablesorter.min.js", function() {
		$("#tasksTable").tablesorter( { sortList: [[0,0]] } ); 
	});
}

init();