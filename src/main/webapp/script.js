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
    liElement.innerHTML = '<p id= "title">' + task.taskText.title
    + '<p id= "time">' + task.time.date
    +  '<p id= "comment">' + task.taskText.comment
    return liElement;
}

