$(document).ready(initialLoad());

function initialLoad(){
	getCourses();
}

function changeCourse(){
	fillUpTasks($("#courses option:selected").val());
}

function fillUpTasks(course){
	jQuery.getJSON("Taskstable?course=" +course, function(data) {
		$("#tasksViewLoader").css("display", "none");
		if (data.role == "tudeng"){
			tableCreate(data.id, data.name, data.deadline, data.result);
		}
		else if (data.role == "admin" || data.role == "responsible"){
			tableCreate2(data.id, data.name, data.deadline, data.resultCount, data.successCount);
		}
	});	
}

function getCourses(){
	jQuery.getJSON("getcoursenames", function(data){
		var courses = data.coursenames;
		if (courses.length > 0){
			var course = getUrlVars()["course"];
			if (course != undefined){
				fillUpTasks(course);
			}
			fillUpTasks(courses[0]);
		}
		for (var i = 0; i<courses.length; i++){
			var course = courses[i];
			$('#courses').append($("<option></option>").attr("value",course).text(course));
		};
	});
};

function tableCreate(idList, nameList, deadlineList, resultList){
	var tableDiv = document.getElementById("tableDiv");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "tasksTable";
	var row = document.createElement("tr");
	jQuery("<th />").text("Ülesanne").appendTo(row);
	jQuery("<th />").text("Tähtaeg").appendTo(row);
	jQuery("<th />").text("Tulemus").appendTo(row);
	table.appendChild(row);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		var cell = document.createElement("td");
		cell.innerHTML = '<a href="#taskview.html?id=' + idList[i] + '">' + nameList[i] + "</a>";
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
	jQuery("<th />").text("Ülesanne").appendTo(row);
	jQuery("<th />").text("Tähtaeg").appendTo(row);
	jQuery("<th />").text("Esitanuid").appendTo(row);
	jQuery("<th />").text("Õnnestujaid").appendTo(row);
	table.appendChild(row);
	for (var i = 0; i < nameList.length; i++){
		row = document.createElement("tr");
		var cell = document.createElement("td");
		cell.innerHTML = '<a href = "#taskview?id=' + idList[i] + '">' + nameList[i] + "</a>";
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

function getUrlVars() {
    var vars = {};
    window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
}
// Reference: http://stackoverflow.com/questions/5999118/add-or-update-query-string-parameter
function UpdateQueryString(key, value, url) {
    if (!url) url = window.location.href;
    var re = new RegExp("([?|&])" + key + "=.*?(&|#|$)(.*)", "gi");

    if (re.test(url)) {
        if (typeof value !== 'undefined' && value !== null)
            return url.replace(re, '$1' + key + "=" + value + '$2$3');
        else {
            var hash = url.split('#');
            url = hash[0].replace(re, '$1$3').replace(/(&|\?)$/, '');
            if (typeof hash[1] !== 'undefined' && hash[1] !== null) 
                url += '#' + hash[1];
            return url;
        }
    }
    else {
        if (typeof value !== 'undefined' && value !== null) {
            var separator = url.indexOf('?') !== -1 ? '&' : '?',
                hash = url.split('#');
            url = hash[0] + separator + key + '=' + value;
            if (typeof hash[1] !== 'undefined' && hash[1] !== null) 
                url += '#' + hash[1];
            return url;
        }
        else
            return url;
    }
}