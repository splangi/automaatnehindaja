var id;

function init(){
	id = getUrlVars()["id"];
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
			 document.getElementById("deadline").innerHTML = "<h4>Tähtaeg: " + data.deadline.substring(0,16) + "</h4>";
			 document.getElementById("description").innerHTML = data.description.replace(/\n/g, "<br />");
			 
			 console.log("active: " + data.active);
			 if (data.active === "1") {
					$("#fileinput").css("display","block");
			 }
			 else
				 $("#fileinput").css("display","none");
		});	
		fillUpTaskTable();
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
}

function fillUpTaskTable(){
	jQuery.getJSON("tasktable?id=" + id + "&archived="+$("#archived").is(":checked"), function(data) {
		tableCreate(data.fullname, data.time, data.result, data.language, data.attemptId, data.role, data.late);
		document.getElementById("taskViewLoader").display = "none";
});
}


function tableCreate(nameList, timeList, resultList, languageList, attemptIdList, role, lateList){
	var tableDiv = document.getElementById("attempts");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "tasksTable";
	var head = document.createElement("thead");
	var row = document.createElement("tr");
	jQuery("<th />").text("Nimi").appendTo(row);
	jQuery("<th />").text("Esitamise aeg").appendTo(row);
	jQuery("<th />").text("Tulemus").appendTo(row);
	jQuery("<th />").text("Lähtekood").appendTo(row);
	if (role === "admin"){
		jQuery("<th />").text("Väljundid").appendTo(row);
	}
	table.appendChild(head);
	head.appendChild(row);
	var body = document.createElement("tbody");
	table.appendChild(body);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		jQuery("<td />").text(nameList[i]).appendTo(row);
		jQuery("<td />").text(timeList[i]).appendTo(row);
		jQuery("<td />").text(resultList[i]).appendTo(row);
		cell = document.createElement("td");
		link = document.createElement("a");
		link.setAttribute("href", "viewfile?id=" + attemptIdList[i]);
		link.innerHTML = languageList[i];
		cell.appendChild(link);
		row.appendChild(cell);
		if (role === "admin"){
			cell = document.createElement("td");
			link = document.createElement("a");
			link.setAttribute("href", "viewoutput?id=" + attemptIdList[i]);
			link.innerHTML = "Vaata";
			cell.appendChild(link);
			row.appendChild(cell);
		}
		
		if (lateList[i] === "true")
			row.setAttribute("class", "lateRow");
		
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