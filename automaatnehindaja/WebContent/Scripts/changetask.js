var id;
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

function addio(input, output){
	iopairs = iopairs + 1;
	buttoncount = buttoncount + 1;
	var iotable = document.getElementById("iotableBody");
	var row = document.createElement("tr");
	var cell = document.createElement("td");
	cell.innerHTML='<textarea id = "input' + iopairs + '" rows="2" cols="38" style="overflow: auto; resize:none">' +input + '</textarea>';
	row.appendChild(cell);
	cell = document.createElement("td");
	cell.innerHTML='<textarea id = "output' + iopairs + '" rows="2" cols="38" style="overflow: auto; resize:none">' + output + '</textarea>';
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
	$("#loader").css("display", "block");
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
			id: id,
			name: taskname.value,
			description: description.value,
			deadline: deadline.value,
			inputs:JSON.stringify(inputs),
			outputs:JSON.stringify(outputs)
	};
	console.log(variables);
	var request = $.post("changeTask", variables);
	request.done(function(){
		$("#loader").css("display", "none");
		message.innerHTML = "Ülesande muutmine õnnestus!";
	});
	
};

function getValues(){
	jQuery.getJSON("changeTask?id=" + id, function(data){
		document.getElementById("course").innerHTML = data.selectedcourse;
		$.getScript("Scripts/zebra_datepicker.js", function(){
			 $('#deadline').Zebra_DatePicker({direction: 1, format: "d-m-Y"});
			 $("#deadline").val(data.deadline);
		});
		$("#input0").html(data.inputs[0]);
		$("#output0").html(data.outputs[0]);
		taskname.value = data.name;
		description.value = data.description;
		for (var i = 1; i < data.inputs.length; i++){
			addio(data.inputs[i], data.outputs[i]);
		}
	});
};

function getUrlVars() {
    var vars = {};
    window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
    return vars;
}

function init(){
	courses = document.getElementById("courses");
	description = document.getElementById("desc");
	deadline = document.getElementById("deadline");
	taskname = document.getElementById("name");
	message = document.getElementById("message");
	id = getUrlVars()["id"];
	if (id === undefined){
		window.location = "error.html";
	}
	getValues();

}

init();