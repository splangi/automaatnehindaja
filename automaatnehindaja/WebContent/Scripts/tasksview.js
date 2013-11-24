function init(){	
	if (getUrlVars()["archived"] === "true"){
		$("#archived").attr("checked", true);
	}
	getCourses();
}

function change(){
	var prefix = "";
	if (window.location.hash.indexOf("#tasksview")>-1){
		prefix = "#tasksview";
	}
	else if (window.location.hash.indexOf("#changeTask")>-1){
		prefix = "#changeTask";
	}
	window.location.hash = prefix + "?course=" + $("#courses :selected").val() +"&archived="+$("#archived").is(":checked");
}

function fillUpTasks(course){
	archived = $("#archived").is(":checked");
	jQuery.getJSON("Taskstable", {course: course, archived: archived}, function(data) {
		$("#tasksViewLoader").css("display", "none");
		if (data.role == "tudeng"){
			tableCreate(data.id, data.name, data.deadline, data.result, data.active);
		}
		else if (data.role == "admin" || data.role == "responsible"){
			if (window.location.hash.indexOf("#tasksview")>-1){
				tableCreate2(data.id, data.name, data.deadline, data.resultCount, data.successCount, data.active);
			}
			else if (window.location.hash.indexOf("#changeTask")>-1){
				tableCreate3(data.id, data.name, data.deadline, data.active);
			}
		}
	});	
}

function getCourses(){
	jQuery.getJSON("getcoursenames?archived=" + $("#archived").is(":checked") , function(data){
		var courses = data.coursenames;
		
		if (courses == null) {
			$("#tasksViewLoader").css("display", "none");
		}
		else {
			//TODO select the one which was lastly selected
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

function tableCreate(idList, nameList, deadlineList, resultList, archiveList){
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
		if (archiveList[i] === true ){
			cell.innerHTML = '<a href="#taskview.html?id=' + idList[i] + '">' + nameList[i] + "</a>";
		}
		else{
			cell.innerHTML = '<a href="#taskview.html?id=' + idList[i] + '">' + nameList[i] + " (arhiveeritud)" + "</a>";
		}
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

function tableCreate2(idList, nameList, deadlineList, resultCount, successCount, archiveList){
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
		if (archiveList[i] === true ){
			cell.innerHTML = '<a href="#taskview.html?id=' + idList[i] + '">' + nameList[i] + "</a>";
		}
		else{
			cell.innerHTML = '<a href="#taskview.html?id=' + idList[i] + '">' + nameList[i] + " (arhiveeritud)" + "</a>";
		}
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

function tableCreate3(idList, nameList, deadlineList, archiveList){
	var tableDiv = document.getElementById("tableDiv");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "tasksTable";
	var head = document.createElement("thead");
	var row = document.createElement("tr");
	jQuery("<th />").text("Ülesanne").appendTo(row);
	jQuery("<th />").text("Tähtaeg").appendTo(row);
	table.appendChild(head);
	head.appendChild(row);
	var body = document.createElement("tbody");
	table.appendChild(body);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		var cell = document.createElement("td");
		if (archiveList[i] === true ){
			cell.innerHTML = '<a href="#changeTaskView.html?id=' + idList[i] + '">' + nameList[i] + "</a>";
		}
		else{
			cell.innerHTML = '<a href="#changeTaskView.html?id=' + idList[i] + '">' + nameList[i] + " (arhiveeritud)" + "</a>";
		}
		row.appendChild(cell);
		jQuery("<td />").text(deadlineList[i]).appendTo(row);
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

function getUrlVars() {
    var vars = {};
    window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
}

init();