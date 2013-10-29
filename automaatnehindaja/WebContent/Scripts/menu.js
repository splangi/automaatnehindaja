$(document).ready(function() {
	$.get("getRole", function(data){
		console.log(data);
		if (data == "admin"){
			var logid = "<li><a href='#logs'><span>Logid</span></a></li>";
			var kursusehaldus = "<li id = 'coursechoices' class='has-sub'><a href='#addCourse'><span>Kursuste haldus</span></a>" +
			"<ul>" + 
			"<li><a href='#addCourse'><span>Lisa kursus</span></a></li>" + 
			"<li class='last'><a href='#'><span>Kursuse sulgemine</span></a></li>" +
		"</ul></li>";
			$(logid).insertAfter("#afterThis");
			$(kursusehaldus).insertAfter("#afterThis");
		}
		if (data == "responsible" || data == "admin"){
			var kasutajahaldus = "<li id = 'userchoices' class='has-sub'><a><span>Kasutajate haldus</span></a>" + 
					"<ul>" +
						"<li><a href='#addUserManually'><span>Kasutaja lisamine</span></a></li>" +
						"<li><a href='#addUsersCSV'><span>Automaatne lisamine</span></a></li>" +
						"<li class = 'last'><a href='#addUserToCourse'><span>Lisa kasutaja kursusele</span></a></li>" +
					"</ul></li>";
			var ulesannetehaldus = "<li id = 'taskchoices' class='has-sub'><a href='#addTask'><span>Ülesannete haldus</span></a>" + 
					"<ul>" +
						"<li><a href='#addTask'><span>Lisa ülesanne</span></a></li>" +
						"<li><a href='#changeTask'><span>Muuda ülesanne</span></a></li>" +
						"<li class='last'><a href='#closeTask'><span>Ülesannete arhiveerimine</span></a></li>" +
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
		window.history.pushState(null, null, "mainpage.html#main");
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
		$('#userchoice').trigger("click");		
	}
	else if (page == "#addUsersCSV"){
		$("#content").load("html/addusercsv.html");
		$.getScript("Scripts/adduser.js");
		$('#userchoice').trigger("click");
	}
	else if (page == "#addTask"){
		$("#content").load("html/addTask.html");
		$.getScript("Scripts/addtask.js");
		$('#taskchoice').trigger("click");
	}
	else if (page == "#addCourse"){
		$("#content").load("html/addCourse.html");
		$.getScript("Scripts/addCourse.js");
		$('#coursechoice').trigger("click");
	}
	else if (page.indexOf("#taskview") != -1){
		$("#content").load("html/taskview.html");
		$.getScript("Scripts/taskview.js");
	}
}