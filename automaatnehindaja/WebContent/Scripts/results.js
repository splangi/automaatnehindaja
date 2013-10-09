window.onload = function(){
	jQuery.getJSON("resulttable", function(data) {
		$("#tasksViewLoader").css("display", "none");
		tableCreate(data.fullname, data.taskname, data.time, data.result, data.language, data.id);
	});	
	
};

function tableCreate(nameList, tasknameList, deadlineList, resultList, languageList, idList){
	var tableDiv = document.getElementById("attempts");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "attemptTable";
	var row = document.createElement("tr");
	jQuery("<th />").text("Nimi").appendTo(row);
	jQuery("<th />").text("Ulesanne").appendTo(row);
	jQuery("<th />").text("Tahtaeg").appendTo(row);
	jQuery("<th />").text("Tulemus").appendTo(row);
	jQuery("<th />").text("Programeerimiskeel").appendTo(row);
	table.appendChild(row);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		jQuery("<td />").text(nameList[i]).appendTo(row);
		var cell = document.createElement("td");
		cell.innerHTML = "<a href = taskview.html?id="+ idList[i] + ">" + tasknameList[i] + "</a>";
		row.appendChild(cell);
		jQuery("<td />").text(deadlineList[i]).appendTo(row);
		jQuery("<td />").text(resultList[i]).appendTo(row);
		jQuery("<td />").text(languageList[i]).appendTo(row);
		table.appendChild(row);
	}
	table.setAttribute("class", "tableclass");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);
}