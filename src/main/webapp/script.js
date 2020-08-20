async function add() {
  fetch('/task').then(response => response.json()).then((tasksList) => {
    let container = document.getElementById('tasks-container');
    container.innerText = '';
    for (const task of tasksList) {
        container.appendChild(createListElement(task));
    }
  });
}

function createListElement(task) {
    const liElement = document.createElement("li");
    liElement.innerHTML = '<p id= "title">' + task.getTaskText().getTitle()
    + '<p id= "time">' + task.getTime().getTimeAsString()
    + '<p id= "place">' + task.getPlace().getString()
    +  '<p id= "comment">' + task.getTaskText().getComment();
    return liElement;
}

