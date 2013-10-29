$(document).ready(initialize());
	
function initialize(){
	jQuery.getJSON("resulttable", function(data) {
		$("#resultsLoader").css("display", "none");
		tableCreate(data.fullname, data.taskname, data.time, data.result, data.language, data.id);
	});	
	
};

function tableCreate(nameList, tasknameList, deadlineList, resultList, languageList, idList){
	var tableDiv = document.getElementById("attempts");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "attemptTable";
	var head = document.createElement("thead");
	var row = document.createElement("tr");
	jQuery("<th />").text("Nimi").appendTo(row);
	jQuery("<th />").text("Ulesanne").appendTo(row);
	jQuery("<th />").text("Tahtaeg").appendTo(row);
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