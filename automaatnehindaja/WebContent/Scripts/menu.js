$(document).ready(function() {
	$.get("getRole", function(data){
		console.log(data);
		if (data == "admin"){
			var kursusehaldus = "<li class='has-sub'><a href='#addCourse'><span>Kursuste haldus</span></a>" +
			"<ul>" + 
			"<li><a href='#addCourse'><span>Lisa kursus</span></a></li>" + 
			"<li class='last'><a href='#'><span>kursuse sulgemine</span></a></li>" +
		"</ul></li>";
			$(kursusehaldus).insertAfter("#afterThis");
		}
		if (data == "responsible" || data == "admin"){
			var kasutajahaldus = "<li class='has-sub'><a><span>Kasutajate haldus</span></a>" + 
					"<ul>" +
						"<li><a href='#addUserManually'><span>Kasutaja lisamine</span></a></li>" +
						"<li><a href='#addUsersCSV'><span>Automaatne lisammine</span></a></li>" +
						"<li class = 'last'><a href='#addUserToCourse'><span>Lisa kasutaja kursusele</span></a></li>" +
					"</ul></li>";
			var ulesannetehaldus = "<li class='has-sub'><a href='#addTask'><span>Ülesannete haldus</span></a>" + 
					"<ul>" +
						"<li><a href='#addTask'><span>Lisa ülesanne</span></a></li>" +
						"<li class='last'><a href='#closeTask'><span>Ülesannete sulgemine</span></a></li>" +
					"</ul></li>";
			$(ulesannetehaldus).insertAfter("#afterThis");
			$(kasutajahaldus).insertAfter("#afterThis");
		}
		
		$('#cssmenu ul ul li:odd').addClass('odd');
		$('#cssmenu ul ul li:even').addClass('even');
		$('#cssmenu > ul > li > a').click(function() {
			$('#cssmenu li').removeClass('active');
			$(this).closest('li').addClass('active');
			var checkElement = $(this).next();
			if ((checkElement.is('ul')) && (checkElement.is(':visible'))) {
				$(this).closest('li').removeClass('active');
				checkElement.slideUp('normal');
			}
			if ((checkElement.is('ul')) && (!checkElement.is(':visible'))) {
				$('#cssmenu ul ul:visible').slideUp('normal');
				checkElement.slideDown('normal');
			}
			if ($(this).closest('li').find('ul').children().length == 0) {
				return true;
			} else {
				return false;
			}
		});
	});
	$(window).hashchange(function() {
		var hash = location.hash;
		load(hash);
	});
	var hash = location.hash;
	if (hash == ""){
		hash = "#main";
	}
	load(hash);
	
});

function changeActive(){
	
}

function load(page){
	if (page.indexOf("#tasksview") != -1){
		$("#content").load("html/tasksview.html");
		$.getScript("Scripts/tasksview.js");
		$('a[href$="#tasksview"]').trigger("click");
	}
	else if (page == "#main"){
		$("#content").load("html/main.html");
		$('a[href$="#main"]').trigger("click");
	}
	else if (page == "#changepass"){
		$("#content").load("html/change_pass.html");
		$.getScript("Scripts/changepass.js");
		$('a[href$="#changepass"]').trigger("click");
	}
	else if (page == "#results"){
		$("#content").load("html/results.html");
		$.getScript("Scripts/results.js");
		$('a[href$="#results"]').trigger("click");
	}
	else if (page == "#addUserManually"){
		$("#content").load("html/addusermanually.html");
		$.getScript("Scripts/adduser.js");
		$('a[href$="#addUserManually"]').trigger("click");
	}
	else if (page == "#addUsersCSV"){
		$("#content").load("html/addusercsv.html");
		$.getScript("Scripts/adduser.js");
		$('a[href$="#addUsersCSV"]').trigger("click");
	}
	else if (page == "#addTask"){
		$("#content").load("html/addTask.html");
		$.getScript("Scripts/addtask.js");
		$('a[href$="#addTask"]').trigger("click");
	}
	else if (page.indexOf("#taskview") != -1){
		$("#content").load("html/taskview.html");
		$.getScript("Scripts/taskview.js");
	}
}