window.onload = function(){
	var id = getUrlVars()["id"];
	if (id === undefined){
		window.location = "error.html";
	}
	else{
		document.getElementById("uploadform").setAttribute("action", "upload?id=" + id);
		jQuery.getJSON("task?id=" + id, function(data) {
			 document.getElementById("title").innerHTML = "<h1>" + data.name + "</h1>";
			 document.getElementById("deadline").innerHTML = "<h4>T�htaeg: " + data.deadline + "</h4>";
			 document.getElementById("description").innerHTML = "<h4>" + data.description + "</h4>";
		});	
		jQuery.getJSON("tasktable?id=" + id, function(data) {
			tableCreate(data.fullname, data.time, data.result, data.language);
	});
	}
	var result = getUrlVars()["result"];
	console.log(result);
	if (result !== undefined){
		if (result === "ok"){
			document.getElementById("resultOk").hidden = "";
		}
		else if (result === "incorrect"){
			document.getElementById("resultIncorrect").hidden = "";
		}
		else if (result === "toolarge"){
			document.getElementById("resultToolarge").hidden = "";
		}
	}
};

function tableCreate(nameList, timeList, resultList, languageList){
	var tableDiv = document.getElementById("attempts");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "tasksTable";
	var row = document.createElement("tr");
	jQuery("<th />").text("Nimi").appendTo(row);
	jQuery("<th />").text("Esitamise aeg").appendTo(row);
	jQuery("<th />").text("Tulemus").appendTo(row);
	//If we add support for other languages
	//jQuery("<th />").text("Programeerimiskeel").appendTo(row);
	table.appendChild(row);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		jQuery("<td />").text(nameList[i]).appendTo(row);
		jQuery("<td />").text(timeList[i]).appendTo(row);
		jQuery("<td />").text(resultList[i]).appendTo(row);
		//jQuery("<td />").text(languageList[i]).appendTo(row);
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