async function add() {
  fetch('/load-task').then(response => response.json()).then((tasksList) => {
    container = document.getElementById('task-container');
    console.log(tasksList);
    container.innerText = '';
    id = 0;
    for (const task of tasksList) {
        container.appendChild(createListElement(task, id));
        id += 1;
    }
  });
}

function createListElement(task, id) {
    idS = id.toString();
    const liElement = document.createElement("li");
    liElement.innerHTML = '<div class="task" id="task' + idS + '">'
    + '<div id= "title">' + task.taskText.title
    + '</div><div id= "time">' + task.time.date
    + '</div><div id= "place" contentEditable="false">' + task.place.string 
    + '</div><div id= "comment" contentEditable="false">' + task.taskText.comment + '</div></div>' 
    + '<div id="placeforform'+idS+'" class="okButton"></div>'
    + '<button type="button" class="deleteButton" onclick="deleteTask('+ idS + ')">Delete</button>'
    + '<button type="button" class="editButton" onclick="edit('+ idS + ')">Edit</button>';
    
    return liElement;
}
function edit(idS) {
    taskIds = "#task"+idS;
    bodytext = "id="+idS;
    var okButton = document.getElementById("placeforform"+idS);
    var title = document.querySelector(taskIds+" #title");
    title.contentEditable = "true";
    var place = document.querySelector(taskIds+" #place");
    place.contentEditable = "true";
    var time = document.querySelector(taskIds+" #time");
    time.contentEditable = "true";
    var comment = document.querySelector(taskIds+" #comment");
    comment.contentEditable = "true";
    okButton.innerHTML = "<button onclick ='editTask("+idS+")'>Ok</button>";
    console.log(idS);
}

async function editTask(idS) {
    taskIds = "#task"+idS;
    var title = document.querySelector(taskIds+" #title").innerText;
    var place = document.querySelector(taskIds+" #place").innerText;
    var date = document.querySelector(taskIds+" #time").innerText;
    var comment = document.querySelector(taskIds+" #comment").innerText;
    bodyText = "id="+idS +"&task-text="+title + "&task-date="+date+"&task-place="+place + "&task-comment="+comment;
    console.log(bodyText)

    await fetch('/edit-task', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: bodyText
  });
    add();
}


async function deleteTask(curid) {
    bodyText = "id=" + curid.toString();
    console.log(curid)
    console.log(bodyText)
    await fetch('/delete-task', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: bodyText
  });
    add();
}