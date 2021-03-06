
var iopairs = 0;
var buttoncount = 0;
var courses = document.getElementById("courses");
var inputs;
var outputs;
var description;
var deadline;
var taskname;
var	message;



function openOverlay(){
	$("#overlay").css("display", "block");
}

function closeOverlay(){
	$("#overlay").css("display", "none");
}

function addio(){
	iopairs = iopairs + 1;
	buttoncount = buttoncount + 1;
	var iotable = document.getElementById("iotableBody");
	var row = document.createElement("tr");
	var cell = document.createElement("td");
	cell.innerHTML='<textarea id = "input' + iopairs + '" rows="2" cols="38" style="overflow: auto; resize:none"></textarea>';
	row.appendChild(cell);
	cell = document.createElement("td");
	cell.innerHTML='<textarea id = "output' + iopairs + '" rows="2" cols="38" style="overflow: auto; resize:none"></textarea>';
	row.appendChild(cell);
	cell = document.createElement("td");
	cell.innerHTML='<button id = "button' + buttoncount + '" onclick = deleteRow(' + buttoncount + ') style="vertical-align: middle;"> - </button>';
	row.appendChild(cell);
	iotable.appendChild(row);
}

function deleteRow(i){
	var iotable = document.getElementById("iotable");
	var rowIndex = document.getElementById("button" + i).parentNode.parentNode.rowIndex;
	console.log(rowIndex);
	iotable.deleteRow(rowIndex);
	iopairs = iopairs -1;
}

function checkFields(){
	var valid = true;
	var textareas = $('#iotable textarea');
	var textareaCount = textareas.length;
	for (var i = 0; i<textareaCount; i++){
		if (textareas[i].value === ""){
			valid = false;
			break;
			}
	}
	if (valid && taskname.value != "" && deadline.value != "" && description.value != ""){
		post();
	}
	else {
		message.innerHTML = "Palun täitke kõik väljad!";
	}
}

function post(){
	var inputs = {};
	var outputs = {};
	var textareas = $('#iotable textarea');
	var textareaCount = textareas.length;
	for (var i = 0; i<textareaCount; i){
		inputs[i/2] = textareas[i].value;
		outputs[i/2] = textareas[i+1].value;
		i = i + 2;
	}
	var variables ={
			course: courses.value,
			name: taskname.value,
			description: description.value,
			deadline: deadline.value,
			inputs:JSON.stringify(inputs),
			outputs:JSON.stringify(outputs)
	};
	console.log(variables);
	var request = $.post("addTask", variables);
	request.done(function(){
		message.innerHTML = "Ülesande lisamine õnnestus!";
	});
	
};

function getCourses(){
	jQuery.getJSON("getcoursenames", function(data){
		var courses = data.coursenames;
		for (var i = 0; i<courses.length; i++){
			var course = courses[i];
			$('#coursesCSV').append($("<option></option>").attr("value",course).text(course));
			$('#courses').append($("<option></option>").attr("value",course).text(course));
		};
	});
};

function init(){
	getCourses();
	$.getScript("Scripts/zebra_datepicker.js", function(){
		 $('#deadline').Zebra_DatePicker({direction: 1, format: "d-m-Y"});
	});
	 courses = document.getElementById("courses");
	 description = document.getElementById("desc");
	 deadline = document.getElementById("deadline");
	 taskname = document.getElementById("name");
	 console.log(taskname.value);
	 message = document.getElementById("message");
}

init();