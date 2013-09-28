window.onload = function(){
	var tasks = new Array();
	var deadlines = new Array();
	var id = new Array();
	jQuery.getJSON("Taskstable", function(data) {
		tableCreate(data.id, data.name, data.deadline);
	});	
	
};


function tableCreate(idList, nameList, deadlineList){
	var tableDiv = document.getElementById("tableDiv");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "tasksTable";
	var row = document.createElement("tr");
	jQuery("<th />").text("Ulesanne").appendTo(row);
	jQuery("<th />").text("Tahtaeg").appendTo(row);
	table.appendChild(row);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		var cell = document.createElement("td");
		var b = (i+1).toString();
		cell.innerHTML = "<a href = taskview.html?id="+ idList[i] + ">" + nameList[i] + "</a>";
		row.appendChild(cell);
		jQuery("<td />").text(deadlineList[i]).appendTo(row);
		table.appendChild(row);
	}
	table.setAttribute("class", "tableclass");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);
}