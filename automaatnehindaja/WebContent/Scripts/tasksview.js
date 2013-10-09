window.onload = function(){
	jQuery.getJSON("Taskstable", function(data) {
		$("#tasksViewLoader").css("display", "none");
		if (data.role == "tudeng"){
			tableCreate(data.id, data.name, data.deadline, data.result);
		}
		else if (data.role == "admin"){
			tableCreate2(data.id, data.name, data.deadline, data.resultCount, data.successCount);
			$('<a>').attr("href","new_task.html").html("<button>Lisa ülesanne</button>").appendTo($("#buttonDiv"));
		}
	});	
	
};


function tableCreate(idList, nameList, deadlineList, resultList){
	var tableDiv = document.getElementById("tableDiv");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "tasksTable";
	var row = document.createElement("tr");
	jQuery("<th />").text("Ulesanne").appendTo(row);
	jQuery("<th />").text("Tahtaeg").appendTo(row);
	jQuery("<th />").text("Tulemus").appendTo(row);
	table.appendChild(row);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		var cell = document.createElement("td");
		cell.innerHTML = "<a href = taskview.html?id="+ idList[i] + ">" + nameList[i] + "</a>";
		row.appendChild(cell);
		jQuery("<td />").text(deadlineList[i]).appendTo(row);
		jQuery("<td />").text(resultList[i]).appendTo(row);
		table.appendChild(row);
	}
	table.setAttribute("class", "tableclass");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);
}

function tableCreate2(idList, nameList, deadlineList, resultCount, successCount){
	var tableDiv = document.getElementById("tableDiv");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "tasksTable";
	var row = document.createElement("tr");
	jQuery("<th />").text("Ulesanne").appendTo(row);
	jQuery("<th />").text("Tahtaeg").appendTo(row);
	jQuery("<th />").text("Esitanuid").appendTo(row);
	jQuery("<th />").text("Õnnestujaid").appendTo(row);
	table.appendChild(row);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		var cell = document.createElement("td");
		cell.innerHTML = "<a href = taskview.html?id="+ idList[i] + ">" + nameList[i] + "</a>";
		row.appendChild(cell);
		jQuery("<td />").text(deadlineList[i]).appendTo(row);
		jQuery("<td />").text(resultCount[i]).appendTo(row);
		jQuery("<td />").text(successCount[i]).appendTo(row);
		table.appendChild(row);
	}
	table.setAttribute("class", "tableclass");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);
}