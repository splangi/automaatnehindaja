var courseselection;
var roleselection;
var checkbox;
var passwordField;
var passwordRepeat;
var username;
var fullname;
var warningtext;
var studentId;

jQuery(document).ready(function(){
	studentId = document.getElementById("studentid");
	courseselection = document.getElementById("courses");
	roleselection = document.getElementById("role");
	checkbox = document.getElementById("generate");
	passwordField = document.getElementById("secret");
	passwordRepeat = document.getElementById("secretrepeat");
	username = document.getElementById("username");
	fullname = document.getElementById("fullname");
	warningtext = document.getElementById("warning");
	
});

function manualsubmit(){
	var validSubmission = true;
	if ((studentId.value == "" || studentId.value.length != 6) && roleselection.selectedIndex == 2){
		color(studentId);
		warningtext.innerHTML = "  Ebakorrektne matriklinumber";
		validSubmission = false;
	}
	else{
		uncolor(studentId);
	}
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
	if (courseselection.selectedIndex == 0){
		color(courseselection);
		warningtext.innerHTML = "  Palun valige õppeaine";
		validSubmission = false;
	}
	else{
		uncolor(courseselection);
	}	
	if (validSubmission){
		warningtext.innerHTML = "";
		$("#loader").css("display", "block");
		post();
	}
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
		$("#loader").css("display", "none");
		if (request.getResponseHeader("exists") === "true"){
			warningtext.innerHTML = "  Antud emailiga kasutaja on juba olemas";
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
		$("#loader").css("display", "none");
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