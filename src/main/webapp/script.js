async function add() {
  fetch('/send-task').then(response => response.json()).then((tasksList) => {
    container = document.getElementById('task-container');
    console.log(tasksList);
    container.innerText = '';
    for (const task of tasksList) {
        container.appendChild(createListElement(task));
    }
  });
}

function createListElement(task) {
    const liElement = document.createElement("li");
    liElement.innerHTML = '<div id="task"><div id= "title">' + task.taskText.title
    + '</div><div id= "time">' + task.time.date
    + '</div><div id= "place">' + task.place.string + '</div></div>'
    +  '</div><div id= "comment">' + task.taskText.comment + '</div></div>'
    return liElement;
}

