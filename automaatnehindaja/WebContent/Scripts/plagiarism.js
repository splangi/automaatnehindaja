function init(){
	getCourses();
}

function change(){
	window.location.hash = "#plagiarism?course=" + $("#courses :selected").val();
}
	
function changeCourse(course){
	$("#resultsLoader").css("display", "");
	jQuery.getJSON("getPlagiarismScores?course=" + course, function(data) {
		$("#resultsLoader").css("display", "none");
		tableCreate(data.Attempt1ID, data.Attempt2ID, data.username1, data.username2, data.rating, data.time);
	});	
};

function getCourses(){
	var currentSelected = $("#courses :selected").text();
	jQuery.getJSON("getcoursenames?archived=true" , function(data){
		var courses = data.coursenames;
		
		if (courses == null) {
			$("#resultsLoader").css("display", "none");
		}
		else {
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
			if ($.inArray(getUrlVars()["course"], courses)>-1){
				changeCourse(courses[$.inArray(getUrlVars()["course"], courses)]);
			}
			else if (courses.length > 0){
				changeCourse(courses[0]);
			}
		}
	});
};

function tableCreate(Attempt1, Attempt2, username1, username2, rating, time){
	var tableDiv = document.getElementById("attempts");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "attemptTable";
	var head = document.createElement("thead");
	var row = document.createElement("tr");
	jQuery("<th />").text("1. Õpilane").appendTo(row);
	jQuery("<th />").text("2. Õplilane").appendTo(row);
	jQuery("<th />").text("Skoor").appendTo(row);
	jQuery("<th />").text("Aeg").appendTo(row);
	table.appendChild(head);
	head.appendChild(row);
	var body = document.createElement("tbody");
	table.appendChild(body);
	for (var i = 0; i < Attempt1.length; i++){
		row = document.createElement("tr");
		var cell = document.createElement("td");
		cell.innerHTML = "<a href = viewfile?id="+ Attempt1[i] + ">" + username1[i] + "</a>";
		row.appendChild(cell);
		var cell = document.createElement("td");
		cell.innerHTML = "<a href = viewfile?id="+ Attempt2[i] + ">" + username2[i] + "</a>";
		row.appendChild(cell);
		jQuery("<td />").text(rating[i]).appendTo(row);
		jQuery("<td />").text(time[i]).appendTo(row);
		body.appendChild(row);
	}
	table.setAttribute("class", "tablesorter");
	table.setAttribute("id", "plagiarismTable");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);
	
	$.getScript("Scripts/jquery.tablesorter.min.js", function() {
		$("#plagiarismTable").tablesorter( { sortList: [[0,0]] } ); 
	});

	search();
}

function getUrlVars() {
    var vars = {};
    window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
}

init();
