$(document).ready(function() {
	$.get("getRole", function(data){
		if (data == "admin"){
			var logid = "<li><a href='#logs'><span>Logid</span></a></li>";
			var kursusehaldus = "<li id = 'coursechoices' class='has-sub'><a href='#addCourse'><span>Kursuste haldus</span></a>" +
			"<ul>" + 
			"<li><a href='#addCourse'><span>Lisa kursus</span></a></li>" + 
			"<li class='last'><a href='#closeCourse'><span>Kursuse sulgemine</span></a></li>" +
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
			var ulesannetehaldus = "<li id = 'taskchoices' class='has-sub'><a><span>Ülesannete haldus</span></a>" + 
					"<ul>" +
						"<li><a href='#addTask'><span>Lisa ülesanne</span></a></li>" +
						"<li><a href='#changeTask'><span>Muuda ülesanne</span></a></li>" +
						"<li><a href='#closeAttempts'><span>Soorituste arhiveerimine</span></a></li>" +
						"<li class='last'><a href='#closeTasks'><span>Ülesannete arhiveerimine</span></a></li>" +
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
			if (!$(this).next().is("ul")){
				$('#cssmenu ul ul:visible').slideUp('normal');
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
	$("#cssmenu").css("display", "block");
	var hash = location.hash;
	if (hash == ""){
		hash = "#main";
		window.history.pushState(null, null, "mainpage.html#main");
	}
	load(hash);
	
});

$.ajaxSetup({
	  cache: true
	});

function load(page){
	if (page.indexOf("#tasksview") != -1){
		$("#content").load("html/tasksview.html", function(){
			$.getScript("Scripts/tasksview.js");
		});
		
		$('a[href$="#tasksview"]').trigger("click");
	}
	else if (page == "#changeTask"){
		$("#content").load("html/tasksview.html", function(){
			$.getScript("Scripts/tasksview.js");
		});
		$("#taskchoices a").trigger("click");
	}
	else if (page.indexOf("#changeTaskView") != -1){
		$("#content").load("html/changeTask.html", function(){
			$.getScript("Scripts/changetask.js");
		});
		$("#taskchoices a").trigger("click");
	}
	else if (page == "#main"){
		$("#content").load("html/main.html");
		$('a[href$="#main"]').trigger("click");
	}
	else if (page == "#changepass"){
		$("#content").load("html/change_pass.html");
		$('a[href$="#changepass"]').trigger("click");
	}
	else if (page == "#results"){
		$("#content").load("html/results.html", function(){
			$.getScript("Scripts/results.js");
		});
		$('a[href$="#results"]').trigger("click");
	}
	else if (page == "#addUserManually"){
		$("#content").load("html/addusermanually.html", function(){
			$.getScript("Scripts/adduser.js");
		});
		
		$('#userchoices a').trigger("click");		
	}
	else if (page == "#addUsersCSV"){
		$("#content").load("html/addusercsv.html", function(){
			$.getScript("Scripts/adduser.js");
		});
		
		$('#userchoices a').trigger("click");
	}
	else if (page == "#addTask"){
		$("#content").load("html/addTask.html", function(){
			$.getScript( "Scripts/addtask.js" );
		});
		$('#taskchoices a').trigger("click");
	}
	else if (page == "#closeAttempts"){
		$("#content").load("html/closeAttempts.html", function(){
			$.getScript( "Scripts/closeAttempts.js" );
		});
		$('#taskchoices a').trigger("click");
	}
	else if (page == "#closeTasks"){
		$("#content").load("html/closeTasks.html", function(){
			$.getScript( "Scripts/closeTasks.js" );
		});
		$('#taskchoices a').trigger("click");
	}
	else if (page == "#addCourse"){
		$("#content").load("html/addCourse.html", function(){
			$.getScript("Scripts/addCourse.js");
		});
		
		$('#coursechoices a').trigger("click");
	}
	else if (page == "#logs"){
		$("#content").load("html/serverlogs.html", function(){
			$.getScript("Scripts/serverlogs.js");
		});
		$('#logs a').trigger("click");
	}
	else if (page == "#addUserToCourse"){
		$("#content").load("html/addusertocourse.html", function(){
			$.getScript("Scripts/addusertocourse.js");
		});
		$("#userchoices a").trigger("click");
	}
	else if (page == "#closeCourse"){
		$("#content").load("html/closeCourse.html", function(){
			$.getScript("Scripts/closeCourse.js");
		});
		$("#coursechoices a").trigger("click");
	}
	else if (page.indexOf("#taskview") != -1){
		$("#content").load("html/taskview.html", function(){
			$.getScript( "Scripts/taskview.js" );
		});
		$('a[href$="#taskview"]').trigger("click");
	}
}



