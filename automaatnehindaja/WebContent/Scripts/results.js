
	
function changeCourse(){
	var course = $("#courses option:selected").val();
	var archived = $("#archived").is(":checked");
	jQuery.getJSON("resulttable?course=" + course + "&archived=" + archived, function(data) {
		$("#resultsLoader").css("display", "none");
		tableCreate(data.fullname, data.taskname, data.time, data.result, data.language, data.id, data.course, data.late);
	});	
};

function getCourses(){
	var currentSelected = $("#courses :selected").text();
	var archived = $("#archived").is(":checked");
	jQuery.getJSON("getcoursenames?archived=" + archived , function(data){
		var courses = data.coursenames;
		
		if (courses == null) {
			$("#resultsLoader").css("display", "none");
		}
		else {
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
			console.log("1");
			$("#courses option:contains(" + currentSelected + ")").prop("selected", true);
			console.log("2");
			changeCourse();
		}
	});
};

function init(){
	getCourses();
}

function tableCreate(nameList, tasknameList, timeList, resultList, languageList, idList, courseList, lateList){
	var tableDiv = document.getElementById("attempts");
	tableDiv.innerHTML = "";
	var table = document.createElement("table");
	table.setId = "attemptTable";
	var head = document.createElement("thead");
	var row = document.createElement("tr");
	jQuery("<th />").text("Nimi").appendTo(row);
	jQuery("<th />").text("Ülesanne").appendTo(row);
	jQuery("<th />").text("Esitatud").appendTo(row);
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
		jQuery("<td />").text(timeList[i]).appendTo(row);
		jQuery("<td />").text(resultList[i]).appendTo(row);
		jQuery("<td />").text(languageList[i]).appendTo(row);
		
		if (lateList[i] === "true")
			row.setAttribute("class", "lateRow");
		
		body.appendChild(row);
	}
	table.setAttribute("class", "tablesorter");
	table.setAttribute("id", "attemptTable");
	table.setAttribute("border", "1");
	tableDiv.appendChild(table);
	
	$.getScript("Scripts/jquery.tablesorter.min.js", function() {
		$("#attemptTable").tablesorter( { sortList: [[0,0]] } ); 
	});

	search();
}

function search() {
	$("#kwd_search").keyup(function(){
		if( $(this).val() != "") {
			$("#attemptTable tbody>tr").hide();
			$("#attemptTable td:contains-ci('" + $(this).val() + "')").parent("tr").show();
			//$("table#attemptTable tbody td:nth-child(1):contains-ci('" + $(this).val() + "')").parent("tr").show();
		}
		else {
			$("#attemptTable tbody>tr").show();
		}
	});

	// jQuery expression for case-insensitive filter
	$.extend($.expr[":"], 
	{
	    "contains-ci": function(elem, i, match, array) 
		{
			return (elem.textContent || elem.innerText || $(elem).text() || "").toLowerCase().indexOf((match[3] || "").toLowerCase()) >= 0;
		}
	});
}

init();
