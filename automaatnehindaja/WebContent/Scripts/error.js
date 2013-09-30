var count=5;

var counter=setInterval(timer, 1000); //1000 will  run it every 1 second

function timer()
{
  count=count-1;
  if (count <= 0)
  {
     clearInterval(counter);
     window.location = "tasksview.html"
     return;
  }

  document.getElementById("seconds").innerHTML=count;
}