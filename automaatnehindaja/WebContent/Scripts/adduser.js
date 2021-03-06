var courseselection;
var courseselectionCSV;
var roleselection;
var checkbox;
var passwordField;
var passwordRepeat;
var username;
var fullname;
var warningtext;
var studentId;

function init(){
	studentId = document.getElementById("studentid");
	courseselection = document.getElementById("courses");
	roleselection = document.getElementById("role");
	checkbox = document.getElementById("generate");
	passwordField = document.getElementById("secret");
	passwordRepeat = document.getElementById("secretrepeat");
	username = document.getElementById("username");
	fullname = document.getElementById("fullname");
	warningtext = document.getElementById("warning");
	courseselectionCSV = document.getElementById("coursesCSV");	
	jQuery.getJSON("getcoursenames", function(data){
		var courses = data.coursenames;
		for (var i = 0; i<courses.length; i++){
			var course = courses[i];
			$('#coursesCSV').append($("<option></option>").attr("value",course).text(course));
			$('#courses').append($("<option></option>").attr("value",course).text(course));
		}
		if (data.role === "admin"){
			$('#role').append($("<option></option>").attr("value",data.role).text(data.role));
		}
	});
	
}



function manualsubmit(){
	var validSubmission = true;
	if (fullname.value == "" || fullname.value.length < 5){
		color(fullname);
		warningtext.innerHTML = "  Ebakorrektne täisnimi";
		validSubmission = false;
	}
	else{
		uncolor(fullname);
	}
	if ((passwordField.value!=passwordRepeat.value) || (passwordRepeat.value.length < 6 && !checkbox.checked)){
		color(passwordRepeat);
		warningtext.innerHTML = "  Paroolid ei kattu";
		validSubmission = false;
	}
	else{
		uncolor(passwordRepeat);
	}
	if ((passwordField.value.length < 6) && (!checkbox.checked)){
		color(passwordField);
		warningtext.innerHTML = "  Parooli pikkus peab olema vähemalt 6 tähemärki";
		validSubmission = false;
	}
	else {
		uncolor(passwordField);
	}
	if ((username.value == "") || (username.value.indexOf("@") == -1)){
		color(username);
		warningtext.innerHTML = "  Ebakorrektne emaili aadress";
		validSubmission = false;
	}
	else{
		uncolor(username);
	}
	if (roleselection.selectedIndex == 0){
		color(roleselection);
		warningtext.innerHTML = "  Palun valige isiku roll õppeaines";
		validSubmission = false;
	}
	else{
		uncolor(roleselection);
	}
	if (validSubmission){
		warningtext.innerHTML = "";
		unhide("loader");
		post();
	}
}

function hide(targetid){
	$("#"+targetid).css("display", "none");
}

function unhide(targetid){
	$("#"+targetid).css("display", "block");
}

function post(){
	var studentidvalue = "";
	if (roleselection.selectedIndex == 2){
		studentidvalue = studentId.value;
	}
	var variables = {
			username: username.value,
			fullname: fullname.value,
			course: courseselection.value,
			role: roleselection.value,
			autogenerate: checkbox.checked,
			password: passwordField.value,
			studentid: studentidvalue,
		};
	
	var request = jQuery.post("addusermanually", variables);
	request.done(function(){
		hide("loader");
		if (request.getResponseHeader("exists") === "true"){
			warningtext.innerHTML = "  Antud emailiga kasutaja on juba olemas, lisage õpilane hoopis kursusele";
			color(username);
		}
		else if (request.getResponseHeader("error") === "true"){
			warningtext.innerHTML = "  Kasutaja loomine ebaõnnestus";
		}
		else {
			warningtext.innerHTML = "  Kasutaja loomine õnnestus";
		}
	});
	request.fail(function(){
		hide("loader");
		warningtext.innerHTML = "  Kasutaja loomine ebaõnnestus";
	});
}

function uncolor(target){
	target.style.backgroundColor="";
}

function color(target){
	target.style.backgroundColor="#F6CECE";
}

function change(){
	var checkbox = document.getElementById("generate");
	var passwordField = document.getElementById("secret");
	var passwordRepeat = document.getElementById("secretrepeat");
	if (checkbox.checked){
		passwordField.value = "";
		passwordRepeat.value = "";
		uncolor(passwordRepeat);
		uncolor(passwordField);
		passwordField.setAttribute("disabled", "disabled");
		passwordRepeat.setAttribute("disabled", "disabled");
	}
	else {
		passwordField.removeAttribute("disabled");
		passwordRepeat.removeAttribute("disabled");
	}
}

function blackOutStudentId(){
	var selection = document.getElementById("role");
	var studentId = document.getElementById("studentid");
	if (selection.selectedIndex == 2){
		studentId.removeAttribute("disabled");
	}
	else{
		studentId.value="";
		uncolor(studentId);
		studentId.setAttribute("disabled", "disabled");
	}
}

init();