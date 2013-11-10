
	
function changeCourse(){
	var course = $("#courses option:selected").val();
	var archived = $("#archived").is(":checked");
	jQuery.getJSON("resulttable?course=" + course + "&archived=" + archived, function(data) {
		$("#resultsLoader").css("display", "none");
		tableCreate(data.fullname, data.taskname, data.time, data.result, data.language, data.id, data.course);
	});	
};

function getCourses(){
	var archived = $("#archived").is(":checked");
	jQuery.getJSON("getcoursenames?archived=" + archived , function(data){
		var courses = data.coursenames;
		//TODO select the one which was lastly selected
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
		changeCourse();
	});
};

function init(){
	getCourses();
}

function tableCreate(nameList, tasknameList, deadlineList, resultList, languageList, idList, courseList){
	var tableDiv = document.getElementById("attempts");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "attemptTable";
	var head = document.createElement("thead");
	var row = document.createElement("tr");
	jQuery("<th />").text("Nimi").appendTo(row);
	jQuery("<th />").text("Ülesanne").appendTo(row);
	jQuery("<th />").text("Tähtaeg").appendTo(row);
	jQuery("<th />").text("Tulemus").appendTo(row);
	jQuery("<th />").text("Programeerimiskeel").appendTo(row);
	table.appendChild(head);
	head.appendChild(row);
	var body = document.createElement("tbody");
	table.appendChild(body);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		jQuery("<td />").text(nameList[i]).appendTo(row);
		var cell = document.createElement("td");
		cell.innerHTML = "<a href = #taskview.html?id="+ idList[i] + ">" + tasknameList[i] + "</a>";
		row.appendChild(cell);
		jQuery("<td />").text(deadlineList[i]).appendTo(row);
		jQuery("<td />").text(resultList[i]).appendTo(row);
		jQuery("<td />").text(languageList[i]).appendTo(row);
		body.appendChild(row);
	}
	table.setAttribute("class", "tablesorter");
	table.setAttribute("id", "attemptTable");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);
	
	$.getScript("Scripts/jquery.tablesorter.min.js", function() {
		$("#attemptTable").tablesorter( { sortList: [[0,0]] } ); 
	});
}

init();
