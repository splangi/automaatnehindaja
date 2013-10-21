$(document).ready(function(){
	var id = getUrlVars()["id"];
	if (id === undefined){
		window.location = "error.html";
	}
	else{
		document.getElementById("uploadform").setAttribute("action", "upload?id=" + id);
		jQuery.getJSON("task?id=" + id, function(data) {
			$("#taskViewLoader").css("display", "none");
			$("#description").css("display","block");
			$("#fileinput").css("display","block");
			 document.getElementById("title").innerHTML = "<h1>" + data.name + "</h1>";
			 document.getElementById("deadline").innerHTML = "<h4>Tähtaeg: " + data.deadline + "</h4>";
			 document.getElementById("description").innerHTML = "<h4>" + data.description + "</h4>";
		});	
		jQuery.getJSON("tasktable?id=" + id, function(data) {
			tableCreate(data.fullname, data.time, data.result, data.language, data.attemptId);
	});
	}
	var result = getUrlVars()["result"];
	if (result !== undefined){
		if (result === "ok"){
			$("#resultOk").css("display", "block");
		}
		else if (result === "incorrect"){
			$("#resultIncorrect").css("display", "block");
		}
		else if (result === "toolarge"){
			$("#resultToolarge").css("display", "block");
		}
	}
});


function tableCreate(nameList, timeList, resultList, languageList, attemptIdList){
	var tableDiv = document.getElementById("attempts");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "tasksTable";
	var row = document.createElement("tr");
	jQuery("<th />").text("Nimi").appendTo(row);
	jQuery("<th />").text("Esitamise aeg").appendTo(row);
	jQuery("<th />").text("Tulemus").appendTo(row);
	jQuery("<th />").text("Lähtekood").appendTo(row);
	table.appendChild(row);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		jQuery("<td />").text(nameList[i]).appendTo(row);
		jQuery("<td />").text(timeList[i]).appendTo(row);
		jQuery("<td />").text(resultList[i]).appendTo(row);
		cell = document.createElement("td");
		link = document.createElement("a");
		link.setAttribute("href", "viewfile?id=" + attemptIdList[i]);
		link.innerHTML = "Python";
		cell.appendChild(link);
		row.appendChild(cell);
		table.appendChild(row);
	}
	table.setAttribute("class", "tableclass");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);
}

function getUrlVars() {
    var vars = {};
    window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
}