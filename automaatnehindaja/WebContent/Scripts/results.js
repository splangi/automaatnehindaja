$(document).ready(initialize());
	
function initialize(){
	jQuery.getJSON("resulttable", function(data) {
		$("#resultsLoader").css("display", "none");
		tableCreate(data.fullname, data.taskname, data.time, data.result, data.language, data.id, data.course);
	});	
	
};

function tableCreate(nameList, tasknameList, deadlineList, resultList, languageList, idList, courseList){
	var tableDiv = document.getElementById("attempts");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "attemptTable";
	var row = document.createElement("tr");
	jQuery("<th />").text("Nimi").appendTo(row);
	jQuery("<th />").text("Ülesanne").appendTo(row);
	jQuery("<th />").text("Kursus").appendTo(row);
	jQuery("<th />").text("Tähtaeg").appendTo(row);
	jQuery("<th />").text("Tulemus").appendTo(row);
	jQuery("<th />").text("Programeerimiskeel").appendTo(row);
	table.appendChild(row);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		jQuery("<td />").text(nameList[i]).appendTo(row);
		var cell = document.createElement("td");
		cell.innerHTML = "<a href = #taskview.html?id="+ idList[i] + ">" + tasknameList[i] + "</a>";
		row.appendChild(cell);
		jQuery("<td />").text(courseList[i]).appendTo(row);
		jQuery("<td />").text(deadlineList[i]).appendTo(row);
		jQuery("<td />").text(resultList[i]).appendTo(row);
		jQuery("<td />").text(languageList[i]).appendTo(row);
		table.appendChild(row);
	}
	table.setAttribute("class", "tableclass");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);
}